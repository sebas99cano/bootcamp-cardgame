package org.example.cardgame.application.queries.handle.materialize.gameview;

import co.com.sofka.domain.generic.DomainEvent;
import org.example.cardgame.application.queries.generic.ModelViewRepository;
import org.example.cardgame.application.queries.handle.materialize.MaterializeService;
import org.example.cardgame.domain.events.JugadorAgregado;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class HandleJugadorAgregado  implements MaterializeService {
    private final ModelViewRepository repository;

    public HandleJugadorAgregado(ModelViewRepository repository) {
        this.repository = repository;
    }


    @Override
    public Mono<Void> doProcessing(DomainEvent input) {
        var event = (JugadorAgregado)input;
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> query = new HashMap<>();
        query.put("_id", event.aggregateRootId());

        data.put("fecha", Instant.now());
        data.put("jugadores."+event.getJugadorId().value()+".alias", event.getAlias());
        data.put("jugadores."+event.getJugadorId().value()+".jugadorId", event.getJugadorId().value());
        return repository.update(query, data, COLLECTION_GAME_VIEW);
    }

}
