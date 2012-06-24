package com.brianfromoregon.photomosaic;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * An immutable representation of a collection of source images with which mosaics can be created.
 */
public final class Index implements Serializable {

    public final ArrayList<Image> images;
    public final int width;
    public final int height;

    /**
     * Create a new index.
     * @param images A list of images, each with the given width and height.
     * @param width The width of each image.
     * @param height The height of each image.
     */
    public Index(ArrayList<Image> images, int width, int height) {
        images.trimToSize();
        this.images = images;
        this.width = width;
        this.height = height;
    }

    public static class Image implements Serializable {

        public final byte[] jpeg;
        public final String url;

        /**
         *
         * @param jpeg
         * @param url
         */
        public Image(byte[] jpeg, String url) {
            this.jpeg = jpeg;
            this.url = url;
        }
        private static final long serialVersionUID = 0;
    }
    private static final long serialVersionUID = 0;
}
