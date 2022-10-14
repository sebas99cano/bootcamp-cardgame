package org.example.cardgame.application.queries.handle.materialize.mazoview;

import co.com.sofka.domain.generic.DomainEvent;
import org.example.cardgame.application.queries.generic.ModelViewRepository;
import org.example.cardgame.application.queries.handle.materialize.MaterializeService;
import org.example.cardgame.application.queries.handle.model.MazoViewModel;
import org.example.cardgame.domain.events.CartaQuitadaDelMazo;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class HandleCartaQuitadaDelMazo  implements MaterializeService {
    private final ModelViewRepository repository;

    public HandleCartaQuitadaDelMazo(ModelViewRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Void> doProcessing(DomainEvent input) {
        var event = (CartaQuitadaDelMazo) input;
        Map<String, Object> query = new HashMap<>();
        Map<String, Object> data = new HashMap<>();

        query.put("juegoId", event.aggregateRootId());
        query.put("uid", event.getJugadorId().value());

        return repository.get(query, MazoViewModel.class, COLLECTION_MAZO_VIEW)
                .flatMap(mazoViewModel -> {
                    var cartaSet =  mazoViewModel.getCartas();
                    cartaSet.removeIf(carta -> event.getCarta().value().cartaId().value().equals(carta.getCartaId()));
                    data.put("cartas", cartaSet);
                    data.put("fecha", Instant.now());
                    return repository.update(query, data, COLLECTION_MAZO_VIEW).then();
                });
    }

}
