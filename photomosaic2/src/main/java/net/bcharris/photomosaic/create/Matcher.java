package net.bcharris.photomosaic.create;

import edu.wlu.cs.levy.CG.Checker;
import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;
import java.awt.image.BufferedImage;
import java.util.List;
import net.bcharris.photomosaic.index.Index;

public class Matcher {

    private static final Checker<JpegInfo> CHECKER = new Checker<JpegInfo>() {

        @Override
        public boolean usable(JpegInfo v) {
            return !v.used;
        }
    };
    private final KDTree<JpegInfo> tree;
    private final boolean reuseAllowed;
    private final int dd;
    private final ColorSpace colorSpace;

    public Matcher(Index index, boolean reuseAllowed, int dd, ColorSpace colorSpace) {
        this.colorSpace = colorSpace;
        this.dd = dd;
        this.reuseAllowed = reuseAllowed;
        tree = new KDTree<JpegInfo>(3);
        for (Index.Image image : index.images) {
            JpegInfo jpegInfo = new JpegInfo(image.jpeg, dd, colorSpace);
            try {
                tree.insert(jpegInfo.mean, jpegInfo);
            } catch (KeySizeException ex) {
                throw new IllegalStateException("Programmer error", ex);
            } catch (KeyDuplicateException ex) {
                // Not a problem.
            }
        }
    }

    public byte[] match(int[] targetRgb, int w, int h) {
        double[] ddMean = Util.meanRgbs(targetRgb, w, h, dd, dd);
            if (colorSpace == ColorSpace.CIELAB)
                Util.meanRgbs2MeanLabs(ddMean);
        double[] mean = mean(ddMean);
        // First do an efficient search for the 10 nearest mean RGB images
        List<JpegInfo> nearestN;
        try {
            nearestN = tree.nearest(mean, 10, CHECKER);
        } catch (KeySizeException ex) {
            throw new IllegalStateException("Programmer error", ex);
        }
        // Then do a more expensive drill down evaluation
        JpegInfo nearest = null;
        double minDistance = Double.MAX_VALUE;
        for (JpegInfo jpegInfo : nearestN) {
            double distance = jpegInfo.distanceTo(ddMean);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = jpegInfo;
            }
        }
        if (!reuseAllowed) {
            nearest.used = true;
        }
        return nearest.jpeg;
    }

    private static class JpegInfo {

        public final byte[] jpeg;
        public final double[] ddMean;
        public final double[] mean;
        public volatile boolean used = false;

        public JpegInfo(byte[] jpeg, int dd, ColorSpace colorSpace) {
            this.jpeg = jpeg;
            BufferedImage bufferedImage = Util.jpegToBufferedImage(jpeg);
            double[] meanRgbs = Util.meanRgbs(Util.bufferedImageToRgb(bufferedImage), bufferedImage.getWidth(), bufferedImage.getHeight(), dd, dd);
            this.ddMean = meanRgbs;
            if (colorSpace == ColorSpace.CIELAB)
                Util.meanRgbs2MeanLabs(ddMean);
            mean = mean(ddMean);
        }

        public double distanceTo(double[] other) {
            double distance = 0;
            for (int i = 0; i < other.length; i++) {
                distance += Math.abs(other[i] - ddMean[i]);
            }
            return distance;
        }
    }

    private static double[] mean(double[] ddMean) {
        double[] meanRgb = new double[3];
        double sumR = 0;
        double sumG = 0;
        double sumB = 0;
        for (int i = 0; i < ddMean.length; i += 3) {
            sumR += ddMean[i];
            sumG += ddMean[i + 1];
            sumB += ddMean[i + 2];
        }
        meanRgb[0] = sumR / (ddMean.length / 3);
        meanRgb[1] = sumG / (ddMean.length / 3);
        meanRgb[2] = sumB / (ddMean.length / 3);
        return meanRgb;
    }
}
