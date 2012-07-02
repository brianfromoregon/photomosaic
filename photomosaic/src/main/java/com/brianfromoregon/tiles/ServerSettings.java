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
    private static final String FILE_NAME = "tiles.properties";
    private static final ImmutableMap<String, String> props;

    static {
        File settingsFile = new File(System.getProperty("user.home"), FILE_NAME);
        props = loadProperties(settingsFile);
    }

    private static ImmutableMap<String, String> loadProperties(File file) {
        try (Reader reader = Files.newReader(file, Charsets.UTF_8)) {
            Properties props = new Properties();
            props.load(reader);
            return Maps.fromProperties(props);
        } catch (IOException e) {
            Log.log("Something was wrong with ~/%s: %s", FILE_NAME, e.getMessage());
            return ImmutableMap.of();
        }
    }

    private static File getImageMagickDir() throws IllegalStateException, IllegalArgumentException {
        String key = "ImageMagick";
        String val = props.get(key);
        if (val == null)
            throw new IllegalStateException(String.format("Need a line '%s=/path/to/ImageMagick/dir' in ~/%s", key, FILE_NAME));
        File dir = new File(val);
        if (!dir.exists() || !dir.isDirectory())
            throw new IllegalArgumentException(String.format("ImageMagick directory specified in ~/%s not found: %s", FILE_NAME, val));
        return dir;
    }

    public static File convertExe() {
        return new File(getImageMagickDir(), "convert.exe");
    }

    public static File montageExe() {
        return new File(getImageMagickDir(), "montage.exe");
    }
}
