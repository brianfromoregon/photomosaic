package net.bcharris.photomosaic;

public class Mosaic {
    public final byte[][][] jpegLayout;
    public final int cellWidth;
    public final int cellHeight;

    public Mosaic(byte[][][] jpegLayout, int cellWidth, int cellHeight) {
        this.jpegLayout = jpegLayout;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
    }

    public int numTall()
    {
        return jpegLayout.length;
    }

    public int numWide()
    {
        return jpegLayout[0].length;
    }
}
