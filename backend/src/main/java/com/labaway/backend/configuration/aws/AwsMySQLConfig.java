package com.labaway.backend.configuration.aws;

import com.labaway.backend.configuration.secrets.SecretsManagerHelper;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class AwsMySQLConfig {

    private final SecretsManagerHelper secretsHelper;

    @Autowired
    public AwsMySQLConfig(SecretsManagerHelper secretsHelper) {
        this.secretsHelper = secretsHelper;
    }

    @Bean
    public DataSource dataSource() throws Exception {
        Map<String, String> secrets = secretsHelper.getSecret("MySQL-Database");

        String username = secrets.get("username");
        String password = secrets.get("password");
        String host = secrets.get("host");
        String dbname = secrets.get("dbname");

        String port = String.valueOf(secrets.get("port"));
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