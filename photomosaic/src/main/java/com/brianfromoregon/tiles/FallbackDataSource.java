package com.brianfromoregon.tiles;

import lombok.Getter;
import org.springframework.jdbc.datasource.DelegatingDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class FallbackDataSource extends DelegatingDataSource {
    private final DataSource primary;
    private final DataSource fallback;
    @Getter private Exception primaryException;

    public FallbackDataSource(DataSource primary, DataSource fallback) {
        this.primary = primary;
        this.fallback = fallback;
    }

    @Override public void afterPropertiesSet() {
        try {
            Connection connection = primary.getConnection();
            connection.close();
            setTargetDataSource(primary);
        } catch (SQLException e) {
            setTargetDataSource(fallback);
            primaryException = e;
        }
        super.afterPropertiesSet();
    }
}
