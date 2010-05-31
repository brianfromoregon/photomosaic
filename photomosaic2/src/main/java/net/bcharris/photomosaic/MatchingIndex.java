package net.bcharris.photomosaic;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import edu.wlu.cs.levy.CG.Checker;
import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import net.bcharris.photomosaic.Index.Image;
import net.bcharris.photomosaic.ProcessedIndex.ProcessedImage;

/**
 * An index used for matching.
 */
public class MatchingIndex {

    public enum Accuracy {

        EXACT, APPROXIMATE, FASTEST;
    }
    private static final Predicate<UsableImage> UNUSED = new Predicate<UsableImage>() {

        @Override
        public boolean apply(UsableImage input) {
            return !input.used.get();
        }
    };
    private static final Checker<UsableImage> CHECKER = new Checker<UsableImage>() {

        @Override
        public boolean usable(UsableImage v) {
            return UNUSED.apply(v);
        }
    };
    public final ColorSpace colorSpace;
    public final int size;
    public final int jpegWidth, jpegHeight;
    private final List<UsableImage> list;
    private final KDTree<UsableImage> kdTree;
    private final int drillDown;
    private final Accuracy accuracy;
    private static final Random RANDOM = new Random();

    private MatchingIndex(ColorSpace colorSpace, List<UsableImage> list, KDTree<UsableImage> kdTree, int drillDown, Accuracy accuracy, int jpegWidth, int jpegHeight) {
        this.colorSpace = colorSpace;
        this.list = list;
        this.kdTree = kdTree;
        this.drillDown = drillDown;
        this.accuracy = accuracy;
        if (kdTree != null) {
            this.size = kdTree.size();
        } else {
            this.size = list.size();
        }
        this.jpegWidth = jpegWidth;
        this.jpegHeight = jpegHeight;
    }

    public static MatchingIndex create(ProcessedIndex index, ColorSpace colorSpace, Accuracy accuracy) {
        List<UsableImage> list = Lists.newArrayList();
        for (ProcessedImage processedImage : index.processedImages) {
            list.add(new UsableImage(processedImage));
        }
        KDTree<UsableImage> kdTree = null;
        if (accuracy == Accuracy.APPROXIMATE || accuracy == Accuracy.FASTEST) {
            kdTree = new KDTree<UsableImage>(3);
            for (UsableImage usableImage : list) {
                double[] mean = Util.meanRgbs2ColorSpace(usableImage.processedImage.meanRgb, colorSpace);
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
                    if (!Arrays.equals(usableImage.processedImage.image.jpeg, existing.processedImage.image.jpeg)) {
                        forceInsert(mean, usableImage, kdTree);
                    }
                }
            }
        }
        return new MatchingIndex(colorSpace, list, kdTree, index.drillDown, accuracy, index.width, index.height);
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

    public Image match(int[] targetRgb, int w, int h, boolean allowReuse) {
        double[] ddMeanTarget = Util.mean(targetRgb, w, h, drillDown, drillDown, colorSpace);
        double[] targetMean = Util.mean(ddMeanTarget);
        Iterable<UsableImage> candidates;
        if (kdTree == null) {
            candidates = Iterables.filter(list, UNUSED);
        } else {
            try {
                // TODO not a constant.
                int n = accuracy == accuracy.FASTEST ? 1 : 20;
                candidates = kdTree.nearest(targetMean, n, CHECKER);
            } catch (KeySizeException ex) {
                throw new IllegalStateException("Programmer error", ex);
            }
        }
        UsableImage nearest = null;
        double minDistance = Double.MAX_VALUE;
        for (UsableImage usableImage : candidates) {
            double distance = Util.euclidianDistance(Util.meanRgbs2ColorSpace(usableImage.processedImage.ddMeanRgb, colorSpace), ddMeanTarget);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = usableImage;
            }
        }
        if (!allowReuse) {
            nearest.used.set(true);
        }
        return nearest.processedImage.image;
    }

    public void resetUsage() {
        for (UsableImage usableImage : list) {
            usableImage.used.set(false);
        }
    }

    private static class UsableImage {

        private final ProcessedImage processedImage;
        private final AtomicBoolean used = new AtomicBoolean();

        public UsableImage(ProcessedImage processedImage) {
            this.processedImage = processedImage;
        }
    }
}
