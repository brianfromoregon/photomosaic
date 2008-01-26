package net.bcharris.photomosaic.swing;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author brian
 */
public class ImageGridPanel extends JPanel
{
	private BufferedImage currentImage;

	private Double imageRatio;

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if (currentImage != null)
		{
			int componentWidth = getWidth();
			int componentHeight = getHeight();
			
			double componentRatio = componentWidth / (double)componentHeight;
			
			int x1,y1,w,h;
			if (componentRatio > imageRatio)
			{
				y1 = 0;
				h = componentHeight;
				w = (int)(imageRatio * h);
				x1 = (componentWidth - w) / 2;
			}
			else
			{
				x1 = 0;
				w = componentWidth;
				h = (int)((1/imageRatio) * w);
				y1 = (componentHeight - h) / 2;
			}
			
			g.drawImage(currentImage, x1, y1, w, h, this);
		}
	}

	public void setImage(BufferedImage image)
	{
		this.currentImage = image;
		this.imageRatio = image.getWidth() / (double)image.getHeight();
	}
	
	public Double getImageRatio()
	{
		return imageRatio;
	}
}
