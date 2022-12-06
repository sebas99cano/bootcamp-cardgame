package org.example.cardgame.application.command;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "microservice")
public class ConfigProperties {

    private String exchange;
    private String queue;
    private String storeName;
    private String routingKey;
    private String uriMaster;
    private String uriBus;
    private String uriDb;


    public void setUriDb(String uriDb) {
        this.uriDb = uriDb;
    }

    public String getUriDb() {
        return uriDb;
    }

    public void setUriBus(String uriBus) {
        this.uriBus = uriBus;
    }

    public String getUriBus() {
        return uriBus;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public void setUriMaster(String uriMaster) {
        this.uriMaster = uriMaster;
    }

    public String getUriMaster() {
        return uriMaster;
    }
}