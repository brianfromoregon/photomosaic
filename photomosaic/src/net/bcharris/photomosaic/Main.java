package net.bcharris.photomosaic;

import java.io.File;
import java.io.IOException;
import net.bcharris.photomosaic.swing.MosaicDesigner;

/**
 * The point of this class is to read in a number of images, all with identical
 * dimensions, and spit out a script containing ImageMagick commands which will
 * create the final mosaic.
 * 
 * Originally, the mosaics were generated in Java via writing BufferedImages to
 * disk; however, the JVM heap size became too small to do so for large mosaics.
 * This code still exists, however, in ImagePalette.
 * 
 * Also, the source images were originally pre-processed (filtered, resized) in
 * Java, however it turns out that a high quality thumbnail operation is hard to
 * code in Java, and it takes much too long.  So now this pre processing step is
 * left to a unix shell script which invokes ImageMagick.
 * 
 * So, now the Java app only does the work of figuring out which image should go
 * where and lets ImageMagick do the pre and post processing work.  It is much
 * faster..
 * 
 * @author brian
 */
public class Main
{
	private static String montageCmd = "montage";

	public static void main(String[] args)
	{

	}
	
	private static void createMosaicScript(File sourceImageDir, File targetImage, int numImagesTall, int numImagesWide, String scriptOutputDir, int step)
			throws IOException
	{
//		 The utility to help create the mosaic.
//		ImagePalette imagePalette = new ImagePalette(5, 8);
//		
//		// Add source images to palette.
//		imagePalette.addImages(sourceImageDir);
//
//		// A grid of images that, when compacted into 1 large image w.r.t. their
//		// ordering in the grid, will compose the desired mosaic.
//		File[][] imageGrid = toFiles(imagePalette.bestMatches(ImageIO.read(targetImage), numImagesWide, numImagesTall, 1));
//
//		// Spit out the script
//		System.out.println(ImageMagickUtil.generateCommandsToCreateMosaic(montageCmd, imageGrid, step, scriptOutputDir));
		
		new MosaicDesigner().setVisible(true);
	}
	
	private static int gcd(int n, int d)
	{
		if (d == 0)
		{
			return 1;
		}
		int r = n % d;
		return (r == 0) ? d : gcd(d, r);
	}
	
	private static File[][] toFiles(ImageFileContext[][] contexts)
	{
		File[][] files = new File[contexts.length][];
		
		for (int i = 0; i < contexts.length; i++)
		{
			files[i] = new File[contexts[i].length];
			for (int j = 0; j < contexts[i].length; j++)
			{
				files[i][j] = contexts[i][j].file;
			}
		}
		
		return files;
	}
}
