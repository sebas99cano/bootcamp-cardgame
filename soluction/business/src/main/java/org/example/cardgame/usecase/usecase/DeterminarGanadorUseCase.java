package org.example.cardgame.usecase.usecase;

import co.com.sofka.domain.generic.DomainEvent;

import co.com.sofka.domain.generic.Identity;
import org.example.cardgame.domain.Juego;
import org.example.cardgame.domain.command.CrearRondaCommand;
import org.example.cardgame.domain.events.RondaTerminada;
import org.example.cardgame.domain.values.JuegoId;
import org.example.cardgame.usecase.gateway.JuegoDomainEventRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

public class DeterminarGanadorUseCase  extends UseCaseForEvent<RondaTerminada> {
    private final JuegoDomainEventRepository repository;
    private final CrearRondaUseCase crearRondaUseCase;

    public DeterminarGanadorUseCase(JuegoDomainEventRepository repository, CrearRondaUseCase crearRondaUseCase){
        this.repository = repository;
        this.crearRondaUseCase = crearRondaUseCase;
    }


    @Override
    public Flux<DomainEvent> apply(Mono<RondaTerminada> rondaTerminadaMono) {
        return rondaTerminadaMono.flatMapMany((event) -> repository
                .obtenerEventosPor(event.aggregateRootId())
                .collectList()
                .flatMapMany(events -> {
                    var juego = Juego.from(JuegoId.of(event.aggregateRootId()), events);
                    var jugadores = juego.jugadores().values().stream()
                            .filter(jugador -> jugador.mazo().value().cantidad() > 0)
                            .collect(Collectors.toList());
                    var competidores = event.getJugadorIds();

                    if(jugadores.size()  == 1){
                        var jugador = jugadores.get(0);
                        juego.finalizarJuego(jugador.identity(), jugador.alias());
                    } else if (competidores.size() == 1) {
                        competidores.stream().findFirst().ifPresent(jugadorId -> {
                            var jugador = juego.jugadores().get(jugadorId);
                            juego.finalizarJuego(jugador.identity(), jugador.alias());
                        });
                    } else {
                        var command = new CrearRondaCommand();
                        var jugadoresIds = jugadores.stream()
                                .map(a -> a.identity().value())
                                .collect(Collectors.toSet());
                        command.setJuegoId(event.aggregateRootId());
                        command.setTiempo(20);
                        command.setJugadores(jugadoresIds);
                        return crearRondaUseCase.apply(Mono.just(command))
                                .mergeWith(Flux.fromIterable(juego.getUncommittedChanges()));
                    }
                    return Flux.fromIterable(juego.getUncommittedChanges());
                }));
    }
}