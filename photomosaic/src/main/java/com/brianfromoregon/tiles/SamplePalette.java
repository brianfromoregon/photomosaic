package com.brianfromoregon.tiles;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public enum SamplePalette {
    SOLID_COLORS {
        @Override
        public Index generate(int w, int h) {
            ImmutableList.Builder<Index.Image> images = ImmutableList.builder();
            for (int r = 0; r < 256; r += 50) {
                for (int g = 0; g < 256; g += 50) {
                    for (int b = 0; b < 256; b += 50) {
                        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                        Graphics2D g2d = image.createGraphics();
                        g2d.setColor(new Color(r, g, b));
                        g2d.fillRect(0, 0, w, h);

                        images.add(new Index.Image(Util.bufferedImageToBytes(image, "gif"), new File(Joiner.on(',').join(r,g,b))));
                    }
                }
            }

            return new Index(images.build(), w, h);
        }
    }, GRAYSCALE {
        @Override
        public Index generate(int w, int h) {
            ImmutableList.Builder<Index.Image> images = ImmutableList.builder();
            for (int i = 0; i < 256; i += 10) {
                BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                Graphics2D g2d = image.createGraphics();
                g2d.setColor(new Color(i, i, i));
                g2d.fillRect(0, 0, w, h);

                images.add(new Index.Image(Util.bufferedImageToBytes(image, "gif"), new File(String.valueOf(i))));
            }

            return new Index(images.build(), w, h);
        }
    };

    public abstract Index generate(int w, int h);
}
