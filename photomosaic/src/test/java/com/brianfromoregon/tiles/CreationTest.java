package com.brianfromoregon.tiles;

import com.brianfromoregon.tiles.Index.Image;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.awt.image.BufferedImage;

import static org.junit.Assert.assertArrayEquals;

public class CreationTest {

    @Test
    public void drillDown() {
        drillDown(2);
        drillDown(3);
        drillDown(4);
    }

    private void drillDown(int dd) {
        ImmutableList.Builder<Image> images = ImmutableList.builder();
        byte[][][] expected = new byte[dd][dd][];
        for (int i = dd * dd - 1; i >= 0; i--) {
            Image image = TestUtil.image("/drilldown/dd" + dd + "_" + i + ".bmp");
            images.add(image);
            expected[i / dd][i % dd] = image.jpeg;
        }
        Index index = new Index(images.build(), dd, dd);
        Creator creator = new Creator();
        BufferedImage target = TestUtil.bufferedImage("/drilldown/dd" + dd + ".bmp");
        {
            Mosaic mosaic = creator.designMosaic(OptimalMatchingIndex.create(ProcessedIndex.process(index, dd), ColorSpace.SRGB, dd), target, false, dd);
            assertArrayEquals(expected, layoutToJpegs(mosaic.layout));
        }
        {
            Mosaic mosaic = creator.designMosaic(OptimalMatchingIndex.create(ProcessedIndex.process(index, dd), ColorSpace.CIELAB, dd), target, true, dd);
            assertArrayEquals(expected, layoutToJpegs(mosaic.layout));
        }
    }

    private byte[][][] layoutToJpegs(Image[][] layout) {
        byte[][][] jpegs = new byte[layout.length][layout[0].length][];
        for (int i = 0; i < layout.length; i++) {
            Image[] images = layout[i];
            for (int j = 0; j < images.length; j++) {
                Image image = images[j];
                jpegs[i][j] = image.jpeg;
            }
        }
        return jpegs;
    }
}
