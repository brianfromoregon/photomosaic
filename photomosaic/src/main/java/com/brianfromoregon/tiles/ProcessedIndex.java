package com.brianfromoregon.tiles;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.util.List;
import com.brianfromoregon.tiles.Index.Image;

/**
 * An index which has already been processed (processing takes a long time).
 */
public class ProcessedIndex {

    public final int drillDown;
    public final int width;
    public final int height;
    public final List<ProcessedImage> processedImages;

    private ProcessedIndex(int drillDown, int width, int height, List<ProcessedImage> processedImages) {
        this.drillDown = drillDown;
        this.width = width;
        this.height = height;
        this.processedImages = processedImages;
    }

    public static ProcessedIndex process(Index index, final int drillDown) {
        final List<ProcessedImage> processedImages = Lists.newArrayList();
        ThreadedIteratorProcessor<Image> threadedIteratorProcessor = new ThreadedIteratorProcessor<Image>();
        threadedIteratorProcessor.processIterator(index.images.iterator(), new ThreadedIteratorProcessor.ElementProcessor<Image>() {

            @Override
            public void processElement(Image image) {
                ProcessedImage processedImage = new ProcessedImage(image, drillDown);
                synchronized (processedImages) {
                    processedImages.add(processedImage);
                }
            }
        });
        return new ProcessedIndex(drillDown, index.width, index.height, processedImages);
    }

    public static class ProcessedImage {

        public final Image image;
        public final double[] ddMeanRgb;
        public final double[] meanRgb;

        public ProcessedImage(Image image, int dd) {
            this.image = image;
            BufferedImage bufferedImage = Util.jpegToBufferedImage(image.jpeg);
            double[] meanRgbs = Util.mean(Util.bufferedImageToRgb(bufferedImage), bufferedImage.getWidth(), bufferedImage.getHeight(), dd, dd, ColorSpace.SRGB);
            this.ddMeanRgb = meanRgbs;
            meanRgb = Util.mean(ddMeanRgb);
        }
    }
}
