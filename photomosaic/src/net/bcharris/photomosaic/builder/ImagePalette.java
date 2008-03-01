package net.bcharris.photomosaic.builder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import edu.wlu.cs.levy.CG.KDTree;
import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.bcharris.photomosaic.util.ColorUtil;
import net.bcharris.photomosaic.util.CompletableExecutor;
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
				executor.execute(new Runnable()
				{
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
			final int maxSameImageUsage, List<Point> priorities)
	{
		log.info("Finding best image matches for target image sections");
		final ImageFileContext[][] bestMatches = new ImageFileContext[numWide][numTall];

		final CompletableExecutor executor = new SimpleCompletableExecutor(numThreads);

		final Map<ImageFileContext, Integer> usages = new HashMap<ImageFileContext, Integer>();
		
		if (priorities == null)
		{
			priorities = new LinkedList<Point>();
		}

		for (Point p : priorities)
		{
			// Run these in same thread, in serial.
			bestMatch(bestMatches, usages, target, p.x, p.y, numWide, numTall, null, maxSameImageUsage);
		}

		// Do all non-prioritized cells in random order.
		List<Point> shuffled = new ArrayList<Point>(numWide * numTall);
                for (int i = 0; i < numWide; i++) {
                    for (int j = 0; j < numTall; j++) {
                        Point p = new Point(i, j);
                        if (!priorities.contains(p)) {
                            shuffled.add(p);
                        }
                    }
                }
                Collections.shuffle(shuffled);

                for (Point p : shuffled) {
                    bestMatch(bestMatches, usages, target, p.x, p.y, numWide, numTall, executor, maxSameImageUsage);
                }

		executor.awaitCompletionAndShutdown();
		log.info("Done finding best image matches, " + usages.size() + " unique images used to fill " + numTall * numWide + " grid cells.");

		return bestMatches;
	}

	private void bestMatch(final ImageFileContext[][] bestMatches, final Map<ImageFileContext, Integer> usages, final BufferedImage target, final int x, final int y, int numWide, int numTall, CompletableExecutor executor, final int maxSameImageUsage)
	{
		final int xStart = (target.getWidth() * x) / numWide;
		final int xEnd = (target.getWidth() * (x + 1)) / numWide;
		final int w = xEnd - xStart;
		final int yStart = (target.getHeight() * y) / numTall;
		final int yEnd = (target.getHeight() * (y + 1)) / numTall;
		final int h = yEnd - yStart;

		Runnable runnable = new Runnable()
		{
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

							bestMatches[x][y] = best;
							log.debug("Found best match for cell (" + x + "," + y + ")");
						}
					}
				}
				catch (KeySizeException e)
				{
					log.error("Programmer error!", e);
					return;
				}
			}
		};
		
		if (executor == null)
		{
			runnable.run();
		}
		else
		{
			executor.execute(runnable);
		}
	}

//	// Creates a photomosaic of the specified target image using the current palette.
//	public BufferedImage createMosaic(BufferedImage target, int numWide, int numTall, int sliceWidth, int sliceHeight,
//			int maxSameImageUsage) throws IOException
//	{
//		if (numWide * numTall > kdTreeSize.get() * maxSameImageUsage)
//		{
//			throw new IllegalArgumentException(
//					"Not enough palette images to create mosaic given usage constraints; need at least "
//							+ (int) Math.ceil((numWide * numTall) / maxSameImageUsage) + " and you only supplied "
//							+ kdTreeSize.get());
//		}
//
//		log.info("Creating mosaic");
//
//		ImageFileContext[][] bestMatches = bestMatches(target, numWide, numTall, maxSameImageUsage);
//
//		BufferedImage mosaic = new BufferedImage(sliceWidth * numWide, sliceHeight * numTall,
//				BufferedImage.TYPE_INT_ARGB);
//		Graphics2D g = mosaic.createGraphics();
//
//		log.info("Drawing mosaic");
//		for (int i = 0; i < bestMatches.length; i++)
//		{
//			for (int j = 0; j < bestMatches[i].length; j++)
//			{
//				// no point in having this drawing being multithreaded as it gets executed on the event dispatch thread
//				// (right?)
//				g.drawImage(bestMatches[i][j].getBufferedImage(), (mosaic.getWidth() * i) / numWide, (mosaic
//						.getHeight() * j)
//						/ numTall, null);
//				log.debug("Drew cell (" + i + "," + j + ")");
//			}
//		}
//
//		g.dispose();
//		log.info("Done drawing mosaic");
//		return mosaic;
//	}
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

	public int size()
	{
		return kdTreeSize.get();
	}
}
