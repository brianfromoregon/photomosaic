package net.bcharris.photomosaic.create;

import com.google.common.collect.Lists;
import edu.wlu.cs.levy.CG.Checker;
import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.bcharris.photomosaic.create.ProcessedIndex.ProcessedJpeg;

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
                try {
                    kdTree.insert(Util.meanRgbs2ColorSpace(usableJpeg.processedJpeg.meanRgb, colorSpace), usableJpeg);
                } catch (KeySizeException ex) {
                    throw new IllegalStateException("Programmer error", ex);
                } catch (KeyDuplicateException ex) {
                    // Not a problem.
                }
            }
        }
        return new MatchingIndex(colorSpace, list, kdTree, index.drillDown, accuracy, index.width, index.height);
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
