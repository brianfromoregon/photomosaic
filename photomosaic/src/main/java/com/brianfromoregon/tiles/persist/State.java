package com.brianfromoregon.tiles.persist;

import com.brianfromoregon.tiles.Index;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class State {
    @Id private int id=0;
    public String roots;
    public String excludes;
    @Lob public Index palette;
}
