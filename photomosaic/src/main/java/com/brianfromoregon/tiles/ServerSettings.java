package com.brianfromoregon.tiles;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

/**
 *
 */
public class ServerSettings {
    private static final File FILE = new File(System.getProperty("user.home"), "tiles.properties");

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

    private static File getImageMagickDir() throws IllegalStateException, IllegalArgumentException {
        String key = "ImageMagick";
        String val = loadProperties(FILE).get(key);
        if (val == null)
            throw new IllegalStateException(String.format("Need a line '%s=/path/to/ImageMagick/dir' in %s", key, FILE));
        File dir = new File(val);
        if (!dir.exists() || !dir.isDirectory())
            throw new IllegalArgumentException(String.format("ImageMagick directory specified in %s not found: %s", FILE, val));
        return dir;
    }

    public static File convertExe() {
        return new File(getImageMagickDir(), "convert.exe");
    }

    public static File montageExe() {
        return new File(getImageMagickDir(), "montage.exe");
    }
}
