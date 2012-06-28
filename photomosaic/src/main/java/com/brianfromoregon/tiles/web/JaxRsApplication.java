package com.brianfromoregon.tiles.web;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;

import com.brianfromoregon.tiles.indexes.Palette;
import com.googlecode.htmleasy.HtmleasyProviders;

public class JaxRsApplication extends Application {

    public Set<Class<?>> getClasses() {
        Set<Class<?>> myServices = new HashSet<Class<?>>();

        // Add my own JAX-RS annotated classes
        myServices.add(Palette.class);

        // Add Htmleasy Providers
        myServices.addAll(HtmleasyProviders.getClasses());

        return myServices;
    }
}