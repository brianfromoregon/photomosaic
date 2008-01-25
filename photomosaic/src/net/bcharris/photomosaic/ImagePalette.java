package net.bcharris.photomosaic;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;
import net.bcharris.mosaic.util.CompletableExecutor;
import net.bcharris.photomosaic.util.ColorUtil;
import net.bcharris.photomosaic.util.SimpleCompletableExecutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ImagePalette
{
	// Drill down amount
	public final int dd;

	// Stores palette image color info for quick nearest neighbor searching
	private final KDTree kdTree;

	// For serializing
	public Map<ImageFileContext, ImageFileContext> contexts = new HashMap<ImageFileContext, ImageFileContext>();

	// Thread-safe kd-tree size counter
	private final AtomicInteger kdTreeSize = new AtomicInteger(0);

	private final int numThreads;

	private final Log log = LogFactory.getLog(ImagePalette.class);

	public ImagePalette(int dd, int numThreads)
	{
		this.dd = dd;
		this.numThreads = numThreads;
		kdTree = new KDTree(3 * dd * dd);
	}

	// Recursively add all images in the specified file or directory to this palette.
	public void addImages(File f)
	{
		CompletableExecutor executor = new SimpleCompletableExecutor(numThreads);
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
			try
			{
				ImageFileContext context = new ImageFileContext(f);
				if (insert(context))
				{
					kdTreeSize.incrementAndGet();
				}
			}
			catch (IOException e)
			{
				log.info("error while trying to insert image file into palette", e);
			}
		}
	}

	// Get a grid of images which can be used to compose the specified target image as a mosaic.
	public ImageFileContext[][] bestMatches(final BufferedImage target, final int numWide, final int numTall,
			final int maxSameImageUsage)
	{
		log.info("Finding best image matches for target image sections");
		final ImageFileContext[][] bestMatches = new ImageFileContext[numWide][numTall];

		final CompletableExecutor executor = new SimpleCompletableExecutor(numThreads);

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

						double[] sliceMeanColors = ColorUtil.meanColors(image, dd, dd);

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
											kdTree.delete(best.getMeanRgb(dd));
											kdTreeSize.decrementAndGet();
										}
										catch (Exception e)
										{
											log.error("Programmer error", e);
										}
									}

									bestMatches[ii][jj] = best;
									log.debug("Found best match for cell (" + ii + "," + jj + ")");
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
		log.info("Done finding best image matches, " + usages.size() + " unique images used to fill " + numTall
				* numWide + " grid cells.");

		return bestMatches;
	}

	// Creates a photomosaic of the specified target image using the current palette.
	public BufferedImage createMosaic(BufferedImage target, int numWide, int numTall, int sliceWidth, int sliceHeight,
			int maxSameImageUsage) throws IOException
	{
		if (numWide * numTall > kdTreeSize.get() * maxSameImageUsage)
		{
			throw new IllegalArgumentException(
					"Not enough palette images to create mosaic given usage constraints; need at least "
							+ (int) Math.ceil((numWide * numTall) / maxSameImageUsage) + " and you only supplied "
							+ kdTreeSize.get());
		}

		log.info("Creating mosaic");

		ImageFileContext[][] bestMatches = bestMatches(target, numWide, numTall, maxSameImageUsage);

		BufferedImage mosaic = new BufferedImage(sliceWidth * numWide, sliceHeight * numTall,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = mosaic.createGraphics();

		log.info("Drawing mosaic");
		for (int i = 0; i < bestMatches.length; i++)
		{
			for (int j = 0; j < bestMatches[i].length; j++)
			{
				// no point in having this drawing being multithreaded as it gets executed on the event dispatch thread
				// (right?)
				g.drawImage(bestMatches[i][j].getBufferedImage(), (mosaic.getWidth() * i) / numWide, (mosaic
						.getHeight() * j)
						/ numTall, null);
				log.debug("Drew cell (" + i + "," + j + ")");
			}
		}

		g.dispose();
		log.info("Done drawing mosaic");
		return mosaic;
	}

	public boolean insert(ImageFileContext ctx) throws IOException
	{
		try
		{
			synchronized (kdTree)
			{
				if (contexts.containsKey(ctx))
				{
					ctx = contexts.get(ctx);
				}
				else
				{
					contexts.put(ctx, ctx);
				}
				double[] meanRgb = ctx.getMeanRgb(dd);
				if (meanRgb == null)
				{
					return false;
				}
				kdTree.insert(meanRgb, ctx);
				log.debug("Added image to palette: " + ctx.file.getAbsolutePath());
				return true;
			}
		}
		catch (KeyDuplicateException e)
		{
			log.debug("Duplicate key in kdtree, ignoring: " + ctx.file.getAbsolutePath());
			return false;
		}
		catch (KeySizeException e)
		{
			log.error("Programmer error!", e);
			return false;
		}
	}
}
