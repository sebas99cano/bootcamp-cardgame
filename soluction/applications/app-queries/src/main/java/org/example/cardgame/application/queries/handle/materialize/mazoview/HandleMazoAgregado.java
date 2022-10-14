package org.example.cardgame.application.queries.handle.materialize.mazoview;

import co.com.sofka.domain.generic.DomainEvent;
import org.bson.Document;
import org.example.cardgame.application.queries.generic.ModelViewRepository;
import org.example.cardgame.application.queries.handle.materialize.MaterializeService;
import org.example.cardgame.domain.events.JugadorAgregado;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;

@Component
public class HandleMazoAgregado  implements MaterializeService {
    private final ModelViewRepository repository;

    public HandleMazoAgregado(ModelViewRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Void> doProcessing(DomainEvent input) {
        var event = (JugadorAgregado)input;
        var mazo = event.getMazo().value();
        var data = new Document();
        var cartas = new ArrayList<>();
        data.put("uid", event.getJugadorId().value());
        data.put("juegoId", event.aggregateRootId());
        data.put("cantidad", mazo.cartas().size());
        data.put("fecha", Instant.now());

        mazo.cartas().forEach(carta -> {
            var documentCarta = new Document();
            documentCarta.put("poder", carta.value().poder());
            documentCarta.put("cartaId", carta.value().cartaId().value());
            documentCarta.put("estaHabilitada", carta.value().estaHabilitada());
            documentCarta.put("estaOculta", carta.value().estaOculta());
            cartas.add(documentCarta);
        });
        data.put("cartas", cartas);

        return repository.create(data, COLLECTION_MAZO_VIEW).then();
    }
}
