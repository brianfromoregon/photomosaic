package com.brianfromoregon.tiles.web;

import com.brianfromoregon.tiles.ColorSpace;
import com.brianfromoregon.tiles.persist.PaletteDescriptor;
import com.googlecode.htmleasy.ViewWith;
import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.FormParam;
import java.util.HashSet;
import java.util.Set;

@ViewWith("design.ftl")
public class DesignView {

    @FormParam("numWide") @Getter @Setter private int numWide;
    @FormParam("allowReuse") @Getter @Setter private boolean allowReuse;
    @FormParam("colorSpace") @Getter @Setter private String colorSpace;
    @FormParam("drillDown") @Getter @Setter private int drillDown;
    @FormParam("target") @Getter @Setter private byte[] target;
    @Getter @Setter private int[] positions;
    @Getter private int width;
    @Getter private int height;
    @Getter private int paletteSize;

    public boolean success() {
        return positions != null;
    }

    public int numTall() {
        return positions.length / getNumWide();
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

    public void setPaletteProperties(PaletteDescriptor palette) {
        this.width = palette.getPalette().width;
        this.height = palette.getPalette().height;
        this.paletteSize = palette.getPalette().images.size();
    }
}
