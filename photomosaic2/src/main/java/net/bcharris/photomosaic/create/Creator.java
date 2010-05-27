package net.bcharris.photomosaic.create;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Collections;
import javax.imageio.ImageIO;
import net.bcharris.photomosaic.index.Index;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

/**
 * Creates image mosaics.
 */
public class Creator {

    public static void main(String[] args) {
//        File indexFile = new File("E:\\DOCUME~1\\brian\\LOCALS~1\\Temp\\photomosaic6954079617072359010index");
        File indexFile = new File("E:\\tmp\\generated-colors.index");
        File targetImage = new File("E:\\cygwin\\home\\brian\\11-15-07\\IMG_8554.JPG");
        File montageApp = new File("E:\\Program Files\\ImageMagick-6.5.6-Q16\\montage.exe");
        int numWide = 30;
        boolean reuseAllowed = true;
        ColorSpace colorSpace = ColorSpace.CIELAB;



        Index index;
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(indexFile));
            index = (Index) in.readObject();
        } catch (Exception ex) {
            throw new RuntimeException("Fatal error, could not read index from specified file: " + indexFile.getAbsolutePath(), ex);
        }


        BufferedImage img;
        try {
            img = ImageIO.read(targetImage);
        } catch (IOException ex) {
            throw new RuntimeException("Fatal error, could not read target image: " + targetImage.getAbsolutePath(), ex);
        }
        int targetW = img.getWidth();
        int targetH = img.getHeight();

        int mosaicW = index.width * numWide;
        double scalar = mosaicW / (double) targetW;
        double scaledTargetH = targetH * scalar;
        int numTall = (int) (scaledTargetH / index.height);

        if (!reuseAllowed && numWide * numTall > index.images.size()) {
            System.err.println("Fatal error: cannot create a mosaic having " + numWide * numTall + " cells with an index of size " + index.images.size());
            return;
        }

        int targetChunkW = targetW / numWide;
        int targetChunkH = targetH / numTall;


        byte[][][] mosaicJpegLayout = new byte[numTall][numWide][];
        int[] cellOrder = new int[numWide * numTall];
        for (int i = 0; i < cellOrder.length; i++) {
            cellOrder[i] = i;
        }
        Collections.shuffle(Arrays.asList(cellOrder));
        Matcher matcher = new Matcher(index, reuseAllowed, 6, colorSpace);
        int verticalOffset = (targetH % targetChunkH) / 2;
        for (int i = 0; i < cellOrder.length; i++) {
            int cell = cellOrder[i];
            int row = cell / numWide;
            int column = cell % numWide;
            int[] targetSectionRgb = Util.bufferedImageToRgb(img.getSubimage(column * targetChunkW, row * targetChunkH + verticalOffset, targetChunkW, targetChunkH));
            mosaicJpegLayout[row][column] = matcher.match(targetSectionRgb, targetChunkW, targetChunkH);
        }

        File[] rowImages = new File[numTall];
        for (int row = 0; row < mosaicJpegLayout.length; row++) {
            BufferedImage rowImage = new BufferedImage(mosaicW, index.height, BufferedImage.TYPE_INT_RGB);
            byte[][] rowJpegs = mosaicJpegLayout[row];
            for (int column = 0; column < rowJpegs.length; column++) {
                byte[] jpeg = rowJpegs[column];
                int[] rgb = Util.bufferedImageToRgb(Util.jpegToBufferedImage(jpeg));
                rowImage.setRGB(column * index.width, 0, index.width, index.height, rgb, 0, index.width);
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

        File mosaicFile = Util.createTempFile("finalmosaic", ".png");
        CommandLine commandLine = new CommandLine(montageApp.getAbsolutePath());
        for (int i = 0; i < rowImages.length; i++) {
            File row = rowImages[i];
            commandLine.addArgument(row.getAbsolutePath());
        }
        commandLine.addArgument("-tile");
        commandLine.addArgument("x" + numTall);
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
            System.out.println("Problem creating mosaic");
            ex.printStackTrace(System.out);
            return;
        }
    }
}
