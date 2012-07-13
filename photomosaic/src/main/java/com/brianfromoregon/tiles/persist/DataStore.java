package com.brianfromoregon.tiles.persist;

import lombok.Getter;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;

public class DataStore {

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public void savePalette(PaletteDescriptor paletteDescriptor) {
        entityManager.merge(paletteDescriptor);
    }

    @Transactional
    public PaletteDescriptor loadPalette() {
        return entityManager.find(PaletteDescriptor.class, 0);
    }

    @Transactional
    public void saveImageMagickDir(String imageMagickDir) {
        ImageMagickDir entity = new ImageMagickDir();
        entity.setDir(imageMagickDir);
        entityManager.merge(entity);
    }

    @Transactional
    public String loadImageMagickDir() {
        ImageMagickDir entity = entityManager.find(ImageMagickDir.class, 0);
        if (entity == null)
            return null;
        else
            return entity.getDir();
    }

    @Entity
    private static class ImageMagickDir {
        @Getter @Setter @Id private int id = 0;
        @Getter @Setter private String dir;
    }
}
