package com.brianfromoregon.tiles;

import com.brianfromoregon.tiles.persist.DataStore;
import com.brianfromoregon.tiles.persist.PaletteDescriptor;
import com.brianfromoregon.tiles.web.JaxRsApp;
import com.brianfromoregon.tiles.web.SessionState;
import com.google.common.io.ByteStreams;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
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
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:/com/brianfromoregon/tiles/jdbc.properties")
@EnableTransactionManagement
@ComponentScan(basePackages = "com.brianfromoregon.tiles.web")
public class ApplicationContext {

    @Autowired Environment env;

    @Bean(destroyMethod = "close") public DataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(env.getProperty("jdbc.driverClassName"));
        ds.setUrl(env.getProperty("jdbc.url"));
        ds.setUsername(env.getProperty("jdbc.username"));
        ds.setPassword(env.getProperty("jdbc.password"));
        return ds;
    }

    @Bean public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setDataSource(dataSource());
        bean.setPackagesToScan(PaletteDescriptor.class.getPackage().getName());
        bean.setJpaVendorAdapter(new HibernateJpaVendorAdapter() {{
            setGenerateDdl(true);
//            setShowSql(true);
        }});
        return bean;
    }

    @Bean public PlatformTransactionManager txManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean public DataStore dataStore() {
        return new DataStore();
    }

    @Bean public Init init() {
        return new Init();
    }

    @Bean public ImageMagick imageMagick() {
        return new ImageMagick();
    }

    public static class Init {
        @Inject DataStore dataStore;

        @PostConstruct public void init() throws Exception {
            SessionState.target = Util.bytesToBufferedImage(ByteStreams.toByteArray(JaxRsApp.class.getResourceAsStream("brian.jpg")));
            PaletteDescriptor palette = dataStore.loadPalette();
            if (palette == null) {
                dataStore.savePalette(PaletteDescriptor.DEFAULT);
            }
        }
    }

}
