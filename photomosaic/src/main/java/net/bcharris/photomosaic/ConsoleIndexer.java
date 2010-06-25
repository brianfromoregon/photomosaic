package net.bcharris.photomosaic;

import java.io.File;

public class ConsoleIndexer {

    // This should use args4j: http://code.google.com/p/photomosaic/issues/detail?id=28
    public static void main(String[] args) {
        File sourceImageDirectory = new File("E:\\dev\\photomosaic\\image-gen\\colors_16x11");
        final File convertApp = new File("E:\\Program Files\\ImageMagick-6.5.6-Q16\\convert.exe");
        final int width = 16;
        final int height = 11;
        long before = System.currentTimeMillis();
        Indexer indexer = new Indexer();
        File indexFile = Util.createTempFile("photomosaic", ".index");
        Util.writeIndex(indexer.index(sourceImageDirectory, convertApp, width, height), indexFile);
        double secs = (System.currentTimeMillis() - before) / 1000d;
        Log.log("Finished in " + (int) secs + " seconds, wrote index file to " + indexFile.getAbsolutePath());
    }
}
