package com.brianfromoregon.tiles;

import com.google.common.base.Suppliers;
import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import com.brianfromoregon.tiles.Index.Image;
import org.junit.Ignore;

@Ignore
public class TestUtil {

    static Image image(String resourceName) {
        try {
            return new Image(ByteStreams.toByteArray(TestUtil.class.getResourceAsStream(resourceName)), new File(resourceName));
        } catch (Exception ex) {
            throw Throwables.propagate(ex);
        }
    }

    static BufferedImage bufferedImage(String resourceName) {
        try {
            return Util.bytesToBufferedImage(ByteStreams.toByteArray(TestUtil.class.getResourceAsStream(resourceName)));
        } catch (IOException ex) {
            throw Throwables.propagate(ex);
        }
    }
}
