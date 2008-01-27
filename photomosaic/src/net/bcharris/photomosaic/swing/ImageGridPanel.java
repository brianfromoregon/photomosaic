package net.bcharris.photomosaic.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
	
	private List<Point> priorities = Collections.synchronizedList(new ArrayList<Point>());
	
	public ImageGridPanel()
	{
	}
	
	public ImageGridPanel(ImageGridPanel clone)
	{
		this.currentImage = clone.currentImage;
		this.imageRatio = clone.imageRatio;
		this.gridX = clone.gridX;
		this.gridY = clone.gridY;
		this.priorities = clone.priorities;
	}

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
		
		int x1,y1,w,h;
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
		if (gridX == null || gridY == null)
		{
			return;
		}
		
		double cellWidth = w / (double) gridX;
		double cellHeight = h / (double) gridY;

		g.setColor(Color.ORANGE);
		for (int i = 1; i < gridX; i++)
		{
			int xPos = x1 + (int) (i * cellWidth);
			g.drawLine(xPos, y1, xPos, y1 + h);
		}

		for (int i = 1; i < gridY; i++)
		{
			int yPos = y1 + (int) (i * cellHeight);
			g.drawLine(x1, yPos, x1 + w, yPos);
		}

		// Draw priorities
		synchronized (priorities)
		{
			if (!priorities.isEmpty())
			{
				int fontSize = 80;
				Font font = null;
				while (fontSize > 1)
				{
					font = new Font("Lucida Console", Font.BOLD, fontSize);
					FontMetrics metrics = g.getFontMetrics(font);
					int fontHeight = metrics.getHeight();
					int textWidth = metrics.stringWidth(String.valueOf(priorities.size()-1));
					
					if (fontHeight+2 < cellHeight && textWidth+2 < cellWidth)
					{
						break;
					}
					fontSize = Math.min(fontSize-2, (int)(fontSize * .8));
				}
				if (fontSize <= 0)
				{
					font = new Font("Lucida Console", Font.BOLD, 1);
				}
				g.setFont(font);
				
				int priority = 1;
				Iterator<Point> i = priorities.iterator();
				while (i.hasNext())
				{
					Point cell = i.next();
					int xPos = x1 + (int) Math.round(cell.x * cellWidth) + 1;
					int yPos = y1 + (int) Math.round(cell.y * cellHeight) + (int) Math.round(cellHeight) - 1;
					g.drawString(String.valueOf(priority++), xPos, yPos);
				}
			}
		}
	}

	public Point getCellForPoint(Point p)
	{
		if (currentImage == null)
		{
			return null;
		}
		int componentWidth = getWidth();
		int componentHeight = getHeight();

		double componentRatio = componentWidth / (double) componentHeight;

		int x1,y1,w,h;
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
		
		if (p.x < x1 || p.x > x1+w || p.y < y1 || p.y > y1+h)
		{
			return null;
		}
		
		int x = gridX * (p.x - x1) / w;
		int y = gridY * (p.y - y1) / h;
		return new Point(x,y);
	}

	public void setGridSize(int x, int y)
	{
		if ((gridX != null && x != gridX) || (gridY != null && y != gridY))
		{
			priorities.clear();
		}
		
		this.gridX = x;
		this.gridY = y;
	}

	public void setImage(BufferedImage image)
	{
		this.currentImage = image;
		this.imageRatio = image.getWidth() / (double) image.getHeight();
		priorities.clear();
	}

	public BufferedImage getImage()
	{
		return currentImage;
	}

	public Double getImageRatio()
	{
		return imageRatio;
	}
	
	public void addPriority(int pos, Point p)
	{
		if (pos < 0)
		{
			return;
		}
		
		if (validPriority(p))
		{
			priorities.add(pos, p);
		}
	}
	
	public void addPriority(Point p)
	{
		if (validPriority(p))
		{
			priorities.add(p);
		}
	}
	
	public void removePriority(Point p)
	{
		priorities.remove(p);
	}
	
	public void clearPriorities()
	{
		priorities.clear();
	}
	
	public List<Point> getPriorities()
	{
		return new ArrayList<Point>(priorities);
	}
	
	public void setPriorities(List<Point> priorities)
	{
		this.priorities = Collections.synchronizedList(new ArrayList<Point>(priorities));
		synchronized (priorities)
		{
			Iterator<Point> i = priorities.iterator();
			while (i.hasNext())
			{
				Point p = i.next();
				if (!validPriority(p))
				{
					i.remove();
				}
			}
		}
	}
	
	private boolean validPriority(Point p)
	{
		if (gridX == null || gridY == null)
		{
			return false;
		}
		
		return p.x < gridX && p.y < gridY;
	}
}
