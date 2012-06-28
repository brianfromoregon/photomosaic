package com.brianfromoregon.tiles.web;

import com.brianfromoregon.tiles.Index;
import com.brianfromoregon.tiles.indexes.Palette;
import com.brianfromoregon.tiles.web.JaxRsApplication;
import com.googlecode.htmleasy.HtmleasyServletDispatcher;
import freemarker.ext.servlet.FreemarkerServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.ws.rs.core.Application;

/**
 *
 */
public class PaletteImageServer {
    final Server server;

    public PaletteImageServer(int port, final Index index) {
        server = new Server(port);
        Palette.index=index;
        ServletContextHandler servletContextHandler = new ServletContextHandler(server, "/", true, false);
        ServletHolder htmlEasy = new ServletHolder(new HtmleasyServletDispatcher());
        htmlEasy.setInitParameter(Application.class.getName(), JaxRsApplication.class.getName());
        ServletHolder freemarker = new ServletHolder(new FreemarkerServlet());
        freemarker.setInitParameter("TemplatePath", "class://"+getClass().getPackage().getName().replaceAll("\\.", "/"));
        servletContextHandler.addServlet(freemarker, "*.ftl");
        servletContextHandler.addServlet(htmlEasy, "/");
    }

    public void serve() {
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
