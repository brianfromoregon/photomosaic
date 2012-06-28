package com.brianfromoregon.tiles.indexes;

import com.brianfromoregon.tiles.Index;
import com.google.common.collect.ImmutableMap;
import com.googlecode.htmleasy.View;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("palette")
 public class Palette
{
    public static Index index;

    @GET
    @Path("{idx}")
    @Produces("image/jpeg")
    public byte[] img(@PathParam("idx") int idx)
    {
        return index.images.get(idx).jpeg;
    }

    @GET
    @Path("")
    public View all()
    {
        return new View("palette.ftl", ImmutableMap.of("count", index.images.size(), "width", index.width, "height", index.height));
    }
}