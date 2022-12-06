package org.example.cardgame.application.master;

import com.mongodb.ConnectionString;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;


@SpringBootApplication
public class AppMaster {

    public static void main(String[] args) {
        SpringApplication.run(AppMaster.class, args);
    }

    @Bean
    public ReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory(ConfigProperties configProperties) {
        return new SimpleReactiveMongoDatabaseFactory(new ConnectionString(configProperties.getUriDb()));
    }

}
