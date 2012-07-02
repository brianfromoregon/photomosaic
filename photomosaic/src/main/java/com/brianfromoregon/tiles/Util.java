package com.brianfromoregon.tiles;

import com.google.common.base.Throwables;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import static java.lang.Math.*;

public class Util {

    public static File createTempFile(String prefix, String suffix) {
        try {
            return File.createTempFile(prefix, suffix);
        } catch (IOException ex) {
            throw new RuntimeException("Fatal error: could not create temporary file", ex);
        }
    }

    public static BufferedImage bytesToBufferedImage(byte[] bytes) {
        try {
            return ImageIO.read(ByteStreams.newInputStreamSupplier(bytes).getInput());
        } catch (IOException ex) {
            throw new IllegalStateException("Programmer error", ex);
        }
    }

    public static byte[] bufferedImageToBytes(BufferedImage image, String format) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, format, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    public static int[] bufferedImageToRgb(BufferedImage img) {
        int[] rgbArray = new int[img.getWidth() * img.getHeight()];
        img.getRGB(0, 0, img.getWidth(), img.getHeight(), rgbArray, 0, img.getWidth());
        return rgbArray;
    }

    public static double[] mean(int[] packedRgb, int width, int height, int ddx, int ddy, ColorSpace colorSpace) {
        double[] sliceMeanColors = new double[ddx * ddy * 3];

        for (int i = 0; i < ddx; i++) {
            int xStart = (width * i) / ddx;
            int xEnd = (width * (i + 1)) / ddx;

            for (int j = 0; j < ddy; j++) {
                int yStart = (height * j) / ddy;
                int yEnd = (height * (j + 1)) / ddy;
                double sumR = 0, sumG = 0, sumB = 0;
                int total = 0;
                for (int x = xStart; x < xEnd; x++) {
                    for (int y = yStart; y < yEnd; y++) {
                        int pixel = packedRgb[y * width + x];
                        sumR += pixel >> 16 & 0xff;
                        sumG += pixel >> 8 & 0xff;
                        sumB += pixel & 0xff;
                        total++;
                    }
                }
                int index = 3 * (j * ddx + i);
                sliceMeanColors[index] = sumR / total;
                sliceMeanColors[index + 1] = sumG / total;
                sliceMeanColors[index + 2] = sumB / total;
            }
        }
        return meanRgbs2ColorSpace(sliceMeanColors, colorSpace);
    }

    public static double[] mean(double[] ddMean) {
        double[] mean = new double[3];
        double sum1 = 0;
        double sum2 = 0;
        double sum3 = 0;
        for (int i = 0; i < ddMean.length; i += 3) {
            sum1 += ddMean[i];
            sum2 += ddMean[i + 1];
            sum3 += ddMean[i + 2];
        }
        mean[0] = sum1 / (ddMean.length / 3);
        mean[1] = sum2 / (ddMean.length / 3);
        mean[2] = sum3 / (ddMean.length / 3);
        return mean;
    }

    public static double[] meanRgbs2ColorSpace(double[] meanRgbs, ColorSpace colorSpace) {
        if (colorSpace == ColorSpace.CIELAB) {
            double[] tmp = new double[3];
            double[] labs = new double[meanRgbs.length];
            for (int i = 0; i < meanRgbs.length; i += 3) {
                rgbToCIELAB(meanRgbs[i], meanRgbs[i + 1], meanRgbs[i + 2], tmp);
                labs[i] = tmp[0];
                labs[i + 1] = tmp[1];
                labs[i + 2] = tmp[2];
            }
            return labs;
        } else {
            return meanRgbs;
        }
    }

    /**
     * Convert RGB to CIELAB
     * From http://www.easyrgb.com/index.php?X=MATH
     * Observer = 2°, Illuminant = D65
     */
    public static void rgbToCIELAB(double R, double G, double B, double[] lab) {

        // RGB to XYZ
        double var_R = (R / 255d);
        double var_G = (G / 255d);
        double var_B = (B / 255d);

        if (var_R > 0.04045) {
            var_R = pow((var_R + 0.055) / 1.055, 2.4);
        } else {
            var_R = var_R / 12.92;
        }
        if (var_G > 0.04045) {
            var_G = pow((var_G + 0.055) / 1.055, 2.4);
        } else {
            var_G = var_G / 12.92;
        }
        if (var_B > 0.04045) {
            var_B = pow((var_B + 0.055) / 1.055, 2.4);
        } else {
            var_B = var_B / 12.92;
        }

        var_R = var_R * 100;
        var_G = var_G * 100;
        var_B = var_B * 100;

        double X = var_R * 0.4124 + var_G * 0.3576 + var_B * 0.1805;
        double Y = var_R * 0.2126 + var_G * 0.7152 + var_B * 0.0722;
        double Z = var_R * 0.0193 + var_G * 0.1192 + var_B * 0.9505;

        // XYZ to Lab
        double ref_X = 95.047;
        double ref_Y = 100.000;
        double ref_Z = 108.883;
        double var_X = X / ref_X;
        double var_Y = Y / ref_Y;
        double var_Z = Z / ref_Z;

        if (var_X > 0.008856) {
            var_X = pow(var_X, 1d / 3);
        } else {
            var_X = (7.787 * var_X) + (16d / 116);
        }
        if (var_Y > 0.008856) {
            var_Y = pow(var_Y, 1d / 3);
        } else {
            var_Y = (7.787 * var_Y) + (16d / 116);
        }
        if (var_Z > 0.008856) {
            var_Z = pow(var_Z, 1d / 3);
        } else {
            var_Z = (7.787 * var_Z) + (16d / 116);
        }

        double CIEL = (116 * var_Y) - 16;
        double CIEa = 500 * (var_X - var_Y);
        double CIEb = 200 * (var_Y - var_Z);
        lab[0] = CIEL;
        lab[1] = CIEa;
        lab[2] = CIEb;
    }

    public static double euclidianDistance(double[] d1, double[] d2) {
        double distance = 0;
        for (int i = 0; i < d1.length; i++) {
            double diff = d1[i] - d2[i];
            distance += diff * diff;
        }
        // Don't need to take square root because we're not interested in actual values, just their respective order.
//        return sqrt(distance);
        return distance;
    }

    public static Index readIndex(File indexFile) {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(indexFile)));
            return (Index) in.readObject();
        } catch (Exception ex) {
            throw new RuntimeException("Fatal error, could not read index from specified file: " + indexFile.getAbsolutePath(), ex);
        } finally {
            Closeables.closeQuietly(in);
        }
    }

    public static void writeIndex(Index index, File outputFile) {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
            out.writeObject(index);
        } catch (Exception ex) {
            throw new RuntimeException("Fatal error, could not write index to specified file: " + outputFile.getAbsolutePath(), ex);
        } finally {
            Closeables.closeQuietly(out);
        }
    }

    public static BufferedImage readImage(File imageFile) {
        try {
            return ImageIO.read(imageFile);
        } catch (IOException ex) {
            throw new RuntimeException("Fatal error, could not read target image: " + imageFile.getAbsolutePath(), ex);
        }
    }

    public static void installEscapeCloseOperation(final JDialog dialog) {
        Action dispatchClosing = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent event) {
                dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
            }
        };
        JRootPane root = dialog.getRootPane();
        String dispatchWindowClosingActionMapKey = "net.bcharris.tiles:WINDOW_CLOSING";
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), dispatchWindowClosingActionMapKey);
        root.getActionMap().put(dispatchWindowClosingActionMapKey, dispatchClosing);
    }

    public static int maxNumWide(Index index, int targetW, int targetH) {
        return (int) (index.images.size() / Math.floor(index.images.size() / Math.sqrt((double) (targetW * index.height * index.images.size()) / (targetH * index.width))));
    }
}
