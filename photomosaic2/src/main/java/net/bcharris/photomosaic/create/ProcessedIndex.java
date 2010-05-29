package net.bcharris.photomosaic.create;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.util.List;
import net.bcharris.photomosaic.index.Index;

/**
 * An index which has already been processed (processing takes a long time).
 */
public class ProcessedIndex {

    public final int drillDown;
    public final int width;
    public final int height;
    public final List<ProcessedJpeg> jpegs;

    private ProcessedIndex(int drillDown, int width, int height, List<ProcessedJpeg> jpegs) {
        this.drillDown = drillDown;
        this.width = width;
        this.height = height;
        this.jpegs = jpegs;
    }

    public static ProcessedIndex process(Index index, int drillDown) {
        List<ProcessedJpeg> jpegs = Lists.newArrayList();
        for (Index.Image image : index.images) {
            ProcessedJpeg jpegInfo = new ProcessedJpeg(image.jpeg, drillDown);
            jpegs.add(jpegInfo);
        }
        return new ProcessedIndex(drillDown, index.width, index.height, jpegs);
    }

    public static class ProcessedJpeg {

        public final byte[] bytes;
        public final double[] ddMeanRgb;
        public final double[] meanRgb;

        public ProcessedJpeg(byte[] jpeg, int dd) {
            this.bytes = jpeg;
            BufferedImage bufferedImage = Util.jpegToBufferedImage(jpeg);
            double[] meanRgbs = Util.mean(Util.bufferedImageToRgb(bufferedImage), bufferedImage.getWidth(), bufferedImage.getHeight(), dd, dd, ColorSpace.SRGB);
            this.ddMeanRgb = meanRgbs;
            meanRgb = Util.mean(ddMeanRgb);
        }

        public double distanceTo(double[] other) {
            double distance = 0;
            for (int i = 0; i < other.length; i++) {
                distance += Math.abs(other[i] - ddMeanRgb[i]);
            }
            return distance;
        }


    }
}
