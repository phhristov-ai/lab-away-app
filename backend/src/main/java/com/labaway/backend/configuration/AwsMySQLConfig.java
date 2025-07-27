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
        Map<String, String> secrets = secretsHelper.getSecret("MySQL");

        String dbHost = secrets.get("host");
        String dbUsername = secrets.get("username");
        String dbPassword = secrets.get("password");
        String dbName = secrets.get("dbname");
        String dbPort = secrets.get("port");

        String dbUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false", dbHost, dbPort, dbName);

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(5);
        dataSource.setIdleTimeout(30000);
        dataSource.setConnectionTimeout(30000);

        return dataSource;
    }
}