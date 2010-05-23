package net.bcharris.photomosaic;

import com.google.common.io.Closeables;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class PropertiesFileUserInputDelegate implements UserInputDelegate {

    private final Properties properties;

    public PropertiesFileUserInputDelegate(File propertiesFile) {
        this.properties = new Properties();
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(propertiesFile);
            properties.load(fileInputStream);
        } catch (Exception ex) {
            throw new RuntimeException(String.format("While reading properties from file '%s'\n", propertiesFile.toString()), ex);
        }
        Closeables.closeQuietly(fileInputStream);
    }

    @Override
    public File getTargetImage() throws UserInputException {
        return existingFile(getProperty("targetImage", true), true);
    }

    @Override
    public int getTargetImageWidthInSourceImages() throws UserInputException {
        return parseInt(getProperty("targetImageWidthInSourceImage", true));
    }

    @Override
    public int getDrillDown() throws UserInputException {
        return parseInt(getProperty("drillDown", true));
    }

    @Override
    public int getSourceImageHeightInPixels() throws UserInputException {
        return parseInt(getProperty("sourceImageHeight", true));
    }

    @Override
    public int getSourceImageWidthInPixels() throws UserInputException {
        return parseInt(getProperty("sourceImageWidth", true));
    }

    @Override
    public File getSourceImageDirectory() throws UserInputException {
        return existingFile(getProperty("sourceImageDirectory", true), true);
    }

    @Override
    public File getImageMagickDirectory() throws UserInputException {
        return existingFile(getProperty("imageMagickDirectory", true), true);
    }

    @Override
    public File getMosaicFile() throws UserInputException {
        return existingFile("mosaicFile", false);
    }

    @Override
    public File getExistingIndexFile() throws UserInputException {
        return existingFile("targetImage", true);
    }

    private File existingFile(String filePath, boolean mustExist) throws UserInputException {
        File f = new File(filePath);
        if (mustExist && !f.exists()) {
            throw new UserInputException(String.format("The file '%s' does not exist", filePath));
        }
        return f;
    }

    private int parseInt(String value) throws UserInputException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new UserInputException(String.format("Non-integer specified: '%s'", value));
        }
    }

    private String getProperty(String propertyName, boolean required) throws UserInputException {
        String value = properties.getProperty(propertyName);
        if (required && value == null) {
            throw new UserInputException(String.format("No value specified for property '%s'", propertyName));
        }
        return value;
    }
}
