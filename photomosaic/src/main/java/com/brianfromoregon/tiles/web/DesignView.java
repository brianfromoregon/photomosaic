package com.brianfromoregon.tiles.web;

import com.brianfromoregon.tiles.ColorSpace;
import com.brianfromoregon.tiles.persist.Repository;
import com.google.common.collect.Maps;
import com.googlecode.htmleasy.ViewWith;
import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.FormParam;
import java.util.*;

public class DesignView {

    @FormParam("numWide") @Getter @Setter private int numWide;
    @FormParam("allowReuse") @Getter @Setter private boolean allowReuse;
    @FormParam("colorSpace") @Getter @Setter private String colorSpace;
    @FormParam("drillDown") @Getter @Setter private int drillDown;

    public static class Request extends DesignView {
        @FormParam("target") @Getter @Setter private byte[] target;
    }

    @ViewWith("design.ftl")
    public static class Response extends DesignView {
        @Getter @Setter private int[] positions;

        public boolean success() {
            return positions != null;
        }

        public int numTall() {
            return positions.length / getNumWide();
        }

        public int width() {
            return Repository.INSTANCE.get().palette.width;
        }

        public int height() {
            return Repository.INSTANCE.get().palette.height;
        }

        public boolean isSRGB() {
            return ColorSpace.SRGB.name().toLowerCase().equals(getColorSpace().toLowerCase());
        }

        public boolean isCIELAB() {
            return ColorSpace.CIELAB.name().toLowerCase().equals(getColorSpace().toLowerCase());
        }

        public int distinctTiles() {
            Set<Integer> set = new HashSet<>();
            for (int i : positions) set.add(i);
            return set.size();
        }

        public int paletteSize() {
            return Repository.INSTANCE.get().palette.images.size();
        }
    }

    public Response asResponse() {
        DesignView.Response response = new Response();
        response.setNumWide(this.numWide);
        response.setAllowReuse(this.allowReuse);
        response.setColorSpace(this.colorSpace);
        response.setDrillDown(this.drillDown);
        return response;
    }
}
