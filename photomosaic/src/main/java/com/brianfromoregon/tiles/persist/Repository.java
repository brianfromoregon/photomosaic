package com.brianfromoregon.tiles.persist;

import com.brianfromoregon.tiles.Index;
import com.brianfromoregon.tiles.Log;
import com.brianfromoregon.tiles.SamplePalette;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class Repository {

    public static Repository INSTANCE;

    @PersistenceContext
    private EntityManager entityManager;

    private State loaded;

    @Transactional
    public State get() {
        if (loaded == null)
            load();

        return loaded;
    }

    @Transactional
    public void update(String roots, String excludes, Index palette) {
        State e = new State();
        e.roots = roots;
        e.excludes = excludes;
        e.palette = palette;
        entityManager.merge(e);
        loaded = e;
    }

    @Transactional
    private void load() {
        loaded = entityManager.find(State.class, 0);
        if (loaded == null) {
            update("C:\\Users\\Brian\\Pictures", ".*honeymoon.*\nC:\\Users\\Brian\\Pictures\\Scans\\.*", SamplePalette.SOLID_COLORS.generate());
        }
    }

}
