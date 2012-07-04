package com.brianfromoregon.tiles.web;

import com.brianfromoregon.tiles.Index;
import com.brianfromoregon.tiles.Indexer;
import com.brianfromoregon.tiles.Log;
import com.brianfromoregon.tiles.SamplePalette;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.jboss.resteasy.annotations.Form;

import javax.ws.rs.*;
import java.io.File;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Path("palette")
public class PaletteController {

    @GET
    @Path("{idx}")
    @Produces("image/jpeg")
    public byte[] img(@PathParam("idx") int idx) {
        return SessionState.palette.images.get(idx).jpeg;
    }

    @GET
    public PaletteView.Response all() {
        PaletteView.Response response = new PaletteView.Response();
        response.setExcludes("/Users/Brian/Pics/honeymoon\n/Users/Brian/test");
        response.setRoots("/Users/Brian/Pics");
        return response;
    }

    @POST
    public PaletteView.Response refresh(@Form PaletteView request) {
        PaletteView.Response response = request.asResponse();

        Iterable<String> roots = request.rootsList();
        List<File> rootFiles = Lists.newArrayList();
        if (Iterables.isEmpty(roots)) {
            response.getErrors().put("roots", "Need at least one root");
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
        List<Pattern> excludePatterns = Lists.newArrayList();
        for (String exclude : excludes) {
            try {
                excludePatterns.add(Pattern.compile(exclude));
            } catch (PatternSyntaxException e) {
                Log.log(e.getMessage());
                response.getErrors().put("excludes", String.format("Trouble with this exclude <a href=\"http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html\">pattern</a>: '%s'", exclude, e.getMessage()));
                break;
            }
        }
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:˜/test");
        ds.setUser("sa");
        ds.setPassword("sa");
        Connection conn = ds.getConnection();
        if (response.getErrors().isEmpty()) {
            Index palette = new Indexer().index(rootFiles, SamplePalette.W, SamplePalette.H);
            if (palette.images.isEmpty()) {
                response.getErrors().put("roots", "Didn't find any matching images");
            } else {
                SessionState.palette = palette;
            }
        }

        return response;
    }
}