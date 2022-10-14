package org.example.cardgame.usecase.usecase;

import co.com.sofka.domain.generic.DomainEvent;
import org.example.cardgame.domain.events.JuegoCreado;
import org.example.cardgame.domain.events.JuegoFinalizado;
import org.example.cardgame.domain.events.JugadorAgregado;
import org.example.cardgame.domain.events.RondaTerminada;
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

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeterminarGanadorUseCaseTest {

    @InjectMocks
    private DeterminarGanadorUseCase useCase;

    @Mock
    private JuegoDomainEventRepository repository;

    @Test
    void determinarGanador(){
        var event = new RondaTerminada(TableroId.of("TABLERO"), Set.of(JugadorId.of("1111")));
        event.setAggregateRootId("XXXX");

        when(repository.obtenerEventosPor("XXXX"))
                .thenReturn(history());

        StepVerifier
                .create(useCase.apply(Mono.just(event)))
                .expectNextMatches(domainEvent -> {
                    var event2 = (JuegoFinalizado) domainEvent;
                    return event2.getJugadorId().equals(JugadorId.of("1111"))
                            && event2.getAlias().equals("Gianni");
                })
                .expectComplete()
                .verify();
    }

    private Flux<DomainEvent> history() {
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

        return Flux.just(event, event2);
    }
}