package org.example.cardgame.generic.serialize;

import org.example.cardgame.generic.DomainEvent;
import reactor.core.publisher.Mono;

public interface BusinessService{
   Mono<Void> doProcessing(DomainEvent event);
}