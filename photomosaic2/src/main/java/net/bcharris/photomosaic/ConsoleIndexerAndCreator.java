package net.bcharris.photomosaic;

import java.awt.image.BufferedImage;
import java.io.File;

public class ConsoleIndexerAndCreator {
    public static void main(String[] args) {
        File sourceImageDirectory = new File("E:\\mosaic\\basic-tests\\bw-shapes\\tiles");
        final File convertApp = new File("E:\\Program Files\\ImageMagick-6.5.6-Q16\\convert.exe");
        final int width = 10;
        final int height = 10;
        File targetImageFile = new File("E:\\mosaic\\basic-tests\\bw-shapes\\bw-shapes.png");
        int numWide = 10;
        boolean reuseAllowed = false;


        long before = System.currentTimeMillis();
        Indexer indexer = new Indexer();
        Index index = indexer.index(sourceImageDirectory, convertApp, width, height);
        double secs = (System.currentTimeMillis() - before) / 1000d;
        Log.log("Finished indexing in " + (int) secs + " seconds");

        final BufferedImage targetImage = Util.readImage(targetImageFile);
        ProcessedIndex processedIndex = ProcessedIndex.process(index, Creator.DEFAULT_DRILL_DOWN);
        MatchingIndex matchingIndex = MatchingIndex.create(processedIndex, Creator.DEFAULT_COLOR_SPACE, Creator.DEFAULT_ACCURACY);
        Creator creator = new Creator();
        Mosaic mosaic = creator.designMosaic(matchingIndex, targetImage, reuseAllowed, numWide);
        creator.writeToFile(mosaic);
    }
}
