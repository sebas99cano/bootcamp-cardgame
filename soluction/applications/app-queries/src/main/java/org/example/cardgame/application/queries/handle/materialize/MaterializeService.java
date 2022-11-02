package org.example.cardgame.application.queries.handle.materialize;

import org.example.cardgame.generic.DomainEvent;
import reactor.core.publisher.Mono;

public interface MaterializeService{
   String COLLECTION_GAME_VIEW = "gameview";
   String COLLECTION_MAZO_VIEW = "mazoview";
   Mono<Void> doProcessing(DomainEvent input);
}