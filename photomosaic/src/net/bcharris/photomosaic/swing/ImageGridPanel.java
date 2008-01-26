package net.bcharris.photomosaic.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
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

	private Integer gridX,  gridY;

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if (currentImage == null)
		{
			return;
		}
		
		int componentWidth = getWidth();
		int componentHeight = getHeight();

		double componentRatio = componentWidth / (double) componentHeight;

		int x1, y1, w, h;
		if (componentRatio > imageRatio)
		{
			y1 = 0;
			h = componentHeight;
			w = (int) (imageRatio * h);
			x1 = (componentWidth - w) / 2;
		}
		else
		{
			x1 = 0;
			w = componentWidth;
			h = (int) ((1 / imageRatio) * w);
			y1 = (componentHeight - h) / 2;
		}

		g.drawImage(currentImage, x1, y1, w, h, this);
		
		// Draw grid
		if (gridX != null && gridY != null)
		{
			double cellWidth = w / (double)gridX;
			double cellHeight = h / (double)gridY;
			
			g.setColor(Color.MAGENTA);
			for (int i = 1; i < gridX; i++)
			{
				int xPos = x1 + (int)(i * cellWidth);
				g.drawLine(xPos, y1, xPos, y1+h);
			}
			
			for (int i = 1; i < gridY; i++)
			{
				int yPos = y1 + (int)(i * cellHeight);
				g.drawLine(x1, yPos, x1+w, yPos);	
			}
		}
	}

	public void setGridSize(int x, int y)
	{
		this.gridX = x;
		this.gridY = y;
	}

	public void setImage(BufferedImage image)
	{
		this.currentImage = image;
		this.imageRatio = image.getWidth() / (double) image.getHeight();
	}

	public BufferedImage getImage()
	{
		return currentImage;
	}

	public Double getImageRatio()
	{
		return imageRatio;
	}
}
