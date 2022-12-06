package org.example.cardgame.application.command.adapter.service;

import org.example.cardgame.application.command.ConfigProperties;
import org.example.cardgame.usecase.gateway.ListaDeCartaService;
import org.example.cardgame.usecase.gateway.model.CartaMaestra;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class DataConsultarCartaMaestraService implements ListaDeCartaService {
    private final WebClient client;

    public DataConsultarCartaMaestraService(WebClient.Builder builder, ConfigProperties configProperties) {
        this.client = builder.baseUrl(configProperties.getUriMaster()).build();
    }


    @Override
    public Flux<CartaMaestra> obtenerCartasPepsico() {
        return this.client.get().uri("/list").accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(CartaMaestra.class);
    }
}
