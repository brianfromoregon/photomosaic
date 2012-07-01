package com.brianfromoregon.tiles.web.view;

import com.brianfromoregon.tiles.*;
import com.brianfromoregon.tiles.indexes.Palette;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.imageio.ImageIO;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.util.IdentityHashMap;

@Path("design")
public class DesignActions {
    BufferedImage target;

    @GET
    public Design.Response start() {
        Design.Response design = new Design.Response();
        design.numWide = 5;
        design.errors.put("dummy", "");
        return design;
    }

    // This worked when target was a byte[]
//    @GET
//    @Path("target")
//    public Response getTarget() {
//        if (target == null)
//            return Response.noContent().build();
//        else {
//            return Response.ok(target).build();
//        }
//    }

    @POST
    public Design.Response create(@MultipartForm Design.Request request) {
        Design.Response response = request.asResponse();

        if (request.getNumWide() <= 0) {
            response.errors.put("numWide", "Must be greater than 0");
        }

        if (target == null && (target = Util.bytesToBufferedImage(request.getTarget())) == null) {
            response.errors.put("target", "Need to choose an image file");
        } else {
            Index index = Palette.index;
            final ProcessedIndex processedIndex = ProcessedIndex.process(index, Creator.DEFAULT_DRILL_DOWN);
            MatchingIndex optimalMatchingIndex = OptimalMatchingIndex.create(processedIndex, ColorSpace.CIELAB, 6);
            Creator creator = new Creator();
            Mosaic mosaic = creator.designMosaic(optimalMatchingIndex, target, true, request.getNumWide());
            // eew
            IdentityHashMap<Index.Image, Integer> srcPos = Maps.newIdentityHashMap();
            for (int i=0; i<index.images.size(); i++)
                srcPos.put(index.images.get(i), i);

            int[] tgtPos = new int[mosaic.numTall()*mosaic.numWide()];
            for (int row=0; row<mosaic.numTall(); row++) {
                for (int col=0; col<mosaic.numWide(); col++) {
                    tgtPos[mosaic.numWide() * row + col] = srcPos.get(mosaic.layout[row][col]);
                }
            }
            response.positions = tgtPos;
        }

        return response;
    }

}
