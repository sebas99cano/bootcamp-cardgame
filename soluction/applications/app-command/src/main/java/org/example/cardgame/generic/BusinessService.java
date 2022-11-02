package org.example.cardgame.generic;

import org.example.cardgame.generic.DomainEvent;
import reactor.core.publisher.Mono;

public interface BusinessService{
   Mono<Void> doProcessing(DomainEvent event);
}