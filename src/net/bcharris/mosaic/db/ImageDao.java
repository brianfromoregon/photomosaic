package net.bcharris.mosaic.db;

import java.io.File;

import net.bcharris.mosaic.ImageContext;

public interface ImageDao
{

	/**
	 * Try to find an image context for a file. Currently the implementation does not go to the database because it
	 * loads all contexts on startup.
	 * 
	 * @param imageFile The file to find a context for.
	 * @return An image context describing the file, or null if none exists.
	 */
	ImageContext loadImageContext(File imageFile);

	/**
	 * Persist an ImageContext.
	 * @param imageContext The image context to persist.
	 */
	void saveImageContext(final ImageContext imageContext);

}