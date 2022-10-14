package org.example.cardgame.application.queries.handle.materialize.gameview;

import co.com.sofka.domain.generic.DomainEvent;
import org.example.cardgame.application.queries.generic.ModelViewRepository;
import org.example.cardgame.application.queries.handle.materialize.MaterializeService;
import org.example.cardgame.domain.events.RondaIniciada;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class HandleRondaIniciada  implements MaterializeService {
    private final ModelViewRepository repository;

    public HandleRondaIniciada(ModelViewRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Void> doProcessing(DomainEvent input) {
        var event = (RondaIniciada)input;
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> query = new HashMap<>();

        data.put("fecha", Instant.now());
        data.put("tablero.habilitado", true);

        query.put("_id", event.aggregateRootId());

        return repository.update(query, data, COLLECTION_GAME_VIEW);
    }
}
