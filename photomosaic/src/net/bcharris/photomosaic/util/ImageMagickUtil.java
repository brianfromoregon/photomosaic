package net.bcharris.photomosaic.util;

import java.io.File;

/**
 *
 * @author brian
 */
public class ImageMagickUtil
{
	public static String generateScriptToCreateMosaic(String montageCmd, File[][] imageGrid, int xDenom, int yDenom, String outputDirPath)
	{
		StringBuilder all = new StringBuilder();
		StringBuilder sections = new StringBuilder();

		int numWide = imageGrid.length;
		int numTall = imageGrid[0].length;

		for (int j = 0; j < numTall / yDenom; j++)
		{
			for (int i = 0; i < numWide / xDenom; i++)
			{
				String sectionFile = outputDirPath.replace("\\","/") + "/" + j + "_" + i + ".png";
				StringBuilder section = new StringBuilder();
				for (int h = j * yDenom; h < (j + 1) * yDenom; h++)
				{
					for (int w = i * xDenom; w < (i + 1) * xDenom; w++)
					{
						section.append(" \"").append(imageGrid[w][h].getAbsolutePath().replace("\\","/")).append("\"");
					}
				}
				all.append(montageCmd).append(section.toString()).append(" -geometry +0+0 \"").append(sectionFile.replace("\\","/")).append("\"\n");
				sections.append(" ").append(" \"").append(sectionFile).append("\"");
			}
		}

		if (numTall != yDenom || numWide != xDenom)
		{
			all.append(montageCmd).append(sections.toString()).append(" -tile x").append(new Integer(numTall / yDenom)).append(" -geometry +0+0 \"").append(outputDirPath.replace("\\","/")).append("/").append("final.png").append("\"\n");
		}
		return all.toString();
	}

	public static String generateScriptToPrepSourceImages(File srcDir, String outDir, int imgWidth, int imgHeight)
	{
		return "export srcDir=\"" + srcDir.getAbsolutePath().replace("\\","/") + "\"\n" +
				"export outDir=\"" + outDir.replace("\\","/") + "\"\n" +
				"count=0\n" +
				"find \"$srcDir\" -type f | egrep \"\\.(jpg|jpeg|gif|bmp|png|tif|tiff)$\" | while read F\n" +
				"do\n" +
				"	echo $F\n" +
				"	\n" +
				"	# -auto-orient rotates the images to adjust for people turning the camera sideways\n" +
				"	# the multiple resize commands accomplish optimistic (for landscape) cropping.\n" +
				"	convert \"$F\" -auto-orient -resize x"+imgHeight*2+" -resize \""+imgWidth*2+"x<\" -resize 50% -gravity center -crop "+imgWidth+"x"+imgHeight+"+0+0 +repage \"$outDir/$count.png\";\n" +
				"	\n" +
				"	let count=count+1;\n" +
				"done";
	}
}
