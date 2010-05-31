package net.bcharris.photomosaic;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class MosaicPreviewPanel extends JPanel {

    private volatile BufferedImage bufferedImage;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bufferedImage != null) {
            int actWidth = bufferedImage.getWidth();
            int actHeight = bufferedImage.getHeight();
            double scalar = getScalar(actWidth, actHeight, getWidth(), getHeight());
            int width = (int) (actWidth * scalar);
            int height = (int) (actHeight * scalar);
            g.drawImage(bufferedImage, 0, 0, width, height, this);
        }
    }

    public void update(Mosaic mosaic) {
        int mosaicWidth = mosaic.cellWidth * mosaic.numWide();
        int mosaicHeight = mosaic.cellHeight * mosaic.numTall();
        double scalar = getScalar(mosaicWidth, mosaicHeight, getWidth(), getHeight());
        int myCellHeight = (int) (mosaic.cellHeight * scalar);
        int myCellWidth = (int) (mosaic.cellWidth * scalar);
        int width = myCellWidth * mosaic.numWide();
        int height = myCellHeight * mosaic.numTall();
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = newImage.createGraphics();

        for (int row = 0; row < mosaic.jpegLayout.length; row++) {
            byte[][] rowJpegs = mosaic.jpegLayout[row];
            for (int column = 0; column < rowJpegs.length; column++) {
                g.drawImage(Util.jpegToBufferedImage(rowJpegs[column]), column * myCellWidth, row * myCellHeight, myCellWidth, myCellHeight, this);
            }
        }
        this.bufferedImage = newImage;
        repaint();
    }

    private double getScalar(int actWidth, int actHeight, int myWidth, int myHeight) {
        if (myWidth < 1) {
            myWidth = 1;
        }
        if (myHeight < 1) {
            myHeight = 1;
        }
        double widthRatio = (double) myWidth / actWidth;
        double heightRatio = (double) myHeight / actHeight;
        if (widthRatio > heightRatio) {
            return heightRatio;
        } else {
            return widthRatio;
        }
    }
}
