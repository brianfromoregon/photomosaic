
package net.bcharris.photomosaic.util;

import java.io.File;
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
public class ImageMagickUtilTest {

    public ImageMagickUtilTest() {
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

	/**
	 * Test of generateScriptToCreateMosaic method, of class ImageMagickUtil.
	 */
	@Test
	public void generateCommandToCreateMosaic()
	{
		String montageCmd = "montage";
		File[][] imageGrid = sample0to99Images();
		int numTall = 10;
		int numWide = 10;
		String result = ImageMagickUtil.generateScriptToCreateMosaic(montageCmd, imageGrid, 2, 5, "output");
		System.out.println(result);
	}
	
	private File[][] sample0to99Images()
	{
		File[][] imageGrid = new File[10][10];
		for (int i = 0; i < 100; i++)
		{
			imageGrid[i/10][i%10] = new File("images/0to99_16x11/" + String.valueOf(i) + ".jpg");
		}
		return imageGrid;
	}
}