package com.brianfromoregon.tiles;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

/**
 *
 */
public class ServerSettings {
    public static class ImageMagickNotFound extends Exception {
        public ImageMagickNotFound(String message) {
            super(message);
        }
    }

    private static final File FILE = new File(System.getProperty("user.home"), "tiles.properties");
    private static final String IM_KEY = "ImageMagick";

    private static ImmutableMap<String, String> loadProperties(File file) {
        try (Reader reader = Files.newReader(file, Charsets.UTF_8)) {
            Properties props = new Properties();
            props.load(reader);
            return Maps.fromProperties(props);
        } catch (IOException e) {
            Log.log("Something was wrong with %s: %s", FILE, e.getMessage());
            return ImmutableMap.of();
        }
    }

    public static File getImageMagickDir() throws ImageMagickNotFound {
        String val = loadProperties(FILE).get(IM_KEY);
        if (val == null)
            throw new ImageMagickNotFound(String.format("Need a line '%s=/path/to/ImageMagick/dir' in %s", IM_KEY, FILE));
        File dir = new File(val);
        if (!dir.exists() || !dir.isDirectory())
            throw new ImageMagickNotFound(String.format("ImageMagick directory specified in %s not found: %s", FILE, val));
        return dir;
    }

    public static File convertExe() {
        try {
            return new File(getImageMagickDir(), "convert.exe");
        } catch (ImageMagickNotFound e) {
            throw Throwables.propagate(e);
        }
    }

    public static File montageExe() {
        try {
            return new File(getImageMagickDir(), "montage.exe");
        } catch (ImageMagickNotFound e) {
            throw Throwables.propagate(e);
        }
    }

    public static boolean isImageMagickAvailable() {
        try {
            getImageMagickDir();
            return true;
        } catch (ImageMagickNotFound e) {
            return false;
        }
    }

    public static void createSettingsFile(File imageMagickDir) {
        try (Writer writer = Files.newWriter(FILE, Charsets.UTF_8)) {
            Properties p = new Properties();
            p.setProperty(IM_KEY, imageMagickDir.getAbsolutePath());
            p.store(writer, null);
        } catch (IOException e) {
            Log.log(e.getMessage());
        }
    }
}
