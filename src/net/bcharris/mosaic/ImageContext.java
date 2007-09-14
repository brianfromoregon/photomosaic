package net.bcharris.mosaic;

public class ImageContext
{
	public final int ddx, ddy;

	public final String sha256;

	public final Long imageFileLength;

	public final double[] meanRgb;

	public ImageContext(String sha256, long imageFileLength, int ddx, int ddy, double[] meanRgb)
	{
		if (sha256 == null || meanRgb == null)
		{
			throw new IllegalArgumentException("null argument");
		}
		if (sha256.length() != 64)
		{
			throw new IllegalArgumentException("sha256 is not 64 chars long");
		}
		if (meanRgb.length != 3 * ddx * ddy)
		{
			throw new IllegalArgumentException("meanRgb array length is not 3*ddx*ddy");
		}
		if (imageFileLength < 1 || ddx < 1 || ddy < 1)
		{
			throw new IllegalArgumentException("int argument < 1");
		}

		this.sha256 = sha256;
		this.imageFileLength = imageFileLength;
		this.ddx = ddx;
		this.ddy = ddy;
		this.meanRgb = meanRgb;
	}
}
