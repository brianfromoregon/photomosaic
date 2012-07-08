package com.brianfromoregon.tiles.web;

import com.brianfromoregon.tiles.Log;
import com.brianfromoregon.tiles.Services;
import com.brianfromoregon.tiles.Util;
import com.google.common.io.ByteStreams;
import org.eclipse.jetty.server.Server;
import org.jboss.resteasy.plugins.spring.SpringBeanProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.channels.NetworkChannel;

/**
 * Starts a Jetty server and opens a browser.
 */
public class WebMain {

    public static void main(String[] args) throws IOException {
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(Services.class);
        context.addBeanFactoryPostProcessor(new SpringBeanProcessor());
        context.registerShutdownHook();
        SessionState.target = Util.bytesToBufferedImage(ByteStreams.toByteArray(WebMain.class.getResourceAsStream("brian.jpg")));

        Server server = context.getBean(Server.class);
        int port = ((InetSocketAddress) ((NetworkChannel) server.getConnectors()[0].getConnection()).getLocalAddress()).getPort();
        String url = "http://localhost:" + port + "/design";
        Desktop desktop;
        if (Desktop.isDesktopSupported() && (desktop = Desktop.getDesktop()).isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URI(url));
                Log.log("Just opened %s in a browser.", url);
            } catch (Exception ioe) {
                Log.log("Could not automatically open browser because: %s", ioe.getMessage());
                Log.log("Please open %s in a browser yourself.", url);
            }
        } else {
            Log.log("Open %s in a browser.", url);
        }
    }
}
