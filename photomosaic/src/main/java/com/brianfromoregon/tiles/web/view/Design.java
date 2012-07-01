package com.brianfromoregon.tiles.web.view;

import com.brianfromoregon.tiles.ColorSpace;
import com.brianfromoregon.tiles.web.SessionState;
import com.google.common.collect.Maps;
import com.googlecode.htmleasy.ViewWith;
import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.FormParam;
import java.util.Map;

public class Design {

    @FormParam("numWide") @Getter @Setter private int numWide;
    @FormParam("allowReuse") @Getter @Setter private boolean allowReuse;
    @FormParam("colorSpace") @Getter @Setter private String colorSpace;
    @FormParam("drillDown") @Getter @Setter private int drillDown;

    public static class Request extends Design {
        @FormParam("target") @Getter @Setter private byte[] target;
    }

    @ViewWith("design.ftl")
    public static class Response extends Design {
        @Getter private Map<String, String> errors = Maps.newHashMap();
        @Getter @Setter private int[] positions;

        public boolean success() {
            return positions != null;
        }

        public int numTall() {
            return positions.length / getNumWide();
        }

        public int width() {
            return SessionState.palette.width;
        }

        public int height() {
            return SessionState.palette.height;
        }

        public boolean isSRGB() {
            return ColorSpace.SRGB.name().toLowerCase().equals(getColorSpace().toLowerCase());
        }

        public boolean isCIELAB() {
            return ColorSpace.CIELAB.name().toLowerCase().equals(getColorSpace().toLowerCase());
        }
    }

    public Response asResponse() {
        Design.Response response = new Response();
        response.setNumWide(this.numWide);
        return response;
    }
}
