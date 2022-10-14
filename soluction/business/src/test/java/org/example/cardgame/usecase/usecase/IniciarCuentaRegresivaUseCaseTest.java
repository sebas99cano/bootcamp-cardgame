package org.example.cardgame.usecase.usecase;

import co.com.sofka.domain.generic.DomainEvent;
import org.example.cardgame.domain.events.*;
import org.example.cardgame.domain.values.*;
import org.example.cardgame.usecase.gateway.JuegoDomainEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IniciarCuentaRegresivaUseCaseTest {
    @InjectMocks
    private IniciarCuentaRegresivaUseCase useCase;

    @Mock
    private JuegoDomainEventRepository repository;

    @Mock
    private FinalizarRondaUseCase useCase2;

    @Test
    void iniciarCuentaRegresiva(){

        var mainevent = new RondaIniciada();
        mainevent.setAggregateRootId("XXXX");

        when(repository.obtenerEventosPor("XXXX"))
                .thenReturn(history());

//        var command = new FinalizarRondaCommand();
//        command.setJuegoId("XXXX");
//
//        when(useCase2.apply(Mono.just(command)))
//                .thenReturn(historico2());

        when(useCase2.apply(any())).thenReturn(history2());

        StepVerifier
                .create(useCase.apply(Mono.just(mainevent)))
                .expectNextMatches(domainEvent -> {
                    var event = (TiempoCambiadoDelTablero) domainEvent;
                    assert event.aggregateRootId().equals("XXXX");
                    return event.getTableroId().equals(TableroId.of("TABLERO"));
                })
                .expectNextMatches(domainEvent -> {
                    var event = (TiempoCambiadoDelTablero) domainEvent;
                    assert event.aggregateRootId().equals("XXXX");
                    return event.getTableroId().equals(TableroId.of("TABLERO"));
                })
                .expectNextMatches(domainEvent -> {
                    var event = (JuegoCreado) domainEvent;
                    assert event.aggregateRootId().equals("XXXX");
                    return event.getJugadorPrincipal().value().equals("1111");
                })
                .expectNextMatches(domainEvent -> {
                    var event = (JugadorAgregado) domainEvent;
                    assert event.aggregateRootId().equals("XXXX");
                    return  event.getJugadorId().value().equals("1111");
                })
                .expectNextMatches(domainEvent -> {
                    var event = (JugadorAgregado) domainEvent;
                    assert event.aggregateRootId().equals("XXXX");
                    return  event.getJugadorId().value().equals("2222");
                })
                .expectNextMatches(domainEvent -> {
                    var event = (TableroCreado) domainEvent;
                    assert event.aggregateRootId().equals("XXXX");
                    return  event.getTableroId().value().equals("TABLERO");
                })
                .expectNextMatches(domainEvent -> {
                    var event = (RondaCreada) domainEvent;
                    return  event.aggregateRootId().equals("XXXX");
                })
                .expectNextMatches(domainEvent -> {
                    var event = (RondaIniciada) domainEvent;
                    return event.aggregateRootId().equals("XXXX");
                })
                .expectNextMatches(domainEvent -> {
                    var event = (CartaPuestaEnTablero) domainEvent;
                    assert event.aggregateRootId().equals("XXXX") && event.getJugadorId().value().equals("1111");
                    return  event.getCarta().value().cartaId().value().equals("car6");
                })
                .expectNextMatches(domainEvent -> {
                    var event = (CartaQuitadaDelMazo) domainEvent;
                    assert event.aggregateRootId().equals("XXXX") && event.getJugadorId().value().equals("1111");
                    return  event.getCarta().value().cartaId().value().equals("car6");
                })
                .expectNextMatches(domainEvent -> {
                    var event = (CartaPuestaEnTablero) domainEvent;
                    assert event.aggregateRootId().equals("XXXX") && event.getJugadorId().value().equals("2222");
                    return  event.getCarta().value().cartaId().value().equals("car10");
                })
                .expectNextMatches(domainEvent -> {
                    var event = (CartaQuitadaDelMazo) domainEvent;
                    assert event.aggregateRootId().equals("XXXX") && event.getJugadorId().value().equals("2222");
                    return  event.getCarta().value().cartaId().value().equals("car10");
                })
                .expectNextMatches(domainEvent -> {
                    var event = (TiempoCambiadoDelTablero) domainEvent;
                    assert event.aggregateRootId().equals("XXXX");
                    return event.getTableroId().equals(TableroId.of("TABLERO"));
                })
                .expectComplete()
                .verify();

    }

    private Flux<DomainEvent> history(){
        //CREAR JUEGO
        var event = new JuegoCreado(JugadorId.of("1111"));
        event.setAggregateRootId("XXXX");

        //CREAR TABLERO
        var event2 = new TableroCreado(TableroId.of("TABLERO"),
                Set.of(
                        JugadorId.of("1111")
                )
        );
        event2.setAggregateRootId("XXXX");

        //CREAR RONDA
        var event3 = new RondaCreada(
                new Ronda(1,
                        Set.of(JugadorId.of("1111"),
                                JugadorId.of("2222")
                        )
                ), 3);
        event3.setAggregateRootId("XXXX");

        return Flux.just(event, event2, event3);
    }

    private Flux<DomainEvent> history2() {
        var event = new JuegoCreado(JugadorId.of("1111"));
        event.setAggregateRootId("XXXX");

        var event2 = new JugadorAgregado(
                JugadorId.of("1111"), "Gianni",
                new Mazo(Set.of(
                        new Carta(CartaMaestraId.of("car1"), 10, true, true),
                        new Carta(CartaMaestraId.of("car2"), 11, true, true),
                        new Carta(CartaMaestraId.of("car3"), 12, true, true),
                        new Carta(CartaMaestraId.of("car4"), 13, true, true),
                        new Carta(CartaMaestraId.of("car5"), 14, true, true),
                        new Carta(CartaMaestraId.of("car6"), 15, true, true)
                )));
        event2.setAggregateRootId("XXXX");

        var event3 = new JugadorAgregado(
                JugadorId.of("2222"), "Cris",
                new Mazo(Set.of(
                        new Carta(CartaMaestraId.of("car7"), 1, true, true),
                        new Carta(CartaMaestraId.of("car8"), 2, true, true),
                        new Carta(CartaMaestraId.of("car9"), 3, true, true),
                        new Carta(CartaMaestraId.of("car10"), 4, true, true),
                        new Carta(CartaMaestraId.of("car11"), 5, true, true),
                        new Carta(CartaMaestraId.of("car12"), 6, true, true)
                )));
        event3.setAggregateRootId("XXXX");

        var event4 = new TableroCreado(TableroId.of("TABLERO"),
                Set.of(
                        JugadorId.of("1111"),
                        JugadorId.of("2222")
                )
        );
        event4.setAggregateRootId("XXXX");

        var event5 = new RondaCreada(
                new Ronda(1,
                        Set.of(JugadorId.of("1111"),
                                JugadorId.of("2222")
                        )
                ), 3);
        event5.setAggregateRootId("XXXX");

        var event6 = new RondaIniciada();
        event6.setAggregateRootId("XXXX");

        //JUGADOR 1//
        var event7 = new CartaPuestaEnTablero(event4.getTableroId(), event2.getJugadorId(), new Carta(CartaMaestraId.of("car6"), 15, true, true));
        event7.setAggregateRootId("XXXX");

        var event8 = new CartaQuitadaDelMazo(event2.getJugadorId(), new Carta(CartaMaestraId.of("car6"), 15, true, true));
        event8.setAggregateRootId("XXXX");

        //JUGADOR 2//
        var event9 = new CartaPuestaEnTablero(event4.getTableroId(), event3.getJugadorId(), new Carta(CartaMaestraId.of("car10"), 4, true, true));
        event9.setAggregateRootId("XXXX");

        var event10 = new CartaQuitadaDelMazo(event3.getJugadorId(), new Carta(CartaMaestraId.of("car10"), 4, true, true));
        event10.setAggregateRootId("XXXX");

        return Flux.just(event, event2, event3, event4, event5, event6, event7, event8, event9, event10);
    }
}