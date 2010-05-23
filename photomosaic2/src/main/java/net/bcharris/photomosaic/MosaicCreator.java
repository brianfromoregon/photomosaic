package net.bcharris.photomosaic;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class MosaicCreator {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Exactly one argument is required, the path to the properties file.  Here is a sample properties file:");
            return;
        }
        UserInputDelegate userInputDelegate = new PropertiesFileUserInputDelegate(new File(args[0]));
        new MosaicCreator(userInputDelegate).createMosaic();
    }
    private final UserInputDelegate inputs;

    public MosaicCreator(UserInputDelegate inputs) {
        this.inputs = inputs;
    }

    public void createMosaic() {

        try {
            Index index = getExistingIndex();
            if (index == null) {
                index = createNewIndex();
            }

//            ImageMatcher imageMatcher = new ImageMatcher(index);
            TargetImageDescriptor targetImageDescriptor = getTargetImageDescriptor();
//            MosaicDescriptor mosaicDescriptor = imageMatcher.designMosaic(targetImageDescriptor, inputs.getTargetImageWidthInSourceImages());
//            mosaicDescriptor.create(inputs.getMosaicFile());
        } catch (UserInputException ex) {
            System.err.println(ex.getFriendlyDescription());
            return;
        }
    }

    private TargetImageDescriptor getTargetImageDescriptor() {
        return null;
    }

    private Index getExistingIndex() throws UserInputException {
        File existingIndexFile = inputs.getExistingIndexFile();
        try {
            if (existingIndexFile == null) {
                return null;
            }
            FileInputStream fileInputStream = new FileInputStream(existingIndexFile);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Object deserialized = objectInputStream.readObject();
            return (Index) deserialized;
        } catch (Exception ex) {
            throw new UserInputException(String.format("Could not read existing index file '%s' because: %s", existingIndexFile.toString(), ex.getMessage()));
        }
    }

    private Index createNewIndex() throws UserInputException {
        File emptyIndexDir = Files.createTempDir();
        ImageMagick imageMagick = new ImageMagick(inputs.getImageMagickDirectory());
        StringBuilder warningsLog = new StringBuilder();
        SourceImageFinder sourceImageFinder = new SourceImageFinder();
        Iterable<File> sourceImages = sourceImageFinder.findSourceImages(inputs.getSourceImageDirectory());
//        Index index = imageMagick.createIndex(sourceImages, emptyIndexDir, inputs.getSourceImageWidthInPixels(), inputs.getSourceImageHeightInPixels(), inputs.getDrillDown(), warningsLog);
        String warnings = warningsLog.toString().trim();
        if (warnings.length() > 0) {
            System.out.println("Warnings during indexing:");
            System.out.println(warnings);
        }
        System.out.println("Done creating index, directory: " + emptyIndexDir.getAbsolutePath());
//        return index;
        return null;
    }
}
