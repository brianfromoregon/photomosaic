package net.bcharris.photomosaic;

import net.bcharris.photomosaic.Util;
import org.junit.Test;
import static org.junit.Assert.*;

public class UtilTest {

    @Test
    public void testrgbToCIELAB() {
        assertRgbToCIELAB(255, 171, 32, 76.369, 21.182, 74.945);
        assertRgbToCIELAB(40, 199, 20, 70.419, -68.202, 66.965);
    }

    private void assertRgbToCIELAB(double r, double g, double b1, double l, double a, double b2) {
        double[] tmp = new double[3];
        Util.rgbToCIELAB(r, g, b1, tmp);
        assertEquals(l, tmp[0], .0005d);
        assertEquals(a, tmp[1], .0005d);
        assertEquals(b2, tmp[2], .0005d);
    }
}
