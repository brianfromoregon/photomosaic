package com.brianfromoregon.tiles.web;

import com.brianfromoregon.tiles.ServerSettings;
import com.googlecode.htmleasy.View;

import javax.ws.rs.*;
import java.io.File;

@Path("settings")
public class SettingsController {
    @GET
    public SettingsView get() {
        SettingsView view = new SettingsView();
        try {
            view.setImagemagick(ServerSettings.getImageMagickDir().getAbsolutePath());
        } catch (ServerSettings.ImageMagickNotFound ignored) {
        }
        return view;
    }

    @POST
    public SettingsView post(SettingsView form) {
        File f = new File(form.getImagemagick());
        ServerSettings.createSettingsFile(f);
        return form;
    }
}
