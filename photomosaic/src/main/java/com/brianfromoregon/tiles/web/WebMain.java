package com.brianfromoregon.tiles.web;

import com.brianfromoregon.tiles.*;
import com.brianfromoregon.tiles.persist.Repository;
import com.brianfromoregon.tiles.persist.State;
import com.google.common.io.ByteStreams;
import org.eclipse.jetty.server.Server;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.channels.NetworkChannel;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

/**
 * Starts a Jetty server and opens a browser.
 */
public class WebMain {

    public static void main(String[] args) throws IOException {
//
//        Path p = Paths.get(new File("C:\\Users").toURI());
//        System.out.println(FileSystems.getDefault().getPathMatcher("glob:C:\\Users").matches(p));
//        if (true) return;
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(Services.class);
        context.registerShutdownHook();
        Repository.INSTANCE = context.getBean(Repository.class);
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
