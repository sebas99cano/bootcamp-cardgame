package org.example.cardgame.application.queries.handle.materialize.gameview;

import co.com.sofka.domain.generic.DomainEvent;
import org.example.cardgame.application.queries.generic.ModelViewRepository;
import org.example.cardgame.application.queries.handle.materialize.MaterializeService;
import org.example.cardgame.domain.events.JuegoFinalizado;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class HandleJuegoFinalizado  implements MaterializeService {
    private final ModelViewRepository repository;

    public HandleJuegoFinalizado(ModelViewRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Void> doProcessing(DomainEvent input) {
        var event = (JuegoFinalizado)input;
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> query = new HashMap<>();
        data.put("fecha", Instant.now());
        data.put("ganador.alias", event.getAlias());
        data.put("ganador.jugadorId", event.getJugadorId().value());
        data.put("finalizado", true);

        query.put("_id", event.aggregateRootId());

        return repository.update(query, data, COLLECTION_GAME_VIEW);
    }
}
