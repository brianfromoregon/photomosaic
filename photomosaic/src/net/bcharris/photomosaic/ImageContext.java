package net.bcharris.photomosaic;

public class ImageContext
{
	public final int ddx, ddy;
	
	public final double[] meanRgb;

	public ImageContext(int ddx, int ddy, double[] meanRgb)
	{
		if (meanRgb == null)
		{
			throw new IllegalArgumentException("null mean RGB array");
		}
		if (meanRgb.length != 3 * ddx * ddy)
		{
			throw new IllegalArgumentException("meanRgb array length is not 3*ddx*ddy");
		}
		
		this.ddx = ddx;
		this.ddy = ddy;
		this.meanRgb = meanRgb;
	}
}
