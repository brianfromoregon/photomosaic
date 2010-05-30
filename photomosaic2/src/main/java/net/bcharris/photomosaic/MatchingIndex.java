package net.bcharris.photomosaic;

import com.google.common.collect.Lists;
import edu.wlu.cs.levy.CG.Checker;
import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import net.bcharris.photomosaic.ProcessedIndex.ProcessedJpeg;

/**
 * An index used for matching.
 */
public class MatchingIndex {

    public enum Accuracy {

        EXACT, APPROXIMATE, FASTEST;
    }
    private static final Checker<UsableJpeg> CHECKER = new Checker<UsableJpeg>() {

        @Override
        public boolean usable(UsableJpeg v) {
            return !v.used.get();
        }
    };
    public final ColorSpace colorSpace;
    public final int size;
    public final int jpegWidth, jpegHeight;
    private final List<UsableJpeg> list;
    private final KDTree<UsableJpeg> kdTree;
    private final int drillDown;
    private final Accuracy accuracy;
    private static final Random RANDOM = new Random();

    private MatchingIndex(ColorSpace colorSpace, List<UsableJpeg> list, KDTree<UsableJpeg> kdTree, int drillDown, Accuracy accuracy, int jpegWidth, int jpegHeight) {
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
        List<UsableJpeg> list = Lists.newArrayList();
        for (ProcessedJpeg jpeg : index.jpegs) {
            list.add(new UsableJpeg(jpeg));
        }
        KDTree<UsableJpeg> kdTree = null;
        if (accuracy == Accuracy.APPROXIMATE || accuracy == Accuracy.FASTEST) {
            kdTree = new KDTree<UsableJpeg>(3);
            for (UsableJpeg usableJpeg : list) {
                double[] mean = Util.meanRgbs2ColorSpace(usableJpeg.processedJpeg.meanRgb, colorSpace);
                try {
                    kdTree.insert(mean, usableJpeg);
                } catch (KeySizeException ex) {
                    throw new IllegalStateException("Programmer error", ex);
                } catch (KeyDuplicateException ex) {
                    UsableJpeg existing;
                    try {
                        existing = kdTree.search(mean);
                    } catch (KeySizeException ex1) {
                        throw new IllegalStateException("Programmer error", ex1);
                    }
                    // If the jpegs really are different, force an insert
                    if (!Arrays.equals(usableJpeg.processedJpeg.bytes, existing.processedJpeg.bytes)) {
                        forceInsert(mean, usableJpeg, kdTree);
                    }
                }
            }
        }
        return new MatchingIndex(colorSpace, list, kdTree, index.drillDown, accuracy, index.width, index.height);
    }

    private static void forceInsert(double[] mean, UsableJpeg usableJpeg, KDTree<UsableJpeg> kdTree) {
        double[] copy = Arrays.copyOf(mean, mean.length);
        while (true) {
            try {
                double d = RANDOM.nextInt(1000) + 1;
                d *= 1e-10;
                for (int i = 0; i < copy.length; i++) {
                    copy[i] += d;
                }
                kdTree.insert(copy, usableJpeg);
                return;
            } catch (KeySizeException ex) {
                throw new IllegalStateException("Programmer error", ex);
            } catch (KeyDuplicateException ex) {
            }
        }
    }

    public byte[] match(int[] targetRgb, int w, int h, boolean allowReuse) {
        double[] ddMeanTarget = Util.mean(targetRgb, w, h, drillDown, drillDown, colorSpace);
        double[] targetMean = Util.mean(ddMeanTarget);
        List<UsableJpeg> candidates;
        if (kdTree == null) {
            candidates = list;
        } else {
            try {
                // TODO not a constant.
                int n = accuracy == accuracy.FASTEST ? 1 : 20;
                candidates = kdTree.nearest(targetMean, n, CHECKER);
            } catch (KeySizeException ex) {
                throw new IllegalStateException("Programmer error", ex);
            }
        }
        UsableJpeg nearest = null;
        double minDistance = Double.MAX_VALUE;
        for (UsableJpeg usableJpeg : candidates) {

            double distance = Util.euclidianDistance(Util.meanRgbs2ColorSpace(usableJpeg.processedJpeg.ddMeanRgb, colorSpace), ddMeanTarget);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = usableJpeg;
            }
        }
        if (!allowReuse) {
            nearest.used.set(true);
        }
        return nearest.processedJpeg.bytes;
    }

    public void resetUsage() {
        for (UsableJpeg usableJpeg : list) {
            usableJpeg.used.set(false);
        }
    }

    private static class UsableJpeg {

        private final ProcessedJpeg processedJpeg;
        private final AtomicBoolean used = new AtomicBoolean();

        public UsableJpeg(ProcessedJpeg processedJpeg) {
            this.processedJpeg = processedJpeg;
        }
    }
}
