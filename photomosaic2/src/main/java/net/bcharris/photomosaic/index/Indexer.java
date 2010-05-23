package net.bcharris.photomosaic.index;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.bcharris.photomosaic.create.Util;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

/**
 * Builds the index file.
 */
public class Indexer {

    public static void main(String[] args) {
        final File convertApp = new File("E:\\Program Files\\ImageMagick-6.5.6-Q16\\convert.exe");
//        File sourceImageDirectory = new File("E:\\Documents and Settings\\brian\\Desktop");
        File sourceImageDirectory = new File("E:\\dev\\photomosaic\\image-gen\\colors_16x11");
        final int width = 100;
        final int height = 75;


        long before = System.currentTimeMillis();
        SourceImageFinder sourceImageFinder = new SourceImageFinder();
        List<File> sourceImages = sourceImageFinder.findSourceImages(sourceImageDirectory);

        final ThreadLocal<File> tmpFiles = new ThreadLocal<File>() {

            @Override
            protected File initialValue() {
                File f = Util.createTempFile("photomosaic", "jpg");
                f.deleteOnExit();
                return f;
            }
        };
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        final List<byte[]> jpegs = Collections.synchronizedList(new ArrayList<byte[]>());
        for (final File sourceImage : sourceImages) {
            executorService.submit(new Runnable() {

                @Override
                public void run() {
                    File tmpFile = tmpFiles.get();
                    CommandLine commandLine = new CommandLine(convertApp.getAbsolutePath());
                    commandLine.addArgument(sourceImage.getAbsolutePath());
                    commandLine.addArgument("-auto-orient");
                    commandLine.addArgument("-strip");
                    commandLine.addArgument("-resize");
                    commandLine.addArgument(width + "x" + height + "^");
                    commandLine.addArgument("-gravity");
                    commandLine.addArgument("center");
                    commandLine.addArgument("-extent");
                    commandLine.addArgument(width + "x" + height);
                    commandLine.addArgument("JPEG:" + tmpFile.getAbsolutePath());
                    System.out.println(commandLine.toString());
                    DefaultExecutor executor = new DefaultExecutor();
                    PumpStreamHandler handler = new PumpStreamHandler(System.out, System.out);
                    executor.setStreamHandler(handler);
                    try {
                        executor.execute(commandLine);
                    } catch (Exception ex) {
                        System.out.println(String.format("Problem shrinking image '%s'\n", sourceImage.getAbsolutePath()));
                        ex.printStackTrace(System.out);
                        return;
                    }
                    try {
                        jpegs.add(Files.toByteArray(tmpFile));
                    } catch (IOException ex) {
                        throw new RuntimeException("Fatal error, could not read from temporary file: " + tmpFile.getAbsolutePath(), ex);
                    }
                }
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            throw new RuntimeException("Fatal error, interrupted while processing source images", ex);
        }
        File indexFile = Util.createTempFile("photomosaic", "index");
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(indexFile));
            out.writeObject(new Index(ImmutableList.copyOf(jpegs), width, height));
            out.close();
        } catch (IOException ex) {
            throw new RuntimeException("Fatal error, could not write index to specified file: " + indexFile.getAbsolutePath(), ex);
        }
        double secs = (System.currentTimeMillis() - before) / 1000d;
        System.out.println("Finished in " + (int) secs + " seconds, wrote index file to " + indexFile.getAbsolutePath());
    }
}
