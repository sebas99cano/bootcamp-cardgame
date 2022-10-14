package org.example.cardgame.application.command.generic;

import co.com.sofka.domain.generic.DomainEvent;

public interface EventBus {

    void publish(DomainEvent event);

    void publishError(Throwable errorEvent);
}