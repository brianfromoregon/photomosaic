package com.brianfromoregon.tiles.web;

import com.brianfromoregon.tiles.Index;
import com.brianfromoregon.tiles.Indexer;
import com.brianfromoregon.tiles.SamplePalette;
import com.brianfromoregon.tiles.ServerSettings;
import com.brianfromoregon.tiles.persist.Repository;
import com.brianfromoregon.tiles.persist.State;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.googlecode.htmleasy.RedirectException;
import org.jboss.resteasy.annotations.Form;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import java.io.File;
import java.util.List;
import java.util.Set;

@Path("palette")
public class PaletteController {

    @PersistenceContext
    EntityManager entityManager;

    @GET
    @Path("{idx}")
    @Produces("image/jpeg")
    public byte[] img(@PathParam("idx") int idx) {
        return Repository.INSTANCE.get().palette.images.get(idx).jpeg;
    }

    @GET
    public PaletteView.Response all() {
        PaletteView.Response response = new PaletteView.Response();
        State loaded = Repository.INSTANCE.get();
        response.setExcludes(loaded.excludes);
        response.setRoots(loaded.roots);
        return response;
    }

    @POST
    public PaletteView.Response refresh(@Form PaletteView request) {
        PaletteView.Response response = request.asResponse();

        Iterable<String> roots = request.rootsList();
        List<File> rootFiles = Lists.newArrayList();
        if (Iterables.isEmpty(roots)) {
            response.getErrors().put("roots", "Need to enter at least one search root.");
        } else {
            for (String root : roots) {
                File f = new File(root);
                if (!f.exists() || !f.isDirectory()) {
                    response.getErrors().put("roots", "Could not find a directory with this name: " + root);
                    break;
                }
                rootFiles.add(f);
            }
        }

        Iterable<String> excludes = request.excludesList();
        Set<File> excludeFiles = Sets.newHashSet();
        for (String exclude : excludes) {
            File f = new File(exclude);
            if (!f.exists()) {
                response.getErrors().put("excludes", "This file does not exist: "+exclude);
                break;
            }
            excludeFiles.add(f);
        }

        if (response.getErrors().isEmpty()) {
            if (!ServerSettings.isImageMagickAvailable()) {
                throw new RedirectException(SettingsController.class);
            }

            Index palette = new Indexer(Repository.INSTANCE.get().palette).index(rootFiles, excludeFiles, SamplePalette.W, SamplePalette.H);
            if (palette.images.isEmpty()) {
                response.getErrors().put("roots", "Didn't find any matching images");
            } else {
                Repository.INSTANCE.update(request.getRoots(), request.getExcludes(), palette);
            }
        }

        return response;
    }
}