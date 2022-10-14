package org.example.cardgame.application.queries.handle.materialize.gameview;

import co.com.sofka.domain.generic.DomainEvent;
import org.example.cardgame.application.queries.generic.ModelViewRepository;
import org.example.cardgame.application.queries.handle.materialize.MaterializeService;
import org.example.cardgame.domain.events.JuegoCreado;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class HandleJuegoCreado  implements MaterializeService {

    private final ModelViewRepository repository;

    public HandleJuegoCreado(ModelViewRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Void> doProcessing(DomainEvent input) {
        var event = (JuegoCreado)input;
        Map<String, Object> data = new HashMap<>();
        data.put("_id", event.aggregateRootId());
        data.put("fecha", Instant.now());
        data.put("uid", event.getJugadorPrincipal().value());
        data.put("iniciado", false);
        data.put("finalizado", false);
        data.put("cantidadJugadores", 0);
        data.put("jugadores", new HashMap<>());
        return repository.create(data, COLLECTION_GAME_VIEW);
    }
}
