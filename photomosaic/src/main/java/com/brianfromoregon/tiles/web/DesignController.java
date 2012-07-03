package com.brianfromoregon.tiles.web;

import com.brianfromoregon.tiles.*;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.ws.rs.*;
import java.awt.image.BufferedImage;
import java.util.IdentityHashMap;

@Path("design")
public class DesignController {

    @GET
    public DesignView.Response start() {
        DesignView.Response design = new DesignView.Response();
        design.setNumWide(18);
        ColorSpace cs = ColorSpace.CIELAB;
        design.setColorSpace(cs.name());
        design.setDrillDown(1);
        design.setAllowReuse(true);
        design.setPositions(calcPositions(design.getNumWide(), design.isAllowReuse(), cs, design.getDrillDown()));
        return design;
    }

    @POST
    public DesignView.Response create(@MultipartForm DesignView.Request request) {
        DesignView.Response response = request.asResponse();

        if (request.getNumWide() <= 1) {
            response.getErrors().put("numWide", "Must be greater than 1");
        }

        if (request.getDrillDown() < 1) {
            response.getErrors().put("drillDown", "Must be greater than 0");
        }

        BufferedImage newTarget = Util.bytesToBufferedImage(request.getTarget());
        if (newTarget != null) {
            SessionState.target = newTarget;
        }

        int maxWide = Util.maxNumWide(SessionState.palette, SessionState.target.getWidth(), SessionState.target.getHeight());
        if (!request.isAllowReuse() && request.getNumWide() > maxWide) {
            // Auto correct, I suspect it's better to not notify.
            request.setNumWide(maxWide);
            response.setNumWide(maxWide);
        }

        if (response.getErrors().isEmpty()) {
            response.setPositions(calcPositions(request.getNumWide(), request.isAllowReuse(), ColorSpace.fromString(request.getColorSpace()), request.getDrillDown()));
        }

        return response;
    }

    private int[] calcPositions(int numWide, boolean allowReuse, ColorSpace colorSpace, int drillDown) {

        Index index = SessionState.palette;
        final ProcessedIndex processedIndex = ProcessedIndex.process(index, drillDown);
        MatchingIndex matchingIndex;
        if (drillDown == 1)
            matchingIndex = FastFuzzyMatchingIndex.create(processedIndex, colorSpace);
        else
            matchingIndex = OptimalMatchingIndex.create(processedIndex, colorSpace, drillDown);
        Creator creator = new Creator();
        Mosaic mosaic = creator.designMosaic(matchingIndex, SessionState.target, allowReuse, numWide);

        IdentityHashMap<Index.Image, Integer> srcPos = index.indexedImages();
        int[] tgtPos = new int[mosaic.numTall() * mosaic.numWide()];
        for (int row = 0; row < mosaic.numTall(); row++) {
            for (int col = 0; col < mosaic.numWide(); col++) {
                tgtPos[mosaic.numWide() * row + col] = srcPos.get(mosaic.layout[row][col]);
            }
        }
        return tgtPos;
    }
}
