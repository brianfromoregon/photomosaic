package com.brianfromoregon.tiles;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import java.net.URI;
import java.util.IdentityHashMap;

/**
 * An immutable representation of a collection of source images with which mosaics can be created.
 */
public final class Index {

    public final ImmutableList<Image> images;
    public final int width;
    public final int height;

    /**
     * Create a new index.
     * @param images A list of images, each with the given width and height.
     * @param width The width of each image.
     * @param height The height of each image.
     */
    public Index(ImmutableList<Image> images, int width, int height) {
        this.images = images;
        this.width = width;
        this.height = height;
    }

    public IdentityHashMap<Image, Integer> indexedImages() {
        IdentityHashMap<Image, Integer> srcPos = Maps.newIdentityHashMap();
        for (int i = 0; i < images.size(); i++)
            srcPos.put(images.get(i), i);
        return srcPos;
    }

    public static class Image {

        public final byte[] jpeg;
        public final URI uri;

        public Image(byte[] jpeg, URI uri) {
            this.jpeg = jpeg;
            this.uri = uri;
        }

    }
}
