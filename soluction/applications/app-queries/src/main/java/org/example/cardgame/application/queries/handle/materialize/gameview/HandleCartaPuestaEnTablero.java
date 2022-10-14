package org.example.cardgame.application.queries.handle.materialize.gameview;

import co.com.sofka.domain.generic.DomainEvent;
import org.example.cardgame.application.queries.generic.ModelViewRepository;
import org.example.cardgame.application.queries.handle.materialize.MaterializeService;
import org.example.cardgame.domain.events.CartaPuestaEnTablero;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class HandleCartaPuestaEnTablero implements MaterializeService {
    private final ModelViewRepository repository;

    public HandleCartaPuestaEnTablero(ModelViewRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Void> doProcessing(DomainEvent input) {
        var event = (CartaPuestaEnTablero)input;
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> query = new HashMap<>();
        Map<String, Object>  document = new HashMap<>();

        var carta = event.getCarta().value();
        var jugadorId = event.getJugadorId().value();
        document.put("cartaId", carta.cartaId().value());
        document.put("estaOculta", carta.estaOculta());
        document.put("poder", carta.poder());
        document.put("estaHabilitada", carta.estaHabilitada());
        document.put("jugadorId", jugadorId);

        data.put("fecha", Instant.now());
        data.put("tablero.cartas."+jugadorId+"."+carta.cartaId().value(), document);

        return repository.update(query, data, COLLECTION_GAME_VIEW);
    }
}
