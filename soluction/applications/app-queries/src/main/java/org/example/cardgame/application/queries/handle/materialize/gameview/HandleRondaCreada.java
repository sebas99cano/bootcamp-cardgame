package org.example.cardgame.application.queries.handle.materialize.gameview;

import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofka.domain.generic.Identity;
import org.bson.Document;
import org.example.cardgame.application.queries.generic.ModelViewRepository;
import org.example.cardgame.application.queries.handle.materialize.MaterializeService;
import org.example.cardgame.domain.events.RondaCreada;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class HandleRondaCreada  implements MaterializeService {

    private final ModelViewRepository repository;

    public HandleRondaCreada(ModelViewRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Void> doProcessing(DomainEvent input) {
        var event = (RondaCreada)input;
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> query = new HashMap<>();
        var ronda = event.getRonda().value();
        var document = new Document();
        var jugadores = ronda.jugadores().stream()
                .map(Identity::value)
                .collect(Collectors.toList());

        document.put("jugadores", jugadores);
        document.put("numero", ronda.numero());
        document.put("estaIniciada", false);

        data.put("fecha", Instant.now());
        data.put("tiempo", event.getTiempo());
        data.put("ronda", document);

        query.put("_id", event.aggregateRootId());

        return repository.update(query, data, COLLECTION_GAME_VIEW);

    }
}
