package org.example.cardgame.application.queries.handle.materialize.gameview;

import co.com.sofka.domain.generic.DomainEvent;
import org.example.cardgame.application.queries.generic.ModelViewRepository;
import org.example.cardgame.application.queries.handle.materialize.MaterializeService;
import org.example.cardgame.domain.events.TiempoCambiadoDelTablero;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class HandleTiempoCambiadoDelTablero implements MaterializeService {
    private final ModelViewRepository repository;

    public HandleTiempoCambiadoDelTablero(ModelViewRepository repository) {
        this.repository = repository;
    }


    @Override
    public Mono<Void> doProcessing(DomainEvent input) {
        var event = (TiempoCambiadoDelTablero)input;
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> query = new HashMap<>();
        data.put("fecha", Instant.now());
        data.put("tiempo", event.getTiempo());
        data.put("ronda.estaIniciada", true);

        query.put("_id", event.aggregateRootId());

        return repository.update(query, data, COLLECTION_GAME_VIEW);
    }
}
