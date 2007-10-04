package net.bcharris.mosaic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

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

	public void doMosaic(File target, File dest, int numWide, int numTall, int sliceWidth, int sliceHeight,
			int maxSameImageUsage)
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

	private static final String srcArg = "srcLoc", targetArg = "targetImg", destArg = "destFile";

	public static void main(String[] args) throws Exception
	{
		File src, dest, target;
		Mosaic mosaic;

		if (System.getProperty(srcArg) == null || System.getProperty(targetArg) == null
				|| System.getProperty(destArg) == null)
		{
			log.fatal(LINE_SEPARATOR + usage());
			return;
		}

		src = new File(System.getProperty(srcArg));
		target = new File(System.getProperty(targetArg));
		dest = new File(System.getProperty(destArg));

		if (!src.exists())
		{
			log.fatal("Non-existant source location specified in argument: " + srcArg + LINE_SEPARATOR + usage());
			return;
		}
		if (!target.exists())
		{
			log.fatal("Non-existant target location specified in argument: " + targetArg + LINE_SEPARATOR + usage());
			return;
		}
		dest.delete();
		if (dest.exists())
		{
			log.fatal("Invalid destination argument, directory in the way at: " + destArg + LINE_SEPARATOR + usage());
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
			mosaic.palette.contexts = loadContexts();
			mosaic.palette.addImages(src);

			int numWide = 72, numTall = 96;

			ImageFileContext[][] bestMatches = mosaic.palette.bestMatches(ImageIO.read(target), numWide, numTall, 5);
			// String[][] bestMatches = new String[numWide][numTall];
			//
			// for (int i = 0; i < numWide; i++)
			// {
			// for (int j = 0; j < numTall; j++)
			// {
			// bestMatches[i][j] = i + "_" + j;
			// }
			// }

			int denom = 12;

			StringBuilder all = new StringBuilder();
			StringBuilder sections = new StringBuilder();
			for (int j = 0; j < numTall / denom; j++)
			{
				for (int i = 0; i < numWide / denom; i++)
				{
					String sectionFile = "section/" + j + "_" + i + ".png";
					StringBuilder section = new StringBuilder();
					for (int h = j * denom; h < (j + 1) * denom; h++)
					{
						for (int w = i * denom; w < (i + 1) * denom; w++)
						{
							section.append(" \"").append(bestMatches[w][h].file.getAbsolutePath()).append("\"");
							// section.append(" \"").append(bestMatches[w][h]).append("\"");
						}
					}
					all.append("montage").append(section.toString()).append(" -geometry +0+0 ").append(sectionFile)
							.append("\n");
					sections.append(" ").append(" \"").append(sectionFile).append("\"");
				}
			}

			all.append("montage").append(sections.toString()).append(" -tile x").append(new Integer(numTall/denom)).append(" -geometry +0+0 ").append("out.png").append("\n");
			all.append("montage").append(sections.toString().replace(".png", ".jpg")).append(" -tile x").append(new Integer(numTall/denom)).append(" -geometry +0+0 ").append("out.png").append("\n");
			System.out.println(all.toString());
			// mosaic.doMosaic(target, dest, numWide, numTall, 300, 225, 5);
			// saveContexts(mosaic.palette.contexts);
		}
		catch (Throwable t)
		{
			log.error("", t);
			return;
		}
	}

	private static String usage()
	{
		return "" + "Usage: java -jar App.jar -DsrcLoc=B -DtargetImg=C -DdestFile=D" + LINE_SEPARATOR
				+ "\tB - The directory or image file to recursively process the image files within." + LINE_SEPARATOR
				+ "\tC - The image file to recreate as a mosaic." + LINE_SEPARATOR
				+ "\tD - Where to write the created mosaic image file.";
	}

	private static Map<ImageFileContext, ImageFileContext> loadContexts() throws IOException, ClassNotFoundException
	{
		File f = new File("contexts.obj");

		if (!f.exists())
		{
			return new HashMap<ImageFileContext, ImageFileContext>();
		}
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream("contexts.obj"));
		return (Map<ImageFileContext, ImageFileContext>) ois.readObject();
	}

	private static void saveContexts(Map<ImageFileContext, ImageFileContext> map) throws Exception
	{
		File f = new File("contexts.obj");

		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
		oos.writeObject(map);
		oos.close();
	}
}
