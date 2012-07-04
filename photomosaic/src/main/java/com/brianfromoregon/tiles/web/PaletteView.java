package com.brianfromoregon.tiles.web;

import com.brianfromoregon.tiles.Index;
import com.brianfromoregon.tiles.Tuple;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.googlecode.htmleasy.ViewWith;
import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.FormParam;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import static com.brianfromoregon.tiles.Tuple.tuple;

public class PaletteView {
    @FormParam("roots") @Getter @Setter private String roots;
    @FormParam("excludes") @Getter @Setter private String excludes;

    public Iterable<String> rootsList() {
        return Splitter.onPattern("\r?\n").trimResults().omitEmptyStrings().split(getRoots());
    }

    public Iterable<String> excludesList() {
        return Splitter.onPattern("\r?\n").trimResults().omitEmptyStrings().split(getExcludes());
    }

    @ViewWith("palette.ftl")
    public static class Response extends PaletteView {
        @Getter private Map<String, String> errors = Maps.newHashMap();

        public List<Tuple> images() {

            final IdentityHashMap<Index.Image, Integer> indexes = SessionState.palette.indexedImages();
            return Lists.transform(new Ordering<Index.Image>() {
                public int compare(Index.Image a, Index.Image b) {
                    return a.uri.compareTo(b.uri);
                }
            }.sortedCopy(SessionState.palette.images), new Function<Index.Image, Tuple>() {
                @Override public Tuple apply(Index.Image input) {
                    return tuple(indexes.get(input), input.uri);
                }
            });
        }

        public int width() {
            return SessionState.palette.width;
        }

        public int height() {
            return SessionState.palette.height;
        }

        public int numRoots() {
            return Iterables.size(rootsList());
        }

        public int numExcludes() {
            return Iterables.size(excludesList());
        }
    }

    public Response asResponse() {
        Response response = new Response();
        response.setRoots(this.roots);
        response.setExcludes(this.excludes);
        return response;
    }
}
