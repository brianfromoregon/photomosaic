package net.bcharris.photomosaic;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;

public class SourceImageFinder extends DirectoryWalker {

    public SourceImageFinder() {
        super(imageFileFilter(), -1);
    }

    public Iterable<File> findSourceImages(File directory) {
        List<File> results = Lists.newArrayList();
        try {
            walk(directory, results);
        } catch (IOException ex) {
            throw new RuntimeException("Problem finding source images", ex);
        }
        return results;
    }

    @Override
    protected void handleFile(File file, int depth, Collection results) throws IOException {
        results.add(file);
    }

    private static FileFilter imageFileFilter() {
        return new SuffixFileFilter(new String[]{"png", "jpg", "jpeg", "gif", "bmp", "tif", "tiff"}, IOCase.INSENSITIVE);
    }
}
