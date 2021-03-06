package com.brianfromoregon.tiles.web;

import com.brianfromoregon.tiles.*;
import com.brianfromoregon.tiles.persist.DataStore;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.IdentityHashMap;

@Path("design")
@Component
public class DesignController {
    @Inject DataStore dataStore;

    @GET
    public DesignView start() {
        DesignView view = new DesignView();
        view.setNumWide(18);
        ColorSpace cs = ColorSpace.CIELAB;
        view.setColorSpace(cs.name());
        view.setDrillDown(4);
        view.setAllowReuse(true);
        view.setPositions((int[]) calcPositions(view.getNumWide(), view.isAllowReuse(), cs, view.getDrillDown()).get(0));
        view.setPaletteProperties(dataStore.loadPalette());
        return view;
    }

    @POST
    public DesignView post(@MultipartForm DesignView view) {
        if ("create".equals(view.getAction())) {
            return create(view);
        } else {
            return apply(view).get(0);
        }
    }

    private DesignView create(DesignView view) {
        Tuple t = apply(view);
        view = t.get(0);
        Mosaic m = t.get(1);

        File f = new Creator().writeToFile(m);
        view.setCreatedFile(f.getAbsolutePath());
        return view;
    }

    private Tuple apply(DesignView view) {
        if (view.getNumWide() <= 1) {
            view.setNumWide(2);
        }

        if (view.getDrillDown() < 1) {
            view.setDrillDown(1);
        }

        BufferedImage newTarget = Util.bytesToBufferedImage(view.getTarget());
        if (newTarget != null) {
            SessionState.target = newTarget;
        }

        int maxWide = Util.maxNumWide(dataStore.loadPalette().getPalette(), SessionState.target.getWidth(), SessionState.target.getHeight());
        if (!view.isAllowReuse() && view.getNumWide() > maxWide) {
            view.setNumWide(maxWide);
        }

        Tuple t = calcPositions(view.getNumWide(), view.isAllowReuse(), ColorSpace.fromString(view.getColorSpace()), view.getDrillDown());
        view.setPositions((int[]) t.get(0));

        view.setPaletteProperties(dataStore.loadPalette());
        return new Tuple(view, t.get(1));
    }

    private Tuple calcPositions(int numWide, boolean allowReuse, ColorSpace colorSpace, int drillDown) {

        Index index = dataStore.loadPalette().getPalette();
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
        return new Tuple(tgtPos, mosaic);
    }
}
