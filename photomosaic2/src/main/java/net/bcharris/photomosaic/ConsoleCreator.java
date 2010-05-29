package net.bcharris.photomosaic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.imageio.ImageIO;
import net.bcharris.photomosaic.Index;

public class ConsoleCreator {

    public static void main(String[] args) {
//        File indexFile = new File("E:\\DOCUME~1\\brian\\LOCALS~1\\Temp\\photomosaic6954079617072359010index");
        File indexFile = new File("E:\\mosaic\\20100526-bcharris-200-150.index");
        File targetImageFile = new File("E:\\mosaic\\parents.jpg");
        int numWide = 20;
        boolean reuseAllowed = true;

        Index index;
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(indexFile));
            index = (Index) in.readObject();
        } catch (Exception ex) {
            throw new RuntimeException("Fatal error, could not read index from specified file: " + indexFile.getAbsolutePath(), ex);
        }

        final BufferedImage targetImage;
        try {
            System.out.println("Reading target image: " + targetImageFile.getAbsolutePath());
            targetImage = ImageIO.read(targetImageFile);
        } catch (IOException ex) {
            throw new RuntimeException("Fatal error, could not read target image: " + targetImageFile.getAbsolutePath(), ex);
        }
        ProcessedIndex processedIndex = ProcessedIndex.process(index, Creator.DRILL_DOWN);
        MatchingIndex matchingIndex = MatchingIndex.create(processedIndex, Creator.DEFAULT_COLOR_SPACE, Creator.DEFAULT_ACCURACY);
        Creator creator = new Creator();
        Mosaic mosaic = creator.designMosaic(matchingIndex, targetImage, reuseAllowed, numWide);
        creator.writeToFile(mosaic);
    }
}
