package net.bcharris.mosaic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Mosaic
{
	private static final Log log = LogFactory.getLog(Mosaic.class);

	private ImagePalette palette;

	public Mosaic(ImagePalette palette)
	{
		this.palette = palette;
	}

	public void doMosaic(File target, File dest, int numWide, int numTall,  int sliceWidth, int sliceHeight, int maxSameImageUsage)
	{
		BufferedImage targetImage = null;

		try
		{
			targetImage = ImageIO.read(target);
		}
		catch (IOException e)
		{
			log.error("while reading target image", e);
		}

		if (targetImage == null)
		{
			return;
		}

		BufferedImage mosaic = null;

		try
		{
			mosaic = palette.createMosaic(targetImage, numWide, numTall, sliceWidth, sliceHeight, maxSameImageUsage);
		}
		catch (IOException e)
		{
			log.error("while reading target image", e);
		}

		try
		{
			log.info("Writing mosaic to " + dest.getAbsolutePath());
			ImageIO.write(mosaic, "png", dest);
			log.info("Done writing mosaic");
		}
		catch (IOException e)
		{
			log.error("while writing mosaic image", e);
		}
	}

    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
    private static final String srcArg="srcLoc", targetArg="targetImg", destArg = "destFile";
	
	public static void main(String[] args) throws Exception
	{
		File src, dest, target;
		Mosaic mosaic;

		if (System.getProperty(srcArg) == null || System.getProperty(targetArg) == null || System.getProperty(destArg) == null)
		{
			log.fatal(LINE_SEPARATOR + usage());
			return;
		}

		src = new File(System.getProperty(srcArg));
		target = new File(System.getProperty(targetArg));
		dest = new File(System.getProperty(destArg));

		if (!src.exists())
		{
			log.fatal("Non-existant source location specified in argument: " +srcArg + LINE_SEPARATOR + usage());
			return;
		}
		if (!target.exists())
		{
			log.fatal("Non-existant target location specified in argument: " +targetArg + LINE_SEPARATOR + usage());
			return;
		}
		dest.delete();
		if (dest.exists())
		{
			log.fatal("Invalid destination argument, directory in the way at: " +destArg + LINE_SEPARATOR + usage());
			return;
		}
		
		try
		{
			BeanFactory factory = new ClassPathXmlApplicationContext("context.xml");
			mosaic = (Mosaic) factory.getBean("mosaic");
		}
		catch (Throwable t)
		{
			log.fatal("Program startup failed.", t);
			log.fatal(LINE_SEPARATOR + usage());
			return;
		}
		
		try
		{
			mosaic.palette.addImages(src);
			mosaic.doMosaic(target, dest, 72, 96, 150, 113, 100);
		}
		catch (Throwable t)
		{
			log.error("", t);
			return;
		}
	}
	
	private static String usage()
	{
		return "" +
				"Usage: java -jar App.jar -DsrcLoc=B -DtargetImg=C -DdestFile=D" + LINE_SEPARATOR + 
				"\tB - The directory or image file to recursively process the image files within." + LINE_SEPARATOR +
				"\tC - The image file to recreate as a mosaic." + LINE_SEPARATOR +
				"\tD - Where to write the created mosaic image file.";
	}
}
