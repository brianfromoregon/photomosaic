package com.brianfromoregon.photomosaic;

import java.awt.image.BufferedImage;

import com.brianfromoregon.photomosaic.ColorSpace;
import com.brianfromoregon.photomosaic.Util;
import org.junit.Test;
import static org.junit.Assert.*;

public class UtilTest {

    @Test
    public void meanColors() {
        BufferedImage dd2Img = TestUtil.bufferedImage("/drilldown/dd2.bmp");
        int[] dd2Rgb = Util.bufferedImageToRgb(dd2Img);
        {
            double[] expected = new double[]{255d * 3 / 4, 255d * 3 / 4, 255d * 3 / 4};
            double[] actual = Util.mean(dd2Rgb, 4, 4, 1, 1, ColorSpace.SRGB);
            for (int i = 0; i < actual.length; i++) {
                assertEquals(expected[i], actual[i], 0d);
            }
        }
        {
            double[] expected = new double[]{0, 0, 0, 255, 255, 255, 255, 255, 255, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 255, 255, 255, 255, 255, 255, 0, 0, 0};
            double[] actual = Util.mean(dd2Rgb, 4, 4, 4, 4, ColorSpace.SRGB);
            for (int i = 0; i < actual.length; i++) {
                assertEquals(expected[i], actual[i], 0d);
            }
        }
        {
            double[] expected = new double[]{255d / 2, 255d / 2, 255d / 2, 255, 255, 255, 255, 255, 255, 255d / 2, 255d / 2, 255d / 2};
            double[] actual = Util.mean(dd2Rgb, 4, 4, 1, 4, ColorSpace.SRGB);
            for (int i = 0; i < actual.length; i++) {
                assertEquals(expected[i], actual[i], 0d);
            }
        }
        {
            double[] expected = new double[]{255d * 3 / 4, 255d * 3 / 4, 255d * 3 / 4, 255d * 3 / 4, 255d * 3 / 4, 255d * 3 / 4};
            double[] actual = Util.mean(dd2Rgb, 4, 4, 2, 1, ColorSpace.SRGB);
            for (int i = 0; i < actual.length; i++) {
                assertEquals(expected[i], actual[i], 0d);
            }
        }
    }

    @Test
    public void rgbToCIELAB() {
        assertRgbToCIELAB(255, 171, 32, 76.369, 21.182, 74.945);
        assertRgbToCIELAB(40, 199, 20, 70.419, -68.202, 66.965);
    }

    @Test
    public void meanRgbs2ColorSpace() {
        double[] rgbs = new double[]{255, 171, 32, 40, 199, 20};
        double[] labs = new double[]{76.369, 21.182, 74.945, 70.419, -68.202, 66.965};
        double[] actuals = Util.meanRgbs2ColorSpace(rgbs, ColorSpace.CIELAB);
        for (int i = 0; i < labs.length; i++) {
            double lab = labs[i];
            double actual = actuals[i];
            assertEquals(lab, actual, .0005d);
        }
    }

    private void assertRgbToCIELAB(double r, double g, double b1, double l, double a, double b2) {
        double[] tmp = new double[3];
        Util.rgbToCIELAB(r, g, b1, tmp);
        assertEquals(l, tmp[0], .0005d);
        assertEquals(a, tmp[1], .0005d);
        assertEquals(b2, tmp[2], .0005d);
    }
}
