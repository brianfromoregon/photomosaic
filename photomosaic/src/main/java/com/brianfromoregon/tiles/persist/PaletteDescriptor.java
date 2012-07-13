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
    public static final PaletteDescriptor DEFAULT;
    static {
        DEFAULT = new PaletteDescriptor();
        DEFAULT.setRoots(new HashSet<String>());
        DEFAULT.setExcludes(new HashSet<String>());
        DEFAULT.setPalette(SamplePalette.SOLID_COLORS.generate());
    }

    @Getter @Setter @Id private int id=0;
    @Getter @Setter @ElementCollection(fetch = FetchType.EAGER) private Set<String> roots;
    @Getter @Setter @ElementCollection(fetch = FetchType.EAGER) private Set<String> excludes;
    @Getter @Setter @Lob private Index palette;
}
