package net.bcharris.photomosaic;

import java.io.File;

/**
 * These methods must all return sanitized & correct values or else throw a UserInputException.
 */
public interface UserInputDelegate {

    File getTargetImage() throws UserInputException;

    int getTargetImageWidthInSourceImages() throws UserInputException;

    int getDrillDown() throws UserInputException;

    int getSourceImageHeightInPixels() throws UserInputException;

    int getSourceImageWidthInPixels() throws UserInputException;

    File getSourceImageDirectory() throws UserInputException;

    File getImageMagickDirectory() throws UserInputException;

    File getMosaicFile() throws UserInputException;

    File getExistingIndexFile() throws UserInputException;
}
