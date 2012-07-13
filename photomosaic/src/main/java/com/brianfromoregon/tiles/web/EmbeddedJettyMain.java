package com.brianfromoregon.tiles.web;

import com.brianfromoregon.tiles.Log;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.springframework.core.io.ClassPathResource;

import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.channels.NetworkChannel;

public class EmbeddedJettyMain {
    public static void main(String[] args) {
        try {
            Server server = new Server(0);
            WebAppContext context = new WebAppContext();
            context.setResourceBase(new ClassPathResource("").getURL().toString());
            context.setConfigurations(new Configuration[]{new WebXmlConfiguration()});
            server.setHandler(context);
            server.start();
            int port;
            try {
                port = ((InetSocketAddress) ((NetworkChannel) server.getConnectors()[0].getConnection()).getLocalAddress()).getPort();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
