package br.com.dataeasy.authentication.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class ServiceConfig {

    @Value("${signing.key}")
    private String jwtSigningKey = "";

    @Value("${spring.database.driverClassName}")
    private String driverClassName = "";

    @Value("${spring.datasource.url}")
    private String dataSourceUrl = "";

    @Value("${spring.datasource.username}")
    private String username = "";

    @Value("${spring.datasource.password}")
    private String password = "";

    public String getJwtSigningKey() {
        return jwtSigningKey;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getDataSourceUrl() {
        return dataSourceUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}