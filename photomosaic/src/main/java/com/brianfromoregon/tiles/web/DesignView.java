package com.brianfromoregon.tiles.web;

import com.brianfromoregon.tiles.ColorSpace;
import com.brianfromoregon.tiles.persist.DataStore;
import com.googlecode.htmleasy.ViewWith;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import java.util.HashSet;
import java.util.Set;

@ViewWith("design.ftl")
@Component
public class DesignView {

    @Named DataStore dataStore;

    @FormParam("numWide") @Getter @Setter private int numWide;
    @FormParam("allowReuse") @Getter @Setter private boolean allowReuse;
    @FormParam("colorSpace") @Getter @Setter private String colorSpace;
    @FormParam("drillDown") @Getter @Setter private int drillDown;
    @FormParam("target") @Getter @Setter private byte[] target;
    @Getter @Setter private int[] positions;

    public boolean success() {
        return positions != null;
    }

    public int numTall() {
        return positions.length / getNumWide();
    }

    public int width() {
        return dataStore.loadPalette().getPalette().width;
    }

    public int height() {
        return dataStore.loadPalette().getPalette().height;
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
        return dataStore.loadPalette().getPalette().images.size();
    }
}
