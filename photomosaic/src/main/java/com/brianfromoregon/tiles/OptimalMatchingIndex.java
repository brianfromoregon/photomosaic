package com.brianfromoregon.tiles;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.List;
import com.brianfromoregon.tiles.ProcessedIndex.ProcessedImage;

/**
 * This index matches in (3*dd*dd) space using a brute force approach (comparing
 * to every image).  It is guaranteed to return the optimal image but it takes
 * a while.
 */
public class OptimalMatchingIndex extends MatchingIndex {

    private static final Predicate<UsableImage> UNUSED = new Predicate<UsableImage>() {

        @Override
        public boolean apply(UsableImage input) {
            return !input.used.get();
        }
    };

    public static MatchingIndex create(ProcessedIndex index, ColorSpace colorSpace, int drillDown) {
        List<TranslatedImage> all = Lists.newArrayList();
        for (ProcessedImage processedImage : index.processedImages) {
            all.add(new TranslatedImage(processedImage, colorSpace));
        }
        return new OptimalMatchingIndex(all, drillDown, colorSpace, index.width, index.height);
    }
    private final List<TranslatedImage> all;
    private final int drillDown;
    private final ColorSpace colorSpace;

    private OptimalMatchingIndex(List<TranslatedImage> all, int drillDown, ColorSpace colorSpace, int imageWidth, int imageHeight) {
        super(imageWidth, imageHeight);
        this.all = all;
        this.drillDown = drillDown;
        this.colorSpace = colorSpace;
    }

    @Override
    protected UsableImage unusedMatch(int[] targetRgb, int w, int h) {
        double[] ddMeanTarget = Util.mean(targetRgb, w, h, drillDown, drillDown, colorSpace);
        TranslatedImage nearest = null;
        double minDistance = Double.MAX_VALUE;
        for (TranslatedImage translatedImage : Iterables.filter(all, UNUSED)) {
            double distance = Util.euclidianDistance(translatedImage.ddMean, ddMeanTarget);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = translatedImage;
            }
        }
        return nearest;
    }

    @Override
    protected List<? extends UsableImage> all() {
        return all;
    }

    private static class TranslatedImage extends UsableImage {

        private final double[] ddMean;

        public TranslatedImage(ProcessedImage processedImage, ColorSpace colorSpace) {
            super(processedImage.image);
            this.ddMean = Util.meanRgbs2ColorSpace(processedImage.ddMeanRgb, colorSpace);
        }
    }
}
