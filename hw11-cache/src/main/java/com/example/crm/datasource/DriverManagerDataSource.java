package com.example.crm.datasource;

import com.example.exception.PropertiesLoadException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class DriverManagerDataSource implements DataSource {
    private DataSource dataSourcePool;

    public DriverManagerDataSource(String propertiesFileName) {
        Properties properties = loadProperties(propertiesFileName);
        createConnectionPool(properties);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.dataSourcePool.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PrintWriter getLogWriter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLogWriter(PrintWriter out) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLoginTimeout(int seconds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLoginTimeout()  {
        throw new UnsupportedOperationException();
    }

    @Override
    public Logger getParentLogger() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T unwrap(Class<T> iface) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        throw new UnsupportedOperationException();
    }

    private void createConnectionPool(Properties properties) {
        var config = new HikariConfig(properties);

        config.setRegisterMbeans(true);
        config.setAutoCommit(false);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        this.dataSourcePool = new HikariDataSource(config);
    }

    private Properties loadProperties(String configFileName) {
        try (var inputStream = getClass().getClassLoader().getResourceAsStream(configFileName)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new PropertiesLoadException(configFileName);
        }
    }
}
