package com.brianfromoregon.photomosaic;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import com.brianfromoregon.photomosaic.Index.Image;

public class MosaicPreviewPanel extends JPanel {

    private volatile BufferedImage bufferedImage;
    private volatile int width, height;
    private volatile Mosaic mosaic;

    public MosaicPreviewPanel() {
        ToolTipManager.sharedInstance().registerComponent(this);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bufferedImage != null) {
            int actWidth = bufferedImage.getWidth();
            int actHeight = bufferedImage.getHeight();
            double scalar = getScalar(actWidth, actHeight, getWidth(), getHeight());
            width = (int) (actWidth * scalar);
            height = (int) (actHeight * scalar);
            g.drawImage(bufferedImage, 0, 0, width, height, this);
        }
    }

    public void update(Mosaic mosaic) {
        this.mosaic = mosaic;
        int mosaicWidth = mosaic.cellWidth * mosaic.numWide();
        int mosaicHeight = mosaic.cellHeight * mosaic.numTall();
        double scalar = getScalar(mosaicWidth, mosaicHeight, getWidth(), getHeight());
        int myCellHeight = (int) (mosaic.cellHeight * scalar);
        int myCellWidth = (int) (mosaic.cellWidth * scalar);
        width = myCellWidth * mosaic.numWide();
        height = myCellHeight * mosaic.numTall();
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = newImage.createGraphics();

        for (int row = 0; row < mosaic.layout.length; row++) {
            Image[] rowImages = mosaic.layout[row];
            for (int column = 0; column < rowImages.length; column++) {
                g.drawImage(Util.jpegToBufferedImage(rowImages[column].jpeg), column * myCellWidth, row * myCellHeight, myCellWidth, myCellHeight, this);
            }
        }
        this.bufferedImage = newImage;
        repaint();
    }

    private static double getScalar(int actWidth, int actHeight, int myWidth, int myHeight) {
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

    @Override
    public String getToolTipText(MouseEvent event) {
        Image image = imageForEvent(event);
        if (image != null) {
            return image.url;
        } else {
            return null;
        }
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);
        if (e.getClickCount() == 1) {
            Image image = imageForEvent(e);
            if (image != null) {
                JDialog popup = new JDialog((java.awt.Frame) null, image.url, true);
                popup.add(new ImagePanel(Util.jpegToBufferedImage(image.jpeg)), BorderLayout.CENTER);
                popup.pack();
                Util.installEscapeCloseOperation(popup);
                popup.setVisible(true);
            }
        }
    }

    private Image imageForEvent(MouseEvent event) {
        if (event.getComponent() != this) {
            return null;
        }
        double x = event.getX();
        double y = event.getY();
        int column = (int) (mosaic.numWide() * x / width);
        int row = (int) (mosaic.numTall() * y / height);
        if (row >= 0 && row < mosaic.numTall() && column >= 0 && column < mosaic.numWide()) {
            return mosaic.layout[row][column];
        } else {
            return null;
        }
    }

    private static class ImagePanel extends JPanel {

        private final BufferedImage image;

        public ImagePanel(BufferedImage image) {
            this.image = image;
        }

        @Override
        protected void paintComponent(Graphics g) {
            double scalar = getScalar(image.getWidth(), image.getHeight(), getWidth(), getHeight());
            g.drawImage(image, 0, 0, (int) (image.getWidth() * scalar), (int) (image.getHeight() * scalar), null);
        }

        @Override
        public Dimension preferredSize() {
            return new Dimension(image.getWidth(), image.getHeight());
        }
    }
}
