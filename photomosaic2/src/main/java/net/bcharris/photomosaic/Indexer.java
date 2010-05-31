package net.bcharris.photomosaic;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

/**
 * Builds the index file.
 */
public class Indexer {

    public Index index(File sourceImageDirectory, final File convertApp, final int width, final int height) {
        SourceImageFinder sourceImageFinder = new SourceImageFinder();
        List<File> sourceImages = sourceImageFinder.findSourceImages(sourceImageDirectory);

        final ThreadLocal<File> tmpFiles = new ThreadLocal<File>() {

            @Override
            protected File initialValue() {
                File f = Util.createTempFile("photomosaic", ".jpg");
                f.deleteOnExit();
                return f;
            }
        };
        final ArrayList<Index.Image> images = new ArrayList<Index.Image>();
        ThreadedIteratorProcessor<File> threadedIteratorProcessor = new ThreadedIteratorProcessor<File>();
        threadedIteratorProcessor.processIterator(sourceImages.iterator(), new ThreadedIteratorProcessor.ElementProcessor<File>() {

            @Override
            public void processElement(File sourceImage) {
                File tmpFile = tmpFiles.get();
                CommandLine commandLine = new CommandLine(convertApp.getAbsolutePath());
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
                Log.log((images.size() + 1) + ":" + commandLine.toString());
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
                        images.add(new Index.Image(Files.toByteArray(tmpFile), sourceImage.getAbsolutePath()));
                    }
                } catch (IOException ex) {
                    throw new RuntimeException("Fatal error, could not read from temporary file: " + tmpFile.getAbsolutePath(), ex);
                }
            }
        });
        return new Index(images, width, height);
    }
}
