package com.brianfromoregon.tiles.web.view;

import com.google.common.collect.Maps;
import com.googlecode.htmleasy.ViewWith;
import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Design {

    @FormParam("numWide") @Getter @Setter int numWide;

    public static class Request extends Design {
        @FormParam("target") @Getter @Setter byte[] target;
    }

    @ViewWith("design.ftl")
    public static class Response extends Design {
        @Getter Map<String, String> errors = Maps.newHashMap();
        @Getter int[] positions;

        public boolean success() {
            return positions != null;
        }

        public int numTall() {
            return positions.length / numWide;
        }
    }

    public Response asResponse() {
        Design.Response response = new Response();
        response.numWide = this.numWide;
        return response;
    }
}
