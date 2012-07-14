package com.brianfromoregon.tiles.web;

import com.googlecode.htmleasy.ViewWith;
import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.FormParam;

@ViewWith("settings.ftl")
public class SettingsView {
    @FormParam("imagemagick") @Getter @Setter private String imagemagick;
    @Getter @Setter private boolean valid;
}
