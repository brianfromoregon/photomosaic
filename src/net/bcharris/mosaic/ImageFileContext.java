package net.bcharris.mosaic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;

import javax.imageio.ImageIO;

import net.bcharris.mosaic.util.ColorUtil;

public class ImageFileContext
{
	// Location on disk of the image this context describes
	public final File file;

	private SoftReference<BufferedImage> bufferedImage;

	private double[] meanRgb;

	private final int ddx, ddy;

	public ImageFileContext(File image, int ddx, int ddy)
	{
		this.file = image;
		this.bufferedImage = new SoftReference<BufferedImage>(null);
		this.ddx = ddx;
		this.ddy = ddy;
	}

	public BufferedImage getBufferedImage() throws IOException
	{
		BufferedImage bi = bufferedImage.get();
		if (bi == null)
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

	public double[] getMeanRgb()
	throws IOException
	{
		if (meanRgb == null)
		{
			BufferedImage bi = getBufferedImage();
			if (bi == null)
			{
				return null;
			}
			this.meanRgb = ColorUtil.meanColors(bi, ddx, ddy);
		}
		return this.meanRgb;
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
}
