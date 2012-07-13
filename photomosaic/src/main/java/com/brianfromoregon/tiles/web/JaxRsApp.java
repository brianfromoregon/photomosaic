package com.brianfromoregon.tiles.web;

import com.googlecode.htmleasy.HtmleasyProviders;

import javax.ws.rs.core.Application;
import java.util.Set;

public class JaxRsApp extends Application {
    public Set<Class<?>> getClasses() {
        return HtmleasyProviders.getClasses();
    }
}
