package org.example.cardgame.application.master;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "microservice")
public class ConfigProperties {


    private String uriDb;


    public void setUriDb(String uriDb) {
        this.uriDb = uriDb;
    }

    public String getUriDb() {
        return uriDb;
    }


}