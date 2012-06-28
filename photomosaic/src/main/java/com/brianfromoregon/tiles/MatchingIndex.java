package com.brianfromoregon.tiles;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import com.brianfromoregon.tiles.MatchingIndex.UsableImage;
import com.brianfromoregon.tiles.Index.Image;

public abstract class MatchingIndex<T extends UsableImage> {

    public final int imageWidth, imageHeight;

    public MatchingIndex(int imageWidth, int imageHeight) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public Image match(int[] targetRgb, int w, int h, boolean allowReuse) {
        UsableImage matched = unusedMatch(targetRgb, w, h);
        if (!allowReuse) {
            matched.used.set(true);
        }
        return matched.image;
    }

    protected abstract UsableImage unusedMatch(int[] targetRgb, int w, int h);

    protected abstract Collection<? extends UsableImage> all();

    protected abstract static class UsableImage {

        protected final AtomicBoolean used = new AtomicBoolean();
        public final Image image;

        public UsableImage(Image image) {
            this.image = image;
        }
    }

    public void resetUsage() {
        for (UsableImage usableImage : all()) {
            usableImage.used.set(false);
        }
    }
}
