package com.brianfromoregon.tiles.persist;

import com.brianfromoregon.tiles.Index;
import com.brianfromoregon.tiles.SamplePalette;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class PaletteDescriptor {
    private static final PaletteDescriptor DEFAULT;
    static {
        DEFAULT = new PaletteDescriptor();
        DEFAULT.setRoots(new HashSet<String>());
        DEFAULT.setExcludes(new HashSet<String>());
        DEFAULT.setPalette(SamplePalette.SOLID_COLORS.generate(32, 22));
    }

    public static final PaletteDescriptor getDefault(int w, int h) {
        if (w == DEFAULT.getPalette().width && h == DEFAULT.getPalette().height)
            return getDefault();

        PaletteDescriptor p = new PaletteDescriptor();
        p.setRoots(new HashSet<String>());
        p.setExcludes(new HashSet<String>());
        p.setPalette(SamplePalette.SOLID_COLORS.generate(w, h));
        return p;
    }

    public static final PaletteDescriptor getDefault() {
        return DEFAULT;
    }

    @Getter @Setter @Id private int id=0;
    @Getter @Setter @ElementCollection(fetch = FetchType.EAGER) private Set<String> roots;
    @Getter @Setter @ElementCollection(fetch = FetchType.EAGER) private Set<String> excludes;
    @Getter @Setter @Lob private Index palette;
}
