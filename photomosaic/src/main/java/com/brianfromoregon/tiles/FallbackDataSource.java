package com.brianfromoregon.tiles;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DelegatingDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class FallbackDataSource extends DelegatingDataSource {
    private static Logger Log = LoggerFactory.getLogger(FallbackDataSource.class);
    private final DataSource primary;
    private final DataSource secondary;
    @Getter private Exception connectException;

    public FallbackDataSource(DataSource primary, DataSource secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    @Override public void afterPropertiesSet() {
        try {
            Connection connection = primary.getConnection();
            connection.close();
            setTargetDataSource(primary);
        } catch (SQLException e) {
            Log.info("Could not get connection from primary DataSource, falling back to secondary", e);
            setTargetDataSource(secondary);
            connectException = e;
        }
        super.afterPropertiesSet();
    }
}
