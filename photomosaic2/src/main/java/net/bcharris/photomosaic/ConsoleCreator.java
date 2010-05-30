package net.bcharris.photomosaic;

import java.awt.image.BufferedImage;
import java.io.File;

public class ConsoleCreator {

    public static void main(String[] args) {
        File indexFile = new File("E:\\mosaic\\basic-tests\\bw-shapes\\tiles.index");
        File targetImageFile = new File("E:\\mosaic\\basic-tests\\bw-shapes\\bw-shapes.png");
        int numWide = 10;
        boolean reuseAllowed = true;

        Index index = Util.readIndex(indexFile);

        final BufferedImage targetImage = Util.readImage(targetImageFile);
        ProcessedIndex processedIndex = ProcessedIndex.process(index, 10);
        MatchingIndex matchingIndex = MatchingIndex.create(processedIndex, Creator.DEFAULT_COLOR_SPACE, Creator.DEFAULT_ACCURACY);
        Creator creator = new Creator();
        Mosaic mosaic = creator.designMosaic(matchingIndex, targetImage, reuseAllowed, numWide);
        File mosaicFile = creator.writeToFile(mosaic);
        Log.log(mosaicFile.getAbsolutePath());
    }
}
