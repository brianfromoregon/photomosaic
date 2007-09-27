package net.bcharris.mosaic.util;

import java.awt.image.BufferedImage;

public class ColorUtil
{
	private static double[] meanColor(final BufferedImage image)
	{
		if (image == null)
			throw new IllegalArgumentException("image is null");

		int w = image.getWidth();
		int h = image.getHeight();
		int[] rgbArray = new int[w * h];
		image.getRGB(0, 0, w, h, rgbArray, 0, w);
		double sumR = 0, sumG = 0, sumB = 0;
		for (int i = 0; i < rgbArray.length; i++)
		{
			sumR += rgbArray[i] >> 16 & 0xff;
			sumG += rgbArray[i] >> 8 & 0xff;
			sumB += rgbArray[i] & 0xff;
		}
		return new double[] { sumR / rgbArray.length, sumG / rgbArray.length, sumB / rgbArray.length };
	}

	public static double[] meanColors(final BufferedImage image, int ddx, int ddy)
	{
		double[] sliceMeanColors = new double[ddx * ddy * 3];
		int width = image.getWidth();
		int height = image.getHeight();

		for (int i = 0; i < ddx; i++)
		{
			int xStart = (width * i) / ddx;
			int xEnd = (width * (i + 1)) / ddx;
			int w = xEnd - xStart;

			for (int j = 0; j < ddy; j++)
			{
				int yStart = (height * j) / ddy;
				int yEnd = (height * (j + 1)) / ddy;
				int h = yEnd - yStart;
				double[] sliceMeanRGB = meanColor(image.getSubimage(xStart, yStart, w, h));
				int index = 3 * (j * ddx + i);
				for (int channel = 0; channel < 3; channel++)
				{
					sliceMeanColors[index + channel] = sliceMeanRGB[channel];
				}
			}
		}
		return sliceMeanColors;
	}
}
