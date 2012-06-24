package com.brianfromoregon.photomosaic;

import java.io.File;

/**
 *
 */
public class Env {
    public static File convertExe() {
        File f = new File("D:\\ImageMagick-6.7.7-Q16\\convert.exe");

        if (!f.isFile() || !f.exists()) {
            throw new RuntimeException("The specified ImageMagick convert app is invalid: " + f.getAbsolutePath());
        }

        return f;
    }
}
