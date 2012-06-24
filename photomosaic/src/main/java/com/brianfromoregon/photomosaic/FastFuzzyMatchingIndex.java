package com.brianfromoregon.photomosaic;

import com.google.common.collect.Lists;
import edu.wlu.cs.levy.CG.Checker;
import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import com.brianfromoregon.photomosaic.MatchingIndex.UsableImage;
import com.brianfromoregon.photomosaic.ProcessedIndex.ProcessedImage;

/**
 * This index matches in 3 space, no drilling down.  This constraint allows for
 * the usage of a kd-tree (k=3) which makes it extremely fast.
 */
public class FastFuzzyMatchingIndex extends MatchingIndex {

    private static final Random RANDOM = new Random();
    private static final Checker<UsableImage> CHECKER = new Checker<UsableImage>() {

        @Override
        public boolean usable(UsableImage v) {
            return !v.used.get();
        }
    };

    public static MatchingIndex create(ProcessedIndex index, ColorSpace colorSpace) {
        List<UsableImage> all = Lists.newArrayList();
        KDTree<UsableImage> kdTree = new KDTree<UsableImage>(3);
        for (final ProcessedImage processedImage : index.processedImages) {
            UsableImage usableImage = new UsableImage(processedImage.image) {
            };
            all.add(usableImage);
            double[] mean = Util.meanRgbs2ColorSpace(processedImage.meanRgb, colorSpace);
            try {
                kdTree.insert(mean, usableImage);
            } catch (KeySizeException ex) {
                throw new IllegalStateException("Programmer error", ex);
            } catch (KeyDuplicateException ex) {
                UsableImage existing;
                try {
                    existing = kdTree.search(mean);
                } catch (KeySizeException ex1) {
                    throw new IllegalStateException("Programmer error", ex1);
                }
                // If the jpegs really are different, force an insert
                if (!Arrays.equals(processedImage.image.jpeg, existing.image.jpeg)) {
                    forceInsert(mean, usableImage, kdTree);
                }
            }
        }
        return new FastFuzzyMatchingIndex(all, kdTree, colorSpace, index.width, index.height);
    }

    private static void forceInsert(double[] mean, UsableImage usableImage, KDTree<UsableImage> kdTree) {
        double[] copy = Arrays.copyOf(mean, mean.length);
        while (true) {
            try {
                double d = RANDOM.nextInt(1000) + 1;
                d *= 1e-10;
                for (int i = 0; i < copy.length; i++) {
                    copy[i] += d;
                }
                kdTree.insert(copy, usableImage);
                return;
            } catch (KeySizeException ex) {
                throw new IllegalStateException("Programmer error", ex);
            } catch (KeyDuplicateException ex) {
            }
        }
    }
    private final List<UsableImage> all;
    private final KDTree<UsableImage> kdTree;
    private final ColorSpace colorSpace;

    private FastFuzzyMatchingIndex(List<UsableImage> all, KDTree<UsableImage> kdTree, ColorSpace colorSpace, int imageWidth, int imageHeight) {
        super(imageWidth, imageHeight);
        this.all = all;
        this.kdTree = kdTree;
        this.colorSpace = colorSpace;
    }

    @Override
    protected UsableImage unusedMatch(int[] targetRgb, int w, int h) {
        double[] meanTarget = Util.mean(targetRgb, w, h, 1, 1, colorSpace);
        try {
            return kdTree.nearest(meanTarget, 1, CHECKER).get(0);
        } catch (KeySizeException ex) {
            throw new IllegalStateException("Programmer error", ex);
        }
    }

    @Override
    protected Collection all() {
        return all;
    }
}
