package org.example.cardgame.application.command.handle.usecase;

import co.com.sofka.domain.generic.DomainEvent;
import reactor.core.publisher.Mono;

public interface BusinessService{
   Mono<Void> doProcessing(DomainEvent event);
}