package com.brianfromoregon.tiles.web.control;

import com.brianfromoregon.tiles.Index;
import com.brianfromoregon.tiles.web.SessionState;
import com.brianfromoregon.tiles.web.view.Design;
import com.google.common.collect.ImmutableMap;
import com.googlecode.htmleasy.View;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("palette")
public class Palette {
    @GET
    @Path("{idx}")
    @Produces("image/jpeg")
    public byte[] img(@PathParam("idx") int idx) {
        return SessionState.palette.images.get(idx).jpeg;
    }

    @GET
    public View all() {
        return new View("palette.ftl", ImmutableMap.of("count", SessionState.palette.images.size(), "width",
                SessionState.palette.width, "height", SessionState.palette.height));
    }
}