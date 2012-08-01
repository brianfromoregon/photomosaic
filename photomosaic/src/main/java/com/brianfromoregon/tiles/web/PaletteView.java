package com.brianfromoregon.tiles.web;

import com.brianfromoregon.tiles.Index;
import com.brianfromoregon.tiles.Tuple;
import com.brianfromoregon.tiles.persist.PaletteDescriptor;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.googlecode.htmleasy.ViewWith;
import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.FormParam;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ViewWith("palette.ftl")
public class PaletteView {
    @FormParam("roots") @Getter @Setter private String roots;
    @FormParam("excludes") @Getter @Setter private String excludes;
    @FormParam("width") @Getter @Setter private int width;
    @FormParam("height") @Getter @Setter private int height;
    @Getter private Map<String, String> errors = Maps.newHashMap();
    @Getter @Setter private boolean shortTermMemory;
    @Getter private List<Tuple> images;

    public Set<String> getRootsList() {
        return Sets.newHashSet(Splitter.onPattern("\r?\n").trimResults().omitEmptyStrings().split(getRoots()));
    }

    public Set<String> getExcludesList() {
        return Sets.newHashSet(Splitter.onPattern("\r?\n").trimResults().omitEmptyStrings().split(getExcludes()));
    }

    public void setRootsList(Set<String> roots) {
        setRoots(Joiner.on('\n').join(roots));
    }

    public void setExcludesList(Set<String> excludes) {
        setExcludes(Joiner.on('\n').join(excludes));
    }

    public void setPaletteProperties(PaletteDescriptor palette) {
        final IdentityHashMap<Index.Image, Integer> indexes = palette.getPalette().indexedImages();
        this.images = Lists.transform(new Ordering<Index.Image>() {
            public int compare(Index.Image a, Index.Image b) {
                return a.file.compareTo(b.file);
            }
        }.sortedCopy(palette.getPalette().images), new Function<Index.Image, Tuple>() {
            @Override public Tuple apply(Index.Image input) {
                return new Tuple(indexes.get(input), input.file);
            }
        });
        this.width = palette.getPalette().width;
        this.height = palette.getPalette().height;
    }
}
