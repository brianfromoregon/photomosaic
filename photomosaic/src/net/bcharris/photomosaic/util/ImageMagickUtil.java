package net.bcharris.photomosaic.util;

import java.io.File;

/**
 *
 * @author brian
 */
public class ImageMagickUtil
{
	public static String generateCommandsToCreateMosaic(String montageCmd, File[][] imageGrid, int denom, String outputDirPath)
	{
		StringBuilder all = new StringBuilder();
		StringBuilder sections = new StringBuilder();
		
		int numWide = imageGrid.length;
		int numTall = imageGrid[0].length;
		
		all.append("mkdir " + outputDirPath + "\n");
		for (int j = 0; j < numTall / denom; j++)
		{
			for (int i = 0; i < numWide / denom; i++)
			{
				String sectionFile = outputDirPath + "/" + j + "_" + i + ".png";
				StringBuilder section = new StringBuilder();
				for (int h = j * denom; h < (j + 1) * denom; h++)
				{
					for (int w = i * denom; w < (i + 1) * denom; w++)
					{
						section.append(" \"").append(imageGrid[w][h].getAbsolutePath()).append("\"");
					}
				}
				all.append(montageCmd).append(section.toString()).append(" -geometry +0+0 ").append(sectionFile).append("\n");
				sections.append(" ").append(" \"").append(sectionFile).append("\"");
			}
		}

		all.append(montageCmd).append(sections.toString()).append(" -tile x").append(new Integer(numTall / denom)).append(" -geometry +0+0 ").append(outputDirPath).append("/").append("final.png").append("\n");
		return all.toString();
	}
}
