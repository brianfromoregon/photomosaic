package com.brianfromoregon.tiles.web;

import com.brianfromoregon.tiles.Index;
import com.brianfromoregon.tiles.Tuple;
import com.brianfromoregon.tiles.persist.DataStore;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.googlecode.htmleasy.ViewWith;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import static com.brianfromoregon.tiles.Tuple.tuple;

@ViewWith("palette.ftl")
@Component
public class PaletteView {
    @FormParam("roots") @Getter @Setter private String roots;
    @FormParam("excludes") @Getter @Setter private String excludes;
    @Getter private Map<String, String> errors = Maps.newHashMap();
    @Getter @Setter private int width, height;

    @Inject DataStore dataStore;

    public List<String> getRootsList() {
        return Lists.newArrayList(Splitter.onPattern("\r?\n").trimResults().omitEmptyStrings().split(getRoots()));
    }

    public void setRootsList(List<String> roots) {
        setRoots(Joiner.on('\n').join(roots));
    }

    public void setExcludesList(List<String> excludes) {
        setExcludes(Joiner.on('\n').join(excludes));
    }

    public List<String> getExcludesList() {
        return Lists.newArrayList(Splitter.onPattern("\r?\n").trimResults().omitEmptyStrings().split(getExcludes()));
    }

    public List<Tuple> images() {

        final IdentityHashMap<Index.Image, Integer> indexes = dataStore.loadPalette().getPalette().indexedImages();
        return Lists.transform(new Ordering<Index.Image>() {
            public int compare(Index.Image a, Index.Image b) {
                return a.file.compareTo(b.file);
            }
        }.sortedCopy(dataStore.loadPalette().getPalette().images), new Function<Index.Image, Tuple>() {
            @Override public Tuple apply(Index.Image input) {
                return tuple(indexes.get(input), input.file);
            }
        });
    }
}
