package org.example.cardgame.application.command.handle.usecase;


import co.com.sofka.domain.generic.DomainEvent;
import org.example.cardgame.application.command.handle.IntegrationHandle;
import org.example.cardgame.domain.events.RondaIniciada;
import org.example.cardgame.usecase.usecase.IniciarCuentaRegresivaUseCase;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;


@Configuration
public class CuentaRegresivaEventHandle implements BusinessService{

    private final IniciarCuentaRegresivaUseCase usecase;
    private final IntegrationHandle handle;


    public CuentaRegresivaEventHandle(IniciarCuentaRegresivaUseCase usecase, IntegrationHandle handle) {
        this.usecase = usecase;
        this.handle = handle;
    }


    @Override
    public Mono<Void> doProcessing(DomainEvent event) {
        return handle.apply(usecase.apply(Mono.just((RondaIniciada)event)));
    }
}