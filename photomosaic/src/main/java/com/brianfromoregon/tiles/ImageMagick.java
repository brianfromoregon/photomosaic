package com.brianfromoregon.tiles;

import com.brianfromoregon.tiles.persist.DataStore;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;

/**
 *
 */
public class ImageMagick {

    private static ImageMagick INSTANCE;

    @Inject DataStore dataStore;

    @PostConstruct public void init() {
        INSTANCE = this;
    }

    public static void setImageMagickDir(String dir) {
        INSTANCE.dataStore.saveImageMagickDir(dir);
    }

    public static String getImageMagickDir() {
        return INSTANCE.dataStore.loadImageMagickDir();
    }

    public static File convertExe() {
        return new File(getImageMagickDir(), "convert.exe");
    }

    public static File montageExe() {
        return new File(getImageMagickDir(), "montage.exe");
    }

    public static boolean isImageMagickValid() {
        try {
            return convertExe().isFile() && montageExe().isFile();
        } catch (Exception e) {
            return false;
        }
    }
}
