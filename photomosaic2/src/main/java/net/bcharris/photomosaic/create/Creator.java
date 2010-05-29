package net.bcharris.photomosaic.create;

import com.google.common.base.Throwables;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import javax.imageio.ImageIO;
import net.bcharris.photomosaic.create.MatchingIndex.Accuracy;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

/**
 * Creates image mosaics.
 */
public class Creator {

    public static final int DRILL_DOWN = 6;
    public static final Accuracy DEFAULT_ACCURACY = Accuracy.EXACT;
    public static final ColorSpace DEFAULT_COLOR_SPACE = ColorSpace.CIELAB;

    public Mosaic designMosaic(MatchingIndex index, BufferedImage targetImage, boolean reuseAllowed, int numWide) {
        int targetW = targetImage.getWidth();
        int targetH = targetImage.getHeight();

        int mosaicW = index.jpegWidth * numWide;
        double scalar = mosaicW / (double) targetW;
        double scaledTargetH = targetH * scalar;
        int numTall = (int) (scaledTargetH / index.jpegHeight);

        if (!reuseAllowed && numWide * numTall > index.size) {
            throw new IllegalArgumentException("Cannot create a mosaic having " + numWide * numTall + " cells with an index of size " + index.size);
        }

        int targetChunkW = targetW / numWide;
        int targetChunkH = targetH / numTall;

        System.out.println("Designing mosaic.");
        byte[][][] mosaicJpegLayout = new byte[numTall][numWide][];
        int[] cellOrder = new int[numWide * numTall];
        for (int i = 0; i < cellOrder.length; i++) {
            cellOrder[i] = i;
        }
        Collections.shuffle(Arrays.asList(cellOrder));
        int verticalOffset = (targetH % targetChunkH) / 2;
        for (int i = 0; i < cellOrder.length; i++) {
            if (i % 10 == 0) {
                System.out.println(i + "/" + cellOrder.length);
            }
            int cell = cellOrder[i];
            int row = cell / numWide;
            int column = cell % numWide;
            int[] targetSectionRgb = Util.bufferedImageToRgb(targetImage.getSubimage(column * targetChunkW, row * targetChunkH + verticalOffset, targetChunkW, targetChunkH));
            mosaicJpegLayout[row][column] = index.match(targetSectionRgb, targetChunkW, targetChunkH, reuseAllowed);
        }

        return new Mosaic(mosaicJpegLayout, index.jpegWidth, index.jpegHeight);
    }

    public File writeToFile(Mosaic mosaic) {
        File montageApp = new File("E:\\Program Files\\ImageMagick-6.5.6-Q16\\montage.exe");
        System.out.println("Creating mosaic rows.");
        File[] rowImages = new File[mosaic.numTall()];
        for (int row = 0; row < mosaic.jpegLayout.length; row++) {
            BufferedImage rowImage = new BufferedImage(mosaic.cellWidth * mosaic.numWide(), mosaic.cellHeight, BufferedImage.TYPE_INT_RGB);
            byte[][] rowJpegs = mosaic.jpegLayout[row];
            for (int column = 0; column < rowJpegs.length; column++) {
                byte[] jpeg = rowJpegs[column];
                int[] rgb = Util.bufferedImageToRgb(Util.jpegToBufferedImage(jpeg));
                rowImage.setRGB(column * mosaic.cellWidth, 0, mosaic.cellWidth, mosaic.cellHeight, rgb, 0, mosaic.cellWidth);
            }
            File rowImageFile = Util.createTempFile("row" + row + "_", ".png");
            rowImageFile.deleteOnExit();
            rowImages[row] = rowImageFile;
            try {
                ImageIO.write(rowImage, "png", rowImageFile);
                System.out.println("Wrote row image to " + rowImageFile.getAbsolutePath());
            } catch (IOException ex) {
                throw new RuntimeException("Fatal error, could not write to temporary file: " + rowImageFile.getAbsolutePath(), ex);
            }
        }

        System.out.println("Creating final mosaic.");
        File mosaicFile = Util.createTempFile("finalmosaic", ".png");
        CommandLine commandLine = new CommandLine(montageApp.getAbsolutePath());
        for (int i = 0; i < rowImages.length; i++) {
            File row = rowImages[i];
            commandLine.addArgument(row.getAbsolutePath());
        }
        commandLine.addArgument("-tile");
        commandLine.addArgument("x" + mosaic.numTall());
        commandLine.addArgument("-geometry ");
        commandLine.addArgument("+0+0");
        commandLine.addArgument("PNG24:" + mosaicFile.getAbsolutePath());
        System.out.println(commandLine.toString());
        DefaultExecutor executor = new DefaultExecutor();
        PumpStreamHandler handler = new PumpStreamHandler(System.out, System.out);
        executor.setStreamHandler(handler);
        try {
            executor.execute(commandLine);
        } catch (Exception ex) {
            Throwables.propagate(ex);
        }
        for (File file : rowImages) {
            file.delete();
        }
        return mosaicFile;
    }
}
