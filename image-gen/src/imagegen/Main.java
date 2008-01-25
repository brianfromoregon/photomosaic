package imagegen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 *
 * @author brian
 */
public class Main
{
	public static void main(String[] args)
			throws Exception
	{
		genColors();
	}

	private static void genColors()
			throws Exception
	{
		File outputDir = new File("colors_16x11");
		outputDir.mkdir();

		for (int r = 0; r < 256; r += 50)
		{
			for (int g = 0; g < 256; g += 50)
			{
				for (int b = 0; b < 256; b += 50)
				{
					BufferedImage image = new BufferedImage(16, 11, BufferedImage.TYPE_INT_RGB);

					Graphics2D g2d = image.createGraphics();
					g2d.setColor(new Color(r, g, b));
					g2d.fillRect(0, 0, 16, 11);

					ImageIO.write(image, "JPG", new File(outputDir, String.valueOf(r) + "_" + String.valueOf(g) + "_" + String.valueOf(b) + ".jpg"));
				}
			}
		}
	}

	private static void genNumbers()
			throws Exception
	{
		File outputDir = new File("0to99_16x11");
		outputDir.mkdir();

		for (int i = 0; i < 100; i++)
		{
			BufferedImage image = new BufferedImage(16, 11, BufferedImage.TYPE_INT_RGB);

			Graphics2D g2d = image.createGraphics();
			g2d.drawString(String.valueOf(i), 1, 10);

			ImageIO.write(image, "JPG", new File(outputDir, String.valueOf(i) + ".jpg"));
		}
	}
}
