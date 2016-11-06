package com.fq.ifs.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author jifang
 * @since 2016/11/6 下午2:43.
 */
public class ConnectionManager {

    public static Connection getConnection(String file) {
        return getConnection(getDataSourceHikari(file));
    }

    public static Connection getConnection(DataSource dataSource) {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static DataSource dataSource;

    private static DataSource getDataSourceHikari(String file) {
        if (dataSource == null) {
            synchronized (ConnectionManager.class) {
                if (dataSource == null) {
                    Properties properties = loadConfig(file);
                    HikariConfig config = new HikariConfig();
                    config.setDriverClassName(properties.getProperty("mysql.driver.class"));
                    config.setJdbcUrl(properties.getProperty("mysql.url"));
                    config.setUsername(properties.getProperty("mysql.user"));
                    config.setPassword(properties.getProperty("mysql.password"));

                    // 设置连接池最大连接数
                    config.setMaximumPoolSize(Integer.valueOf(properties.getProperty("pool.max.size")));
                    // 设置连接池最少连接数
                    config.setMinimumIdle(Integer.valueOf(properties.getProperty("pool.min.size")));
                    // 设置最大空闲时间
                    config.setIdleTimeout(Integer.valueOf(properties.getProperty("pool.max.idle_time")));
                    // 设置连接最长寿命
                    config.setMaxLifetime(Integer.valueOf(properties.getProperty("pool.max.life_time")));
                    dataSource = new HikariDataSource(config);
                }
            }
        }

        return dataSource;
    }

    private static Properties loadConfig(String file) {

        Properties properties = new Properties();
        try {
            InputStream inputStream = ConnectionManager.class.getClassLoader().getResourceAsStream(file);
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
