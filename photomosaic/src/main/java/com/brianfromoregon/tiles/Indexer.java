package com.brianfromoregon.tiles;

import com.brianfromoregon.tiles.Index.Image;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Builds the index file.
 */
public class Indexer {

    private final Index current;

    /**
     * @param current May be null
     */
    public Indexer(Index current) {
        this.current = current;
    }

    public Index index(Iterable<File> sourceImageDirectories, final Set<File> excludes, final int width, final int height) {
        SourceImageFinder sourceImageFinder = new SourceImageFinder(excludes);
        Set<File> imagesToProcess = sourceImageFinder.findSourceImages(sourceImageDirectories);
        final List<Image> reusable;
        if (current != null && current.width == width && current.height == height) {
            reusable = new ArrayList<>(current.images);
        } else {
            reusable = new ArrayList<>();
        }
        final List<File> reusableFiles = Lists.transform(reusable, new Function<Image, File>() {
            @Override public File apply(Image input) {
                return input.file;
            }
        });

        // Trash old images that may be excluded, no longer exist, or not be included.
        reusableFiles.retainAll(imagesToProcess);
        // Reuse the rest
        imagesToProcess.removeAll(reusableFiles);

        final ImmutableList.Builder<Image> images = ImmutableList.builder();
        images.addAll(reusable);
        if (!imagesToProcess.isEmpty()) {
            final ThreadLocal<File> tmpFiles = new ThreadLocal<File>() {

                @Override
                protected File initialValue() {
                    File f = Util.createTempFile("tiles", ".jpg");
                    f.deleteOnExit();
                    return f;
                }
            };
            final ProgressCalc progressCalc = new ProgressCalc(imagesToProcess.size());
            final AtomicInteger counter = new AtomicInteger();
            ThreadedIteratorProcessor<File> threadedIteratorProcessor = new ThreadedIteratorProcessor<File>();
            threadedIteratorProcessor.processIterator(imagesToProcess.iterator(), new ThreadedIteratorProcessor.ElementProcessor<File>() {

                @Override
                public void processElement(File sourceImage) {
                    File tmpFile = tmpFiles.get();
                    CommandLine commandLine = new CommandLine(ImageMagick.convertExe().getAbsolutePath());
                    commandLine.addArgument(sourceImage.getAbsolutePath());
                    commandLine.addArgument("-auto-orient");
                    commandLine.addArgument("-strip");
                    commandLine.addArgument("-resize");
                    commandLine.addArgument(width + "x" + height + "^");
                    commandLine.addArgument("-gravity");
                    commandLine.addArgument("north");
                    commandLine.addArgument("-extent");
                    commandLine.addArgument(width + "x" + height);
                    commandLine.addArgument("JPEG:" + tmpFile.getAbsolutePath());
                    int imageNum = counter.incrementAndGet() + 1;
                    Log.log(imageNum + ":" + commandLine.toString());
                    DefaultExecutor executor = new DefaultExecutor();
                    PumpStreamHandler handler = new PumpStreamHandler(System.out, System.out);
                    executor.setStreamHandler(handler);
                    try {
                        executor.execute(commandLine);
                    } catch (Throwable ex) {
                        Log.log("Problem shrinking image '%s'\n", sourceImage.getAbsolutePath());
                        ex.printStackTrace(System.out);
                        return;
                    }
                    try {
                        synchronized (images) {
                            images.add(new Image(Files.toByteArray(tmpFile), sourceImage));
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException("Fatal error, could not read from temporary file: " + tmpFile.getAbsolutePath(), ex);
                    }
                    if (imageNum % 20 == 0) {
                        Log.log("%d%% complete, ETA=%s", progressCalc.percentInt(imageNum), progressCalc.eta(imageNum));
                    }
                }
            });
        }
        return new Index(images.build(), width, height);
    }
}
