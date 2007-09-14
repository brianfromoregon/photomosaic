package net.bcharris.mosaic;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

public class Util
{

	public static double[] meanColor(final BufferedImage image)
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
				double[] sliceMeanRGB = Util.meanColor(image.getSubimage(xStart, yStart, w, h));
				int index = 3 * (j * ddx + i);
				for (int channel = 0; channel < 3; channel++)
				{
					sliceMeanColors[index + channel] = sliceMeanRGB[channel];
				}
			}
		}
		return sliceMeanColors;
	}

	public static String[] read(File f, String delim) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		char[] buf = new char[1024];
		BufferedReader br = new BufferedReader(new FileReader(f));
		int read;
		while (-1 != (read = br.read(buf)))
		{
			sb.append(buf, 0, read);
		}
		return sb.toString().split(delim);
	}

	public static String sha256(File f) throws FileNotFoundException, IOException
	{
		try
		{
			MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
			DigestInputStream dis = new DigestInputStream(new BufferedInputStream(new FileInputStream(f)), sha256Digest);

			byte[] buf = new byte[1024];
			while (-1 != dis.read(buf))
			{
			}

			return new String(Hex.encodeHex(dis.getMessageDigest().digest()));
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new RuntimeException(e);
		}
	}
}
