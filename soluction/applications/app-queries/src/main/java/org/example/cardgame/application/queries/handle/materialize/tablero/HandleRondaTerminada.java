package org.example.cardgame.application.queries.handle.materialize.tablero;

import org.example.cardgame.application.queries.adapter.repo.TableroViewModelRepository;
import org.example.cardgame.application.queries.handle.materialize.MaterializeService;
import org.example.cardgame.domain.events.RondaTerminada;
import org.example.cardgame.generic.DomainEvent;
import org.example.cardgame.generic.Identity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


import java.util.HashMap;
import java.util.stream.Collectors;

@Component
public class HandleRondaTerminada  implements MaterializeService {
    private final TableroViewModelRepository repository;

    public HandleRondaTerminada(TableroViewModelRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Void> doProcessing(DomainEvent input) {
        var event = (RondaTerminada)input;
        System.out.println(event);
        return repository.findById(event.aggregateRootId()).flatMap(tablero -> {
            var jugadores = event.getJugadorIds().stream()
                    .map(Identity::value)
                    .collect(Collectors.toSet());
            tablero.setTiempo(0);
            var rondaModel = tablero.getRonda();
            var tableroModel = tablero.getTablero();

            rondaModel.setJugadores(jugadores);
            rondaModel.setEstaIniciada(false);
            tableroModel.setCartas(new HashMap<>());
            tableroModel.setHabilitado(false);

            tablero.setTablero(tableroModel);
            tablero.setRonda(rondaModel);

            return repository.save(tablero);
        }).then();
    }
}
