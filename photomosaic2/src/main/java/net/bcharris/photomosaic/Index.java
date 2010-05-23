package net.bcharris.photomosaic;

import com.google.common.collect.ImmutableList;
import java.io.Serializable;

/**
 * An immutable representation of a collection of source images with which mosaics can be created.
 */
public final class Index implements Serializable {

    public final ImmutableList<byte[]> jpegs;
    public final int width;
    public final int height;

    /**
     * Create a new index.
     * @param jpegs The list of JPEGs, each with the given width and height.
     * @param width The width of each image.
     * @param height The height of each image.
     */
    public Index(ImmutableList<byte[]> jpegs, int width, int height) {
        this.jpegs = jpegs;
        this.width = width;
        this.height = height;
    }
}
