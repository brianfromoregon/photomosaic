package net.bcharris.photomosaic;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.bcharris.photomosaic.Util;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

/**
 * Builds the index file.
 */
public class Indexer {

    public static void main(String[] args) {
        File sourceImageDirectory = new File("C:\\Documents and Settings\\harris\\My Documents\\My Pictures");
        final File convertApp = new File("C:\\Program Files\\ImageMagick-6.6.1-Q8\\convert.exe");
        final int width = 200;
        final int height = 150;


        long before = System.currentTimeMillis();
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
        final ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {

            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat("yyyyMMddHHmmss");
            }
        };
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        final ArrayList<Index.Image> images = new ArrayList<Index.Image>();
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
                    commandLine.addArgument("north");
                    commandLine.addArgument("-extent");
                    commandLine.addArgument(width + "x" + height);
                    commandLine.addArgument("JPEG:" + tmpFile.getAbsolutePath());
                    System.out.println(dateFormat.get().format(new Date()) + ":" + (images.size() + 1) + ":" + commandLine.toString());
                    DefaultExecutor executor = new DefaultExecutor();
                    PumpStreamHandler handler = new PumpStreamHandler(System.out, System.out);
                    executor.setStreamHandler(handler);
                    try {
                        executor.execute(commandLine);
                    } catch (Throwable ex) {
                        System.out.println(String.format("Problem shrinking image '%s'\n", sourceImage.getAbsolutePath()));
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
            out.writeObject(new Index(images, width, height));
            out.close();
        } catch (IOException ex) {
            throw new RuntimeException("Fatal error, could not write index to specified file: " + indexFile.getAbsolutePath(), ex);
        }
        double secs = (System.currentTimeMillis() - before) / 1000d;
        System.out.println("Finished in " + (int) secs + " seconds, wrote index file to " + indexFile.getAbsolutePath());
    }
}
