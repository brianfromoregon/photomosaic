package com.brianfromoregon.tiles;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.brianfromoregon.tiles.Index.Image;

public class IndexFilter {

    // This should be replaced with a UI: http://code.google.com/p/tiles/issues/detail?id=26
    public static void main(String[] args) throws IOException {
        File indexFile = new File("E:\\mosaic\\20100526-bcharris-200-150.index");
        Index index = Util.readIndex(indexFile);
        ArrayList<Image> keptImages = Lists.newArrayList();
        List<String> badDirs = Lists.newArrayList("Joel and Britney's Wedding", "Originals", "Joel & Britney Honeymoon", "Picasa", "Picasa Exports", ".picasaoriginals", "house scans", "insurance health reports");
        for (Image image : index.images) {
            boolean keep = true;
            for (String badDir : badDirs) {
                if (image.url.toLowerCase().contains("\\" + badDir.toLowerCase() + "\\")) {
                    keep = false;
                    break;
                }
            }
            if (keep) {
            System.out.println(image.url);
                keptImages.add(image);
            }
        }
        Util.writeIndex(new Index(keptImages, index.width, index.height), File.createTempFile("new", "index"));
    }
}
