package org.example.cardgame.usecase.usecase;

import org.example.cardgame.domain.command.CrearJuegoCommand;
import org.example.cardgame.domain.events.JuegoCreado;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;



@ExtendWith(MockitoExtension.class)
class CrearJuegoUseCaseTest {

    @InjectMocks
    private CrearJuegoUseCase useCase;
    @Test
    void crearJuego() {
        var command = new CrearJuegoCommand();
        command.setJuegoId("XXXX");
        command.setJugadorPrincipalId("FFFF");
        command.setMontoRequerido(100);

        StepVerifier.create(useCase.apply(Mono.just(command)))
                .expectNextMatches(domainEvent -> {
                    var event = (JuegoCreado) domainEvent;
                    return event.aggregateRootId().equals("XXXX") && event.getJugadorPrincipal().value().equals("FFFF");
                })
                .expectComplete()
                .verify();
    }

}