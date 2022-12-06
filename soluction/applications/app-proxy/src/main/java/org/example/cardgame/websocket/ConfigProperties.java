package org.example.cardgame.websocket;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "microservice")
public class ConfigProperties {

    private String exchange;
    private String queue;
    private String routingKey;
    private String uriBus;

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

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }
}