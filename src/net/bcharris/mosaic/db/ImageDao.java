package net.bcharris.mosaic.db;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.bcharris.mosaic.ImageContext;
import net.bcharris.mosaic.Util;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class ImageDao
{
	private final JdbcTemplate jdbc;

	public final int ddx, ddy;

	// map FileKeys to their respective images
	private final Map<FileKey, ImageContext> imageFileMap = new HashMap<FileKey, ImageContext>();

	// map a filesize to an ImageContext if it is unique, or null if not
	private final Map<Long, ImageContext> uniqueFileLengths = new HashMap<Long, ImageContext>();

	private final Log log = LogFactory.getLog(ImageDao.class);

	public ImageDao(int ddx, int ddy, String dbName, String derbySystemHome, JdbcTemplate jdbc) throws IOException
	{
		this.jdbc = jdbc;
		this.ddx = ddx;
		this.ddy = ddy;
		System.setProperty("derby.system.home", derbySystemHome);
		File dbDir = new File(derbySystemHome + System.getProperty("file.separator") + dbName);
		if (!dbDir.isDirectory())
		{
			log.info("Database not found, creating new one at: " + dbDir.getAbsolutePath());
			createDb(Util.read(new File("sql/createDb.sql"), "\\s*;\\s*"));
			log.info("Done creating database");
		}
		loadAllContexts();
	}

	public ImageContext loadImageContext(File imageFile)
	{
		long len = imageFile.length();

		if (!uniqueFileLengths.containsKey(len))
		{
			return null;
		}

		ImageContext val = uniqueFileLengths.get(len);

		if (val != null)
		{
			return val;
		}

		try
		{
			String Sha256 = Util.sha256(imageFile);
			return imageFileMap.get(new FileKey(Sha256, len));
		}
		catch (IOException e)
		{
			log.warn("could not get sha256 for file", e);
			return null;
		}
	}

	public void saveImageContext(ImageContext context)
	{
		Integer id;
		FileKey fileKey = new FileKey(context.sha256, context.imageFileLength);

		try
		{
			id = jdbc.queryForInt("Select id From Image Where SHA256 = ? And fileSize = ? ", new Object[] {
					context.sha256, context.imageFileLength });
		}
		catch (EmptyResultDataAccessException e)
		{
			id = null;
		}

		if (id != null && imageFileMap.containsKey(fileKey))
		{
			// delete existing db ddx,ddy rows
			jdbc.update("Delete From ImageSection " + "Where imageId = ? And ddx = ? And ddy = ?", new Object[] { id,
					context.ddx, context.ddy });
		}
		else
		{
			if (id == null)
			{
				// insert into Image
				jdbc.update("Insert Into Image (SHA256, fileSize) Values (?, ?)", new Object[] { context.sha256,
						context.imageFileLength });

				id = jdbc.queryForInt("Select id " + "From Image " + "Where SHA256 = ? ",
						new Object[] { context.sha256 });
			}

			// update maps
			imageFileMap.put(fileKey, context);
			
			if (uniqueFileLengths.containsKey(context.imageFileLength))
			{
				if (uniqueFileLengths.get(context.imageFileLength) != null && !uniqueFileLengths.get(context.imageFileLength).equals(context))
				{
					uniqueFileLengths.put(context.imageFileLength, null);
				}
			}
			else
			{
				uniqueFileLengths.put(context.imageFileLength, context);
			}
		}

		// insert values into ImageSection
		for (int i = 0, section = 0; i < context.meanRgb.length; i += 3, section++)
		{
			jdbc.update("Insert Into ImageSection (imageId, ddx, ddy, section, meanR, meanG, meanB) Values "
					+ "(?,?,?,?,?,?,?) ", new Object[] { id, context.ddx, context.ddy, section, context.meanRgb[i],
					context.meanRgb[i + 1], context.meanRgb[i + 2], });
		}
	}

	private void createDb(String[] createStatements)
	{
		jdbc.batchUpdate(createStatements);
	}

	private void loadAllContexts()
	{
		log.info("Loading saved image info from database");
		log.info("Issuing SQL query");
		SqlRowSet rows = jdbc.queryForRowSet("Select SHA256, fileSize, section, meanR, meanG, meanB " + "From Image "
				+ "	Join ImageSection On id = imageId And ddx = ? And ddy = ?" + "Order By SHA256, fileSize, section",
				new Object[] { ddx, ddy });

		int numRows = 0;
		log.info("Processing result set");
		while (!rows.isAfterLast() && rows.next())
		{
			numRows++;
			
			// construct an ImageContext from the selected rows
			String sha256 = rows.getString("SHA256");
			long fileSize = rows.getLong("fileSize");

			double[] meanRgb = new double[3 * ddx * ddy];
			short section = rows.getShort("section");
			int lastSection;
			do
			{
				meanRgb[section * 3 + 0] = rows.getDouble("meanR");
				meanRgb[section * 3 + 1] = rows.getDouble("meanG");
				meanRgb[section * 3 + 2] = rows.getDouble("meanB");
				lastSection = section;
			}
			while (rows.next() && lastSection < (section = rows.getShort("section")));

			ImageContext imageContext = new ImageContext(sha256, fileSize, ddx, ddy, meanRgb);

			// add the loaded image context to the lookup maps
			FileKey fileKey = new FileKey(imageContext.sha256, imageContext.imageFileLength);
			imageFileMap.put(fileKey, imageContext);
			if (uniqueFileLengths.containsKey(imageContext.imageFileLength))
			{
				if (!uniqueFileLengths.get(imageContext.imageFileLength).equals(imageContext))
				{
					uniqueFileLengths.put(imageContext.imageFileLength, null);
				}
			}
			else
			{
				uniqueFileLengths.put(imageContext.imageFileLength, imageContext);
			}
		}
		log.info("Done processing all " + numRows + " rows");
	}
}

class FileKey
{
	public final String Sha256;

	public final long fileSize;

	public FileKey(String Sha256, long fileSize)
	{
		this.Sha256 = Sha256;
		this.fileSize = fileSize;
	}

	@Override
	public int hashCode()
	{
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj)
	{
		return EqualsBuilder.reflectionEquals(this, obj);
	}
}
