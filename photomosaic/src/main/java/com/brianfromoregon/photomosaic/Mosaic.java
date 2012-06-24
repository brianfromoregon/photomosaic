package com.brianfromoregon.photomosaic;

import com.brianfromoregon.photomosaic.Index.Image;

public class Mosaic {
    public final Image[][] layout;
    public final int cellWidth;
    public final int cellHeight;

    public Mosaic(Image[][] layout, int cellWidth, int cellHeight) {
        this.layout = layout;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
    }

    public int numTall()
    {
        return layout.length;
    }

    public int numWide()
    {
        return layout[0].length;
    }
}
