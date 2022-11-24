package org.example.cardgame.application.command;

import com.rabbitmq.client.Connection;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Mono;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Objects;

@SpringBootApplication
public class AppCommand {

    public static void main(String[] args) {
        SpringApplication.run(AppCommand.class, args);
    }


}
