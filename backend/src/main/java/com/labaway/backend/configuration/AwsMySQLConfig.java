package com.labaway.backend.configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class AwsMySQLConfig {

    private final AwsSecretsManagerHelper secretsHelper;

    public AwsMySQLConfig() {
        this.secretsHelper = new AwsSecretsManagerHelper();
    }

    @Bean
    public DataSource dataSource() throws Exception {
        Map<String, String> secrets = secretsHelper.getSecret("MySQL-DB");

        String username = secrets.get("username");
        String password = secrets.get("password");
        String host = secrets.get("host");
        String dbname = secrets.get("dbname");

        String port = String.valueOf(secrets.get("port"));
        String engine = secrets.get("engine");

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