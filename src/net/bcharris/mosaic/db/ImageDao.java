package net.bcharris.mosaic.db;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.bcharris.mosaic.ImageContext;
import net.bcharris.mosaic.Util;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class ImageDao
{
	private final JdbcTemplate jdbcTemplate;

	private final TransactionTemplate transactionTemplate;

	public final int ddx, ddy;

	// map FileKeys to their respective images
	private final Map<FileKey, ImageContext> imageFileMap = new HashMap<FileKey, ImageContext>();

	// map a filesize to an ImageContext if it is unique, or null if not
	private final Map<Long, ImageContext> uniqueFileLengths = new HashMap<Long, ImageContext>();

	private final Log log = LogFactory.getLog(ImageDao.class);

	public ImageDao(int ddx, int ddy, String dbName, String derbySystemHome, JdbcTemplate jdbcTemplate,
			TransactionTemplate transactionTemplate) throws IOException
	{
		this.transactionTemplate = transactionTemplate;
		this.jdbcTemplate = jdbcTemplate;
		this.ddx = ddx;
		this.ddy = ddy;
		System.setProperty("derby.system.home", derbySystemHome);
		String dbLoc = derbySystemHome + System.getProperty("file.separator") + dbName;
		System.setProperty("dbLoc", dbLoc);
		init();
	}

	private void init() throws IOException
	{
		File dbDir = new File(System.getProperty("dbLoc"));
		if (!dbDir.isDirectory())
		{
			log.info("Database not found, creating new one at: " + dbDir.getAbsolutePath());
			createDb(Util.read(new File("sql/createDb.sql"), "\\s*;\\s*"));
			log.info("Done creating database");
		}
		loadAllContexts();
	}

	/**
	 * Try to find an image context for a file. Currently the implementation does not go to the database because it
	 * loads all contexts on startup.
	 * 
	 * @param imageFile The file to find a context for.
	 * @return An image context describing the file, or null if none exists.
	 */
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
			String sha256 = Util.sha256(imageFile);
			return imageFileMap.get(new FileKey(sha256, len));
		}
		catch (IOException e)
		{
			log.warn("could not get sha256 for file", e);
			return null;
		}
		catch (IllegalArgumentException e)
		{
			log.error("Programmer mistake", e);
			return null;
		}
	}

	public synchronized void saveImageContext(final ImageContext imageContext)
	{
		Integer id;
		FileKey fileKey = null;
		try
		{
			fileKey = new FileKey(imageContext.sha256, imageContext.imageFileLength);
		}
		catch (IllegalArgumentException e)
		{
			log.error("Programmer mistake", e);
			return;
		}

		try
		{
			id = jdbcTemplate.queryForInt("Select id From Image Where SHA256 = ? And fileSize = ? ", new Object[] {
					imageContext.sha256, imageContext.imageFileLength });
		}
		catch (EmptyResultDataAccessException e)
		{
			id = null;
		}

		// if this image already exists in the database
		if (id != null)
		{
			// delete existing db ddx,ddy rows
			jdbcTemplate.update("Delete From ImageSection " + "Where imageId = ? And ddx = ? And ddy = ?",
					new Object[] { id, imageContext.ddx, imageContext.ddy });
		}
		else
		{
			// insert into Image
			jdbcTemplate.update("Insert Into Image (SHA256, fileSize) Values (?, ?)", new Object[] {
					imageContext.sha256, imageContext.imageFileLength });

			id = jdbcTemplate.queryForInt("Select id " + "From Image " + "Where SHA256 = ? ",
					new Object[] { imageContext.sha256 });

			// sanity check
			if (imageFileMap.containsKey(fileKey))
			{
				log.warn("Programmer error, imageFileMap contained image file key, but database didn't:"
						+ fileKey.toString());
			}

			// update maps
			imageFileMap.put(fileKey, imageContext);
			updateUniqueFileLengths(imageContext);
		}

		final int finalId = id;

		// insert values into ImageSection
		transactionTemplate.execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus status)
			{
				return jdbcTemplate.batchUpdate(
						"Insert Into ImageSection (imageId, ddx, ddy, section, meanR, meanG, meanB) Values "
								+ "(?,?,?,?,?,?,?) ", new BatchPreparedStatementSetter() {
							public int getBatchSize()
							{
								return imageContext.meanRgb.length / 3;
							}

							public void setValues(PreparedStatement ps, int i) throws SQLException
							{
								ps.setInt(1, finalId);
								ps.setInt(2, imageContext.ddx);
								ps.setInt(3, imageContext.ddy);
								ps.setInt(4, i);
								ps.setDouble(5, imageContext.meanRgb[i * 3 + 0]);
								ps.setDouble(6, imageContext.meanRgb[i * 3 + 1]);
								ps.setDouble(7, imageContext.meanRgb[i * 3 + 2]);
							}
						});
			}
		});
	}

	private synchronized void createDb(String[] createStatements)
	{
		jdbcTemplate.batchUpdate(createStatements);
	}

	private synchronized void loadAllContexts()
	{
		log.info("Loading saved image info from database");
		log.info("Issuing SQL query");
		SqlRowSet rows = jdbcTemplate.queryForRowSet("Select SHA256, fileSize, section, meanR, meanG, meanB "
				+ "From Image " + "	Join ImageSection On id = imageId And ddx = ? And ddy = ?"
				+ "Order By SHA256, fileSize, section", new Object[] { ddx, ddy });

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
			FileKey fileKey = null;
			try
			{
				fileKey = new FileKey(imageContext.sha256, imageContext.imageFileLength);
			}
			catch (IllegalArgumentException e)
			{
				log.error("Programmer mistake", e);
				continue;
			}
			imageFileMap.put(fileKey, imageContext);
			updateUniqueFileLengths(imageContext);
		}
		log.info("Done processing all " + numRows + " rows");
	}

	private void updateUniqueFileLengths(ImageContext imageContext)
	{
		if (uniqueFileLengths.containsKey(imageContext.imageFileLength))
		{
			if (uniqueFileLengths.get(imageContext.imageFileLength) != null
					&& !uniqueFileLengths.get(imageContext.imageFileLength).equals(imageContext))
			{
				uniqueFileLengths.put(imageContext.imageFileLength, null);
			}
		}
		else
		{
			uniqueFileLengths.put(imageContext.imageFileLength, imageContext);
		}
	}
}

/**
 * Assuming a file can be uniquely identified by a FileKey, a SHA256 and its file size. This assumption is theoretically
 * false, but it's good enough for me.
 */
class FileKey
{
	public final String Sha256;

	public final long fileSize;

	public FileKey(String Sha256, long fileSize)
	{
		if (Sha256 == null)
		{
			throw new IllegalArgumentException("null SHA256");
		}
		if (fileSize < 1)
		{
			throw new IllegalArgumentException("illegal file size");
		}
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

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}
