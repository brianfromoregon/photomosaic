package com.brianfromoregon.tiles;

import java.io.File;

/**
 *
 */
public class Env {
    static String imDir = "C:\\ImageMagick-6.7.8-Q16";
    public static File convertExe() {
        File f = new File(imDir + "\\convert.exe");

        if (!f.isFile() || !f.exists()) {
            throw new RuntimeException("The specified ImageMagick convert app is invalid: " + f.getAbsolutePath());
        }

        return f;
    }
}
