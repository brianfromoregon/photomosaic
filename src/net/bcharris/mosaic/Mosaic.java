package net.bcharris.mosaic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Mosaic
{
	private final Log log = LogFactory.getLog(Mosaic.class);

	private ImagePalette palette;

	public Mosaic(ImagePalette palette)
	{
		this.palette = palette;
	}

	public void doMosaic(File target, File dest, double scale, int numWide, int numTall, int maxSameImageUsage)
	{
		BufferedImage targetImage = null;

		try
		{
			targetImage = new ImageFileContext(target).getBufferedImage();
		}
		catch (IOException e)
		{
			log.error("while reading target image", e);
		}

		if (targetImage == null)
		{
			return;
		}

		BufferedImage mosaic = null;

		try
		{
			mosaic = palette.createMosaic(targetImage, scale, numWide, numTall, maxSameImageUsage);
		}
		catch (IOException e)
		{
			log.error("while reading target image", e);
		}

		try
		{
			log.info("Writing mosaic to " + dest.getAbsolutePath());
			ImageIO.write(mosaic, "png", dest);
			log.info("Done writing mosaic");
		}
		catch (IOException e)
		{
			log.error("while writing mosaic image", e);
		}

		// JFrame f = new JFrame();
		// JPanel cp = new JPanel(new GridBagLayout())
		// {
		// public void paint(Graphics g)
		// {
		// int w = getWidth();
		// int h = getHeight();
		// int imageW = mosaic.getWidth(this);
		// int imageH = mosaic.getHeight(this);
		// int x = (w - imageW)/2;
		// int y = (h - imageH)/2;
		// g.drawImage(mosaic, x, y, this);
		// super.paint(g);
		// }
		// };
		// cp.setOpaque(false);
		// GridBagConstraints gbc = new GridBagConstraints();
		// gbc.gridwidth = GridBagConstraints.REMAINDER;
		// gbc.weighty = 1.0;
		// f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// f.setContentPane(cp);
		// f.setSize(mosaic.getWidth(), mosaic.getHeight()+30);
		// f.setLocation(0, 0);
		// f.setVisible(true);
	}

	public static void main(String[] args) throws Exception
	{
		BeanFactory factory = new ClassPathXmlApplicationContext("context.xml");
		Mosaic mosaic = (Mosaic) factory.getBean("mosaic");
		File src = new File("C:\\Documents and Settings\\brian\\Desktop\\Addie");
		File dest = new File("C:\\Documents and Settings\\brian\\Desktop\\out.png");
		File target = new File("C:/Documents and Settings/brian/Desktop/IMG_3635.jpg");

		mosaic.palette.addImages(src);
		mosaic.doMosaic(target, dest, .2, 10, 10, 1);
	}
}
