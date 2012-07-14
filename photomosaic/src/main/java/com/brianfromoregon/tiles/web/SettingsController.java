package com.brianfromoregon.tiles.web;

import com.brianfromoregon.tiles.ImageMagick;
import org.jboss.resteasy.annotations.Form;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("settings")
@Component
public class SettingsController {
    @GET
    public SettingsView get() {
        SettingsView view = new SettingsView();
        view.setImagemagick(ImageMagick.getImageMagickDir());
        view.setValid(ImageMagick.isImageMagickValid());
        return view;
    }

    @POST
    public SettingsView post(@Form SettingsView form) {
        ImageMagick.setImageMagickDir(form.getImagemagick());
        form.setValid(ImageMagick.isImageMagickValid());
        return form;
    }
}
