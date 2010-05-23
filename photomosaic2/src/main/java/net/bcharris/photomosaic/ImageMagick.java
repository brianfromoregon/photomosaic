package net.bcharris.photomosaic;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

public class ImageMagick {

    private final File convertApp;

    public ImageMagick(File installDir) {
        this.convertApp = new File(installDir, "convert");
    }

//    public Index createIndex(Iterable<File> sourceImages, File emptyTargetDir, int width, int height, int drillDown, StringBuilder warningLog) {
//        ImmutableList.Builder<SourceImageInfo> indexBuilder = ImmutableList.builder();
//        int imgNum = 0;
//        for (File srcImg : sourceImages) {
//            File targetImage = new File(emptyTargetDir, imgNum + ".png");
//            {
//                // I've forgotten why I'm using this strange series of resize arguments but a comment I wrote a long time ago said this:
//                // "the multiple resize commands accomplish optimistic (for landscape) cropping."
//                CommandLine commandLine = new CommandLine(convertApp.getAbsolutePath());
//                commandLine.addArgument(srcImg.getAbsolutePath());
//                commandLine.addArgument("-auto-orient");
//                commandLine.addArgument("-strip");
//                commandLine.addArgument("-resize");
//                commandLine.addArgument("x" + height * 2);
//                commandLine.addArgument("-resize");
//                commandLine.addArgument("\"" + width * 2 + "x<\"");
//                commandLine.addArgument("-resize");
//                commandLine.addArgument("50%");
//                commandLine.addArgument("-gravity");
//                commandLine.addArgument("center");
//                commandLine.addArgument("-crop");
//                commandLine.addArgument(width + "x" + height + "+0+0");
//                commandLine.addArgument("+repage");
//                commandLine.addArgument("PNG24:" + targetImage.getAbsolutePath());
//                System.out.println(commandLine.toString());
//                DefaultExecutor executor = new DefaultExecutor();
//                ByteArrayOutputStream stdout = new ByteArrayOutputStream();
//                ByteArrayOutputStream stderr = new ByteArrayOutputStream();
//                PumpStreamHandler handler = new PumpStreamHandler(stdout, stderr);
//                executor.setStreamHandler(handler);
//                try {
//                    executor.execute(commandLine);
//                } catch (Exception ex) {
//                    warningLog.append(String.format("Problem shrinking image '%s'\n", srcImg.getAbsolutePath()));
//                    warningLog.append(Throwables.getStackTraceAsString(ex));
//                    continue;
//                }
//            }
//
//            File tinyImage = new File(emptyTargetDir, imgNum + "-" + drillDown + ".png");
//            {
//                CommandLine commandLine = new CommandLine(convertApp.getAbsolutePath());
//                commandLine.addArgument(targetImage.getAbsolutePath());
//                commandLine.addArgument("-resize");
//                commandLine.addArgument(drillDown + "x" + drillDown + "!");
//                commandLine.addArgument("PNG24:" + tinyImage.getAbsolutePath());
//                System.out.println(commandLine.toString());
//                DefaultExecutor executor = new DefaultExecutor();
//                ByteArrayOutputStream stdout = new ByteArrayOutputStream();
//                ByteArrayOutputStream stderr = new ByteArrayOutputStream();
//                PumpStreamHandler handler = new PumpStreamHandler(stdout, stderr);
//                executor.setStreamHandler(handler);
//                try {
//                    executor.execute(commandLine);
//                } catch (Exception ex) {
//                    warningLog.append(String.format("Problem making tiny image from shrunk image '%s'\n", targetImage.getAbsolutePath()));
//                    warningLog.append(Throwables.getStackTraceAsString(ex));
//                    continue;
//                }
//            }
//            BufferedImage tiny;
//            try {
//                tiny = ImageIO.read(tinyImage);
//            } catch (IOException ex) {
//                warningLog.append(String.format("Problem thumbing image '%s'\n", srcImg.getAbsolutePath()));
//                warningLog.append(Throwables.getStackTraceAsString(ex));
//                continue;
//            }
//            tinyImage.delete();
//            byte[] meanRgbs = new byte[drillDown * drillDown * 3];
//            int[] rgbArray = new int[drillDown * drillDown];
//            tiny.getRGB(0, 0, drillDown, drillDown, rgbArray, 0, drillDown);
//            for (int i = 0; i < rgbArray.length; i++) {
//                meanRgbs[3 * i] = (byte) (rgbArray[i] >> 16 & 0xff);
//                meanRgbs[3 * i + 1] = (byte) (rgbArray[i] >> 8 & 0xff);
//                meanRgbs[3 * i + 2] = (byte) (rgbArray[i] & 0xff);
//            }
//
//            indexBuilder.add(new SourceImageInfo(targetImage.getAbsolutePath(), width, height, meanRgbs));
//
//            imgNum++;
//        }
//        return new Index(indexBuilder.build(), drillDown);
//    }
}
