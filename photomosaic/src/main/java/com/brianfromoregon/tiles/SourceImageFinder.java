package com.brianfromoregon.tiles;

import com.google.common.collect.Lists;
import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

class SourceImageFinder extends DirectoryWalker {

    public SourceImageFinder(Set<File> excludes) {
        // visible directories and image files
        super(new AndFileFilter(Lists.newArrayList(notHiddenFileFilter(),
                new OrFileFilter(imageFileFilter(), DirectoryFileFilter.INSTANCE),
                notExcludedFilter(excludes))), -1);
    }

    public Set<File> findSourceImages(Iterable<File> directories) {
        // linkedhashset to preserve order otherwise disk seeks will go nuts when indexing
        Set<File> results = new LinkedHashSet<>();
        for (File directory : directories) {
            try {
                walk(directory, results);
            } catch (IOException ex) {
                throw new RuntimeException("Problem finding source images", ex);
            }
        }
        return results;
    }

    @Override
    protected void handleFile(File file, int depth, Collection results) throws IOException {
        results.add(file);
    }

    private static IOFileFilter notHiddenFileFilter() {
        // HiddenFileFilter.HIDDEN doesn't exclude files starting with '.' on Windows but I want to.
        return new NotFileFilter(new OrFileFilter(HiddenFileFilter.HIDDEN, new PrefixFileFilter(".")));
    }

    private static IOFileFilter imageFileFilter() {
        return new SuffixFileFilter(new String[]{"png", "jpg", "jpeg", "gif", "bmp", "tif", "tiff"}, IOCase.INSENSITIVE);
    }

    private static IOFileFilter notExcludedFilter(final Set<File> excludes) {
        return new AbstractFileFilter() {
            @Override public boolean accept(File file) {
                if (excludes.contains(file))
                    return false;
                for (File exclude : excludes) {
                    try {
                        if (exclude.isDirectory() && FileUtils.directoryContains(exclude, file))
                            return false;
                    } catch (IOException e) {
                        Log.log("Problem testing exclude: " + e.getMessage());
                    }
                }
                return true;
            }
        };
    }
}
