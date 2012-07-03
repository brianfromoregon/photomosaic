package com.brianfromoregon.tiles.web;

import com.brianfromoregon.tiles.Index;
import com.brianfromoregon.tiles.Tuple;
import static com.brianfromoregon.tiles.Tuple.*;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.googlecode.htmleasy.View;
import org.jboss.resteasy.annotations.Form;

import javax.ws.rs.*;
import java.util.IdentityHashMap;
import java.util.List;

@Path("palette")
public class PaletteController {

    @GET
    @Path("{idx}")
    @Produces("image/jpeg")
    public byte[] img(@PathParam("idx") int idx) {
        return SessionState.palette.images.get(idx).jpeg;
    }

    @GET
    public PaletteView.Response all() {
        PaletteView.Response response = new PaletteView.Response();
        response.setExcludes("/Users/Brian/Pics/honeymoon\n/Users/Brian/test");
        response.setRoots("/Users/Brian/Pics");
        return response;
    }

    @POST
    public PaletteView.Response refresh(@Form PaletteView request) {
        PaletteView.Response response = request.asResponse();
        return response;
    }
}