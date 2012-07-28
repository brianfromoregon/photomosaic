package com.brianfromoregon.tiles.web;

import com.brianfromoregon.tiles.*;
import com.brianfromoregon.tiles.persist.DataStore;
import com.brianfromoregon.tiles.persist.PaletteDescriptor;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.googlecode.htmleasy.RedirectException;
import org.jboss.resteasy.annotations.Form;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.io.File;
import java.util.List;
import java.util.Set;

@Path("palette")
@Component
public class PaletteController {

    @Inject DataStore dataStore;
    @Inject FallbackDataSource fallbackDataSource;

    @GET
    @Path("{idx}")
    @Produces("image/jpeg")
    public byte[] img(@PathParam("idx") int idx) {
        return dataStore.loadPalette().getPalette().images.get(idx).jpeg;
    }

    @GET
    public PaletteView all() {
        PaletteView view = new PaletteView();
        PaletteDescriptor loaded = dataStore.loadPalette();
        view.setExcludesList(loaded.getExcludes());
        view.setRootsList(loaded.getRoots());
        view.setPaletteProperties(loaded);
        if (fallbackDataSource.getConnectException() != null) {
            view.setShortTermMemory(true);
        }
        return view;
    }

    @POST
    public PaletteView refresh(@Form PaletteView view) {
        if (view.getWidth() < 1) {
            view.setWidth(1);
        }
        if (view.getHeight() < 1) {
            view.setHeight(1);
        }
        boolean shouldIndex = true;
        Iterable<String> roots = view.getRootsList();
        List<File> rootFiles = Lists.newArrayList();
        if (Iterables.isEmpty(roots)) {
            dataStore.savePalette(PaletteDescriptor.getDefault(view.getWidth(), view.getHeight()));
            shouldIndex = false;
        } else {
            for (String root : roots) {
                File f = new File(root);
                if (!f.exists() || !f.isDirectory()) {
                    view.getErrors().put("roots", "Could not find a directory with this name: " + root);
                    break;
                }
                rootFiles.add(f);
            }
        }

        Iterable<String> excludes = view.getExcludesList();
        Set<File> excludeFiles = Sets.newHashSet();
        for (String exclude : excludes) {
            File f = new File(exclude);
            if (!f.exists()) {
                view.getErrors().put("excludes", "This file does not exist: "+exclude);
                break;
            }
            excludeFiles.add(f);
        }

        shouldIndex &= view.getErrors().isEmpty();
        if (shouldIndex) {
            if (!ImageMagick.isImageMagickValid()) {
                throw new RedirectException(SettingsController.class);
            }

            Index palette = new Indexer(dataStore.loadPalette().getPalette()).index(rootFiles, excludeFiles, view.getWidth(), view.getHeight());
            if (palette.images.isEmpty()) {
                view.getErrors().put("roots", "Didn't find any matching images");
            } else {
                PaletteDescriptor descriptor = new PaletteDescriptor();
                descriptor.setRoots(view.getRootsList());
                descriptor.setExcludes(view.getExcludesList());
                descriptor.setPalette(palette);
                dataStore.savePalette(descriptor);
            }
        }

        view.setPaletteProperties(dataStore.loadPalette());

        return view;
    }
}