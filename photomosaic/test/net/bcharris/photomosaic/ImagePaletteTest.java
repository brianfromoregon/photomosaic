
package net.bcharris.photomosaic;

import net.bcharris.photomosaic.builder.ImageFileContext;
import net.bcharris.photomosaic.builder.ImagePalette;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import net.bcharris.photomosaic.util.ImageMagickUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author brian
 */
public class ImagePaletteTest {

    public ImagePaletteTest() {
    }

	@BeforeClass
	public static void setUpClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownClass() throws Exception
	{
	}

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
	
	@Test
	public void createMosaic()
			throws Exception
	{
		ImagePalette imagePalette = new ImagePalette(5, 8);
		imagePalette.addImages(new File("images/colors_16x11"));

		// How many images tall the mosaic will be.
		int numImagesTall = 12;

		// How many images wide the mosaic will be.
		int numImagesWide = 18;
		
		// The image we're trying to re-create.
		BufferedImage targetImage = ImageIO.read(new File("images/gradients/Gradient3.jpg"));

		// A grid of images that, when compacted into 1 large image w.r.t. their
		// ordering in the grid, will compose the desired mosaic.
		File[][] imageGrid = toFiles(imagePalette.bestMatches(targetImage, numImagesWide, numImagesTall, 1));
		
		System.out.println(ImageMagickUtil.generateCommandsToCreateMosaic("montage", imageGrid, 4, "output"));
	}
	
	private static File[][] toFiles(ImageFileContext[][] contexts)
	{
		File[][] files = new File[contexts.length][];
		
		for (int i = 0; i < contexts.length; i++)
		{
			files[i] = new File[contexts[i].length];
			for (int j = 0; j < contexts[i].length; j++)
			{
				files[i][j] = contexts[i][j].file;
			}
		}
		
		return files;
	}

//	/**
//	 * Test of addImages method, of class ImagePalette.
//	 */
//	@Test
//	public void addImages()
//	{
//		System.out.println("addImages");
//		File f = null;
//		ImagePalette instance = null;
//		instance.addImages(f);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of bestMatches method, of class ImagePalette.
//	 */
//	@Test
//	public void bestMatches()
//	{
//		System.out.println("bestMatches");
//		BufferedImage target = null;
//		int numWide = 0;
//		int numTall = 0;
//		int maxSameImageUsage = 0;
//		ImagePalette instance = null;
//		ImageFileContext[][] expResult = null;
//		ImageFileContext[][] result = instance.bestMatches(target, numWide, numTall, maxSameImageUsage);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of createMosaic method, of class ImagePalette.
//	 */
//	@Test
//	public void createMosaic() throws Exception
//	{
//		System.out.println("createMosaic");
//		BufferedImage target = null;
//		int numWide = 0;
//		int numTall = 0;
//		int sliceWidth = 0;
//		int sliceHeight = 0;
//		int maxSameImageUsage = 0;
//		ImagePalette instance = null;
//		BufferedImage expResult = null;
//		BufferedImage result = instance.createMosaic(target, numWide, numTall, sliceWidth, sliceHeight, maxSameImageUsage);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of insert method, of class ImagePalette.
//	 */
//	@Test
//	public void insert() throws Exception
//	{
//		System.out.println("insert");
//		ImageFileContext ctx = null;
//		ImagePalette instance = null;
//		boolean expResult = false;
//		boolean result = instance.insert(ctx);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}

}