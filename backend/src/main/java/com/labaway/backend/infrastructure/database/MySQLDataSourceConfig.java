package com.labaway.backend.infrastructure.database;

import com.labaway.backend.configuration.properties.DatabaseProperties;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("!test")
public class MySQLDataSourceConfig {

    private final DatabaseProperties databaseProperties;

    public MySQLDataSourceConfig(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }

    @Bean
    public DataSource dataSource() {

        String username = databaseProperties.username();
        String password = databaseProperties.password();
        String host = databaseProperties.host();
        String dbname = databaseProperties.name();

        String port = String.valueOf(databaseProperties.port());
        String url = String.format("jdbc:mysql://%s:%s/%s", host, port, dbname);

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        dataSource.addDataSourceProperty("cachePrepStmts", "true");
        dataSource.addDataSourceProperty("prepStmtCacheSize", "250");
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return dataSource;
    }

}