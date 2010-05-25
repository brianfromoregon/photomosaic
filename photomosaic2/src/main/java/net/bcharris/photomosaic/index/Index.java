package net.bcharris.photomosaic.index;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * An immutable representation of a collection of source images with which mosaics can be created.
 */
public final class Index implements Serializable {

    public final ArrayList<byte[]> jpegs;
    public final int width;
    public final int height;

    /**
     * Create a new index.
     * @param jpegs The list of JPEGs, each with the given width and height.
     * @param width The width of each image.
     * @param height The height of each image.
     */
    public Index(ArrayList<byte[]> jpegs, int width, int height) {
        jpegs.trimToSize();
        this.jpegs = jpegs;
        this.width = width;
        this.height = height;
    }
    private static final long serialVersionUID = 0;
}
