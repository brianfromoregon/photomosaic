package net.bcharris.photomosaic;

import org.junit.Test;
import static org.junit.Assert.*;

public class UtilTest {

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
