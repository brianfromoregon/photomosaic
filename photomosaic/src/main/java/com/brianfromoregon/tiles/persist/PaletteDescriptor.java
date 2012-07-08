package com.brianfromoregon.tiles.persist;

import com.brianfromoregon.tiles.Index;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.util.List;

@Entity
public class PaletteDescriptor {
    @Getter @Setter @Id private int id=0;
    @Getter @Setter @ElementCollection private List<String> roots;
    @Getter @Setter @ElementCollection private List<String> excludes;
    @Getter @Setter @Lob private Index palette;
}
