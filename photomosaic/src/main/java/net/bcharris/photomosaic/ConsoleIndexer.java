package net.bcharris.photomosaic;

import java.io.File;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class ConsoleIndexer {

    @Option(name = "-dir", usage = "Source image directory to index (default: working directory)", metaVar = "DIR")
    private File directoryToIndex = new File(".");
    @Option(name = "-convert", usage = "ImageMagick convert app", required = true)
    private File imageMagickConvert = new File("convert");
    @Option(name = "-w", usage = "Width of indexed images in pixels (default: 16)")
    private int width = 16;
    @Option(name = "-h", usage = "Height of indexed images in pixels (default: 11)")
    private int height = 11;

    public static void main(String[] args) {
        ConsoleIndexer indexer = new ConsoleIndexer(args);
        indexer.run();
    }

    public ConsoleIndexer(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
            if (!directoryToIndex.isDirectory()) {
                throw new CmdLineException("The specified directory to index is not a directory: " + directoryToIndex.getAbsolutePath());
            }
            if (!imageMagickConvert.isFile() || !imageMagickConvert.exists()) {
                throw new CmdLineException("The specified ImageMagick convert app is invalid: " + imageMagickConvert.getAbsolutePath());
            }
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.exit(1);
        }
    }

    private void run() {
        long before = System.currentTimeMillis();
        Indexer indexer = new Indexer();
        File indexFile = Util.createTempFile("photomosaic", ".index");
        Util.writeIndex(indexer.index(directoryToIndex, imageMagickConvert, width, height), indexFile);
        double secs = (System.currentTimeMillis() - before) / 1000d;
        Log.log("Finished in " + (int) secs + " seconds");
        Log.log("Wrote index file to " + indexFile.getAbsolutePath());
    }
}
