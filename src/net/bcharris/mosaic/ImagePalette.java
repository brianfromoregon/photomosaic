package net.bcharris.mosaic;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.bcharris.mosaic.db.ImageDao;
import net.bcharris.mosaic.db.ImageDaoImpl;
import net.bcharris.mosaic.util.CompletableExecutor;
import net.bcharris.mosaic.util.SimpleCompletableExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;

public class ImagePalette
{
	// Drill down amount
	public final int ddx, ddy;

	// Stores palette image color info for quick nearest neighbor searching
	private final KDTree kdTree;

	// Thread-safe kd-tree size counter
	private final AtomicInteger kdTreeSize = new AtomicInteger(0);

	// DAO for storing image color information
	private final ImageDao dao;

	private final Log log = LogFactory.getLog(ImagePalette.class);

	// Size to resize images to before performing calculations on them.
	public int resizeWidth, resizeHeight;

	public ImagePalette(int resizeWidth, int resizeHeight, ImageDaoImpl dao)
	{
		this.ddx = dao.ddx;
		this.ddy = dao.ddy;
		this.resizeHeight = resizeHeight;
		this.resizeWidth = resizeWidth;
		this.dao = dao;
		kdTree = new KDTree(3 * ddx * ddy);
	}

	// Insert an image into this palette.
	// throws IOException if specified file context does not describe an image file.
	public boolean insert(ImageFileContext imageFileContext) throws IOException
	{
		ImageContext imageContext = getImageContext(imageFileContext);

		if (imageContext == null)
		{
			log.debug("tried to insert non-image into palette: " + imageFileContext.imageFile.getAbsolutePath());
			return false;
		}

		try
		{
			synchronized (kdTree)
			{
				kdTree.insert(imageContext.meanRgb, imageFileContext);
			}
		}
		catch (KeyDuplicateException e)
		{
		}
		catch (KeySizeException e)
		{
			log.error("Programmer error!", e);
		}

		log.debug("Added image to palette: " + imageFileContext.imageFile.getAbsolutePath());
		return true;
	}

	// Creates a photomosaic of the specified target image using the current palette.
	public BufferedImage createMosaic(BufferedImage target, double scale, int numWide, int numTall,
			int maxSameImageUsage) throws IOException
	{
		if (numWide * numTall > kdTreeSize.get() * maxSameImageUsage)
		{
			throw new IllegalArgumentException(
					"Not enough palette images to create mosaic given usage constraints; need at least " + 
					(int)Math.ceil((numWide * numTall) / maxSameImageUsage) + " and you only supplied " + kdTreeSize.get());
		}

		log.info("Creating mosaic");

		int sliceWidth = (int) ((target.getWidth() * scale) / numWide);
		int sliceHeight = (int) ((target.getHeight() * scale) / numTall);
		BufferedImage mosaic = new BufferedImage(sliceWidth * numWide, sliceHeight * numTall,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = mosaic.createGraphics();

		ImageFileContext[][] bestMatches = bestMatches(target, numWide, numTall, maxSameImageUsage);

		log.info("Drawing mosaic");
		for (int i = 0; i < bestMatches.length; i++)
		{
			for (int j = 0; j < bestMatches[i].length; j++)
			{
				// no point in having this drawing being multithreaded as it gets executed on the event dispatch thread
				// (right?)
				g.drawImage(bestMatches[i][j].getBufferedImage(sliceWidth, sliceHeight), (mosaic.getWidth() * i)
						/ numWide, (mosaic.getHeight() * j) / numTall, null);
			}
		}

		g.dispose();
		log.info("Done drawing mosaic");
		return mosaic;
	}

	// Get a grid of images which can be used to compose the specified target image as a mosaic.
	private ImageFileContext[][] bestMatches(final BufferedImage target, final int numWide, final int numTall,
			final int maxSameImageUsage)
	{
		log.info("Finding best image matches for target image sections");
		final ImageFileContext[][] bestMatches = new ImageFileContext[numWide][numTall];

		final CompletableExecutor executor = new SimpleCompletableExecutor(10);

		final int targetWidth = target.getWidth();
		final int targetHeight = target.getHeight();

		final Map<ImageFileContext, Integer> usages = new HashMap<ImageFileContext, Integer>();

		for (int i = 0; i < numWide; i++)
		{
			final int xStart = (targetWidth * i) / numWide;
			final int xEnd = (targetWidth * (i + 1)) / numWide;
			final int w = xEnd - xStart;

			for (int j = 0; j < numTall; j++)
			{
				final int yStart = (targetHeight * j) / numTall;
				final int yEnd = (targetHeight * (j + 1)) / numTall;
				final int h = yEnd - yStart;

				final int ii = i, jj = j;
				executor.execute(new Runnable() {
					public void run()
					{
						BufferedImage image = target.getSubimage(xStart, yStart, w, h);

						double[] sliceMeanColors = Util.meanColors(image, ddx, ddy);

						try
						{
							synchronized (kdTree)
							{
								synchronized (usages)
								{
									ImageFileContext best = (ImageFileContext) kdTree.nearest(sliceMeanColors);
									Integer uses = usages.get(best);

									if (uses == null)
									{
										uses = 0;
									}

									uses++;
									usages.put(best, uses);

									if (uses >= maxSameImageUsage)
									{
										try
										{
											kdTree.delete(getImageContext(best).meanRgb);
											kdTreeSize.decrementAndGet();
										}
										catch (Exception e)
										{
											log.error("Programmer error", e);
										}
									}

									bestMatches[ii][jj] = best;

									if (best == null)
									{
										System.out.println();
									}
								}
							}
						}
						catch (KeySizeException e)
						{
							log.error("Programmer error!", e);
							return;
						}
					}
				});
			}
		}

		executor.awaitCompletionAndShutdown();
		log.info("Done finding best image matches, " + usages.size() + " unique images used to fill " + numTall*numWide + " grid cells.");
		return bestMatches;
	}

	// Recursively add all images in the specified file or directory to this palette.
	public void addImages(File f)
	{
		CompletableExecutor executor = new SimpleCompletableExecutor(10);
		log.info("Adding images in this dir (recursively) to palette: " + f.getAbsolutePath());
		addImages(f, executor);
		executor.awaitCompletionAndShutdown();
		log.info("Done adding images to palette");
	}

	// Recursive, multi-threaded implementation of public method.
	private void addImages(final File f, final CompletableExecutor executor)
	{
		if (f.isDirectory())
		{
			File[] files = f.listFiles();
			for (final File f2 : files)
			{
				executor.execute(new Runnable() {
					public void run()
					{
						addImages(f2, executor);
					}
				});
			}
		}
		else
		{
			ImageFileContext imageFileContext = new ImageFileContext(f);
			try
			{
				insert(imageFileContext);
				kdTreeSize.incrementAndGet();
			}
			catch (IOException e)
			{
				log.info("error while trying to insert image file into palette", e);
			}
		}
	}

	// Utility function to get an ImageContext from an ImageFileContext
	private ImageContext getImageContext(ImageFileContext imageFileContext) throws IOException
	{
		ImageContext imageContext = dao.loadImageContext(imageFileContext.imageFile);
		if (imageContext == null)
		{
			BufferedImage bufferedImage = imageFileContext.getBufferedImage(resizeWidth, resizeHeight);
			if (bufferedImage == null)
			{
				return null;
			}

			imageContext = new ImageContext(Util.sha256(imageFileContext.imageFile), imageFileContext.imageFile
					.length(), ddx, ddy, Util.meanColors(bufferedImage, ddx, ddy));
			dao.saveImageContext(imageContext);
		}
		return imageContext;
	}
}
