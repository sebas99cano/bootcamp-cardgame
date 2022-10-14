package org.example.cardgame.application.queries.handle.materialize.mazoview;

import co.com.sofka.domain.generic.DomainEvent;
import org.example.cardgame.application.queries.generic.ModelViewRepository;
import org.example.cardgame.application.queries.handle.materialize.MaterializeService;
import org.example.cardgame.application.queries.handle.model.MazoViewModel;
import org.example.cardgame.domain.events.CartasAsignadasAJugador;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class HandleCartasAsignadasAJugador  implements MaterializeService {
    private final ModelViewRepository repository;

    public HandleCartasAsignadasAJugador(ModelViewRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Void> doProcessing(DomainEvent input) {
        var event = (CartasAsignadasAJugador)input;
        Map<String, Object> query = new HashMap<>();
        Map<String, Object> data = new HashMap<>();

        query.put("juegoId", event.aggregateRootId());
        query.put("uid", event.getGanadorId().value());


        var mazo = event.getCartasApuesta().stream().map(carta -> {
            var c = new MazoViewModel.Carta();
            c.setCartaId(carta.value().cartaId().value());
            c.setEstaHabilitada(carta.value().estaHabilitada());
            c.setEstaOculta(carta.value().estaOculta());
            c.setPoder(carta.value().poder());
            return c;
        }).collect(Collectors.toSet());

        return repository.get(query, MazoViewModel.class, COLLECTION_MAZO_VIEW).flatMap( model -> {
            var cartaSet =  model.getCartas();
            cartaSet.addAll(mazo);
            data.put("cartas", cartaSet);
            data.put("fecha", Instant.now());
            return repository.update(query, data, COLLECTION_MAZO_VIEW).then();
        });

    }
}
