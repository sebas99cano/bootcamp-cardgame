package org.example.cardgame.application.queries.handle.materialize.gameview;

import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofka.domain.generic.Identity;
import org.example.cardgame.application.queries.generic.ModelViewRepository;
import org.example.cardgame.application.queries.handle.materialize.MaterializeService;
import org.example.cardgame.domain.events.TableroCreado;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class HandleTableroCreado  implements MaterializeService {
    private final ModelViewRepository repository;

    public HandleTableroCreado(ModelViewRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Void> doProcessing(DomainEvent input) {
        var event = (TableroCreado)input;
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> query = new HashMap<>();
        var jugadores = event.getJugadorIds().stream()
                .map(Identity::value)
                .collect(Collectors.toList());

        data.put("fecha", Instant.now());
        data.put("tablero.id", event.getTableroId().value());
        data.put("tablero.cartas", new HashMap<>());
        data.put("tablero.jugadores", jugadores);
        data.put("tablero.habilitado", false);
        data.put("iniciado", true);

        query.put("_id", event.aggregateRootId());

        return repository.update(query, data, COLLECTION_GAME_VIEW);
    }
}
