package net.bcharris.mosaic;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;

import javax.imageio.ImageIO;

public class ImageFileContext
{
	// Location on disk of the image this context describes
	public final File imageFile;

	// Cache of previousily requested image
	private SoftReference<BufferedImage> bufferedImage;

	public ImageFileContext(File image)
	{
		this.imageFile = image;
		this.bufferedImage = new SoftReference<BufferedImage>(null);
	}

	// Get a BufferedImage without resizing first, null if not an image
	public BufferedImage getBufferedImage()
	throws IOException
	{
		BufferedImage bi = bufferedImage.get();
		if (bi == null)
		{
			bi = ImageIO.read(imageFile);
			if (bi == null)
			{
				return null;
			}
			bufferedImage = new SoftReference<BufferedImage>(bi);
		}
		
		return bi;
	}

	// Get a BufferedImage of the specified size, resizing if necessary
	public BufferedImage getBufferedImage(int resizeWidth, int resizeHeight)
	throws IOException
	{
		BufferedImage bi = getBufferedImage();
		if (bi == null)
		{
			return null;
		}
		
		if (bi.getWidth() != resizeWidth || bi.getHeight() != resizeHeight)
		{
			BufferedImage resized = new BufferedImage(resizeWidth, resizeHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = resized.createGraphics();
			g.drawImage(bi, 0, 0, resizeWidth, resizeHeight, null);
			g.dispose();
			bi = resized;
			bufferedImage = new SoftReference<BufferedImage>(bi);
		}

		return bi;
	}

	@Override
	public int hashCode()
	{
		return 37 * (this.imageFile == null ? 37 : this.imageFile.hashCode());
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
		
		return this.imageFile.equals(((ImageFileContext)that).imageFile);
	}
}
