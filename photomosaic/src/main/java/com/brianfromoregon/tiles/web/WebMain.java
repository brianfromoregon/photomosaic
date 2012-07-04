package com.brianfromoregon.tiles.web;

import com.brianfromoregon.tiles.Index;
import com.brianfromoregon.tiles.Log;
import com.brianfromoregon.tiles.SamplePalette;
import com.brianfromoregon.tiles.Util;
import com.google.common.io.ByteStreams;
import com.googlecode.htmleasy.HtmleasyProviders;
import com.googlecode.htmleasy.HtmleasyServletDispatcher;
import freemarker.ext.servlet.FreemarkerServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.ws.rs.core.Application;
import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.channels.NetworkChannel;
import java.util.HashSet;
import java.util.Set;

/**
 * Starts a Jetty server and opens a browser.
 */
public class WebMain {
    public static class JaxRsApplication extends Application {

        public Set<Class<?>> getClasses() {
            Set<Class<?>> myServices = new HashSet<Class<?>>();

            // Add my own JAX-RS annotated classes
            myServices.add(PaletteController.class);
            myServices.add(DesignController.class);

            // Add Htmleasy Providers
            myServices.addAll(HtmleasyProviders.getClasses());

            return myServices;
        }
    }

    public static void main(String[] args) throws IOException {
        Index index = SamplePalette.SOLID_COLORS.generate();

        AbstractApplicationContext context = new ClassPathXmlApplicationContext("services.xml");
        JdbcTemplate jdbc = context.getBean("jdbcTemplate", JdbcTemplate.class);

        Server server = new Server(0);

        SessionState.palette = index;
        SessionState.target = Util.bytesToBufferedImage(ByteStreams.toByteArray(WebMain.class.getResourceAsStream("brian.jpg")));
        ServletContextHandler servletContextHandler = new ServletContextHandler(server, "/", true, false);
        ServletHolder htmlEasy = new ServletHolder(new HtmleasyServletDispatcher());
        htmlEasy.setInitParameter(Application.class.getName(), JaxRsApplication.class.getName());
        ServletHolder freemarker = new ServletHolder(new FreemarkerServlet());
        freemarker.setInitParameter("TemplatePath", "class://" + WebMain.class.getPackage().getName().replaceAll("\\.", "/"));
        servletContextHandler.addServlet(freemarker, "*.ftl");
        servletContextHandler.addServlet(htmlEasy, "/");
        try {
            server.start();
        } catch (Exception e) {
            Log.log("Could not start web server, quitting.");
            e.printStackTrace();
        }

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

        context.registerShutdownHook();
    }
}
