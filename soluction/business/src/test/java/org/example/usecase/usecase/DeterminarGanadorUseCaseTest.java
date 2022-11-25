package org.example.usecase.usecase;


import org.example.cardgame.domain.events.CartaQuitadaDelMazo;
import org.example.cardgame.domain.events.RondaTerminada;
import org.example.cardgame.domain.values.JugadorId;
import org.example.cardgame.domain.values.TableroId;
import org.example.cardgame.generic.DomainEvent;
import org.example.cardgame.usecase.gateway.JuegoDomainEventRepository;
import org.example.cardgame.usecase.usecase.DeterminarGanadorUseCase;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class DeterminarGanadorUseCaseTest {

    @InjectMocks
    DeterminarGanadorUseCase usecase;

    @Mock
    JuegoDomainEventRepository repository;

    @ParameterizedTest
    @ArgumentsSource(StoreEvent_JuegoIniciado.class)
    public void determinarGanador(List<DomainEvent> list){
        var event = new RondaTerminada(TableroId.of("1"), Set.of(JugadorId.of("1"), JugadorId.of("2")));
        event.setAggregateRootId("1");

        var eventStored = new ArrayList<>(list);
        eventStored.add(new CartaQuitadaDelMazo(JugadorId.of("1"), StoreEvent_JuegoIniciado.cartaMenor()));
        eventStored.add(new CartaQuitadaDelMazo(JugadorId.of("1"), StoreEvent_JuegoIniciado.cartaMayor()));

        when(repository.obtenerEventosPor(any())).thenReturn(Flux.fromIterable(eventStored));

        usecase.apply(Mono.just(event)).subscribe(new Consumer<DomainEvent>() {
            @Override
            public void accept(DomainEvent domainEvent) {
                System.out.println(domainEvent);
            }
        });
    }
}