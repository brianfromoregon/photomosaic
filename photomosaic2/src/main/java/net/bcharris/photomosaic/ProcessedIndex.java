package net.bcharris.photomosaic;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.util.List;

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

    public static ProcessedIndex process(Index index, final int drillDown) {
        final List<ProcessedJpeg> jpegs = Lists.newArrayList();
        ThreadedIteratorProcessor<Index.Image> threadedIteratorProcessor = new ThreadedIteratorProcessor<Index.Image>();
        threadedIteratorProcessor.processIterator(index.images.iterator(), new ThreadedIteratorProcessor.ElementProcessor<Index.Image>() {

            @Override
            public void processElement(Index.Image image) {
                ProcessedJpeg jpegInfo = new ProcessedJpeg(image.jpeg, drillDown);
                synchronized (jpegs) {
                    jpegs.add(jpegInfo);
                }
            }
        });
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
    }
}
