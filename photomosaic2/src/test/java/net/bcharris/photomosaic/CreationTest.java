package net.bcharris.photomosaic;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import net.bcharris.photomosaic.Index.Image;
import org.junit.Test;
import static org.junit.Assert.*;

public class CreationTest {

    @Test
    public void simpleCircle() {
        ArrayList<Image> images = Lists.newArrayList();
        images.add(image("/circle/tiles/tile_0.png"));
        images.add(image("/circle/tiles/tile_1.png"));
        images.add(image("/circle/tiles/tile_2.png"));
        images.add(image("/circle/tiles/tile_3.png"));
        Index index = new Index(images, 50, 50);
        Creator creator = new Creator();
        BufferedImage target = bufferedImage("/circle/circle.png");
        byte[][][] expected = new byte[2][2][];
        expected[0][0] = images.get(0).jpeg;
        expected[0][1] = images.get(1).jpeg;
        expected[1][0] = images.get(2).jpeg;
        expected[1][1] = images.get(3).jpeg;
        assertArrayEquals(expected, creator.designMosaic(MatchingIndex.create(ProcessedIndex.process(index, 2), ColorSpace.SRGB, MatchingIndex.Accuracy.APPROXIMATE), target, true, 2).jpegLayout);
        assertArrayEquals(expected, creator.designMosaic(MatchingIndex.create(ProcessedIndex.process(index, 3), ColorSpace.CIELAB, MatchingIndex.Accuracy.APPROXIMATE), target, true, 2).jpegLayout);
        assertArrayEquals(expected, creator.designMosaic(MatchingIndex.create(ProcessedIndex.process(index, 6), ColorSpace.SRGB, MatchingIndex.Accuracy.APPROXIMATE), target, true, 2).jpegLayout);
    }

    private Image image(String resourceName) {
        try {
            return new Image(ByteStreams.toByteArray(getClass().getResourceAsStream(resourceName)), resourceName);
        } catch (IOException ex) {
            throw Throwables.propagate(ex);
        }
    }

    private BufferedImage bufferedImage(String resourceName) {
        try {
            return Util.jpegToBufferedImage(ByteStreams.toByteArray(getClass().getResourceAsStream(resourceName)));
        } catch (IOException ex) {
            throw Throwables.propagate(ex);
        }
    }
}
