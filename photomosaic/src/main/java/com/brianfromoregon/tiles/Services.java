package com.brianfromoregon.tiles;

import com.brianfromoregon.tiles.persist.DataStore;
import com.brianfromoregon.tiles.persist.PaletteDescriptor;
import com.brianfromoregon.tiles.web.DesignController;
import com.brianfromoregon.tiles.web.PaletteController;
import com.brianfromoregon.tiles.web.SettingsController;
import com.brianfromoregon.tiles.web.WebMain;
import com.googlecode.htmleasy.HtmleasyProviders;
import com.googlecode.htmleasy.HtmleasyServletDispatcher;
import freemarker.ext.servlet.FreemarkerServlet;
import org.apache.commons.dbcp.BasicDataSource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.spring.SpringBeanProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@Configuration
@PropertySource("classpath:/com/brianfromoregon/tiles/jdbc.properties")
@EnableTransactionManagement
//@ComponentScan(basePackages = "com.brianfromoregon.tiles.web")
public class Services {

    @Autowired Environment env;

    @Bean(destroyMethod = "close") public DataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(env.getProperty("jdbc.driverClassName"));
        ds.setUrl(env.getProperty("jdbc.url"));
        ds.setUsername(env.getProperty("jdbc.username"));
        ds.setPassword(env.getProperty("jdbc.password"));
        return ds;
    }

    @Bean HtmleasyServletDispatcher htmleasyDispatcher() {
        return new HtmleasyServletDispatcher();
    }

    @Bean(initMethod = "start", destroyMethod = "stop") public Server jettyServer() {
        Server server = new Server(0);
        ServletContextHandler servletContextHandler = new ServletContextHandler(server, "/", true, false);
        ServletHolder htmlEasy = new ServletHolder(htmleasyDispatcher());
        htmlEasy.setInitParameter(Application.class.getName(), JaxRsApplication.class.getName());
        ServletHolder freemarker = new ServletHolder(new FreemarkerServlet());
        freemarker.setInitParameter("TemplatePath", "class://" + WebMain.class.getPackage().getName().replaceAll("\\.", "/"));
        servletContextHandler.addServlet(freemarker, "*.ftl");
        servletContextHandler.addServlet(htmlEasy, "/");
        return server;
    }

    @Bean public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setDataSource(dataSource());
        bean.setPackagesToScan(PaletteDescriptor.class.getPackage().getName());
        bean.setJpaVendorAdapter(new HibernateJpaVendorAdapter() {{
            setGenerateDdl(true);
            setShowSql(true);
        }});
        return bean;
    }

    @Bean public PlatformTransactionManager txManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean public DataStore paletteRepository() {
        return new DataStore();
    }

//    @Bean public SpringBeanProcessor restSpringBridge() {
//        return new SpringBeanProcessor();
//    }

//    @PostConstruct public void restSpringBridgeInit() {
//        restSpringBridge().setDispatcher(htmleasyDispatcher().getDispatcher());
//    }

    public static class JaxRsApplication extends Application {

        public Set<Class<?>> getClasses() {

            Set<Class<?>> myServices = new HashSet<Class<?>>();

//            // Add my own JAX-RS annotated classes
            myServices.add(PaletteController.class);
            myServices.add(DesignController.class);
            myServices.add(SettingsController.class);

            // Add Htmleasy Providers
            myServices.addAll(HtmleasyProviders.getClasses());

            return myServices;
        }
    }
}
