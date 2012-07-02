package com.brianfromoregon.tiles;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public enum SamplePalette {
    SOLID_COLORS {
        @Override
        public Index generate() {
            ImmutableList.Builder<Index.Image> images = ImmutableList.builder();
            for (int r = 0; r < 256; r += 50) {
                for (int g = 0; g < 256; g += 50) {
                    for (int b = 0; b < 256; b += 50) {
                        BufferedImage image = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);

                        Graphics2D g2d = image.createGraphics();
                        g2d.setColor(new Color(r, g, b));
                        g2d.fillRect(0, 0, W, H);

                        images.add(new Index.Image(Util.bufferedImageToBytes(image, "gif"), DUMMY_URI));
                    }
                }
            }

            return new Index(images.build(), W, H);
        }
    }, GRAYSCALE {
        @Override
        public Index generate() {
            ImmutableList.Builder<Index.Image> images = ImmutableList.builder();
            for (int i = 0; i < 256; i += 10) {
                BufferedImage image = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);

                Graphics2D g2d = image.createGraphics();
                g2d.setColor(new Color(i, i, i));
                g2d.fillRect(0, 0, W, H);

                images.add(new Index.Image(Util.bufferedImageToBytes(image, "gif"), DUMMY_URI));
            }

            return new Index(images.build(), W, H);
        }
    };

    final static int W=32, H=22;
    static final Supplier<URI> DUMMY_URI = new Supplier<URI>() {
        @Override public URI get() {
            throw new IllegalStateException("This dummy palette uses in-memory images");
        }
    };
    public abstract Index generate();
}
