package net.bcharris.photomosaic;

import net.bcharris.photomosaic.util.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.ref.SoftReference;

import javax.imageio.ImageIO;

public class ImageFileContext implements Serializable
{
	// Location on disk of the image this context describes
	public final File file;

	private transient SoftReference<BufferedImage> bufferedImage;

	private double[][] meanRgb = new double[6][];

	public ImageFileContext(File image)
	{
		this.file = image;
		this.bufferedImage = new SoftReference<BufferedImage>(null);
	}

	public BufferedImage getBufferedImage() throws IOException
	{
		BufferedImage bi = null;
		if (bufferedImage == null || bufferedImage.get() == null)
		{
			bi = ImageIO.read(file);
			if (bi == null)
			{
				return null;
			}
			bufferedImage = new SoftReference<BufferedImage>(bi);
		}
		return bi;
	}

	public double[] getMeanRgb(int dd) throws IOException
	{	
		if (meanRgb[dd-1] == null)
		{
			BufferedImage bi = getBufferedImage();
			if (bi == null)
			{
				return null;
			}

			meanRgb[dd-1] = ColorUtil.meanColors(bi, dd, dd);
		}
		
		return meanRgb[dd-1];
	}

	@Override
	public int hashCode()
	{
		return 37 * (this.file == null ? 37 : this.file.hashCode());
	}

	@Override
	public boolean equals(Object that)
	{
		if (this == that)
		{
			return true;
		}

		if (that == null || !(that instanceof ImageFileContext))
		{
			return false;
		}

		return this.file.equals(((ImageFileContext) that).file);
	}

	private Object writeReplace() throws ObjectStreamException
	{
		try
		{
			this.getMeanRgb(1);
			this.getMeanRgb(2);
			this.getMeanRgb(3);
			this.getMeanRgb(4);
			this.getMeanRgb(5);
			this.getMeanRgb(6);
		}
		catch (IOException e)
		{
			e.printStackTrace(System.err);
		}
		
		return this;
	}
}
