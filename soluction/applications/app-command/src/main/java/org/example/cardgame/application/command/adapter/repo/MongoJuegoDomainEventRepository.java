package org.example.cardgame.application.command.adapter.repo;

import org.example.cardgame.application.command.ApplicationConfig;
import org.example.cardgame.generic.EventStoreRepository;
import org.example.cardgame.generic.DomainEvent;
import org.example.cardgame.usecase.gateway.JuegoDomainEventRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class MongoJuegoDomainEventRepository implements JuegoDomainEventRepository {
    private final EventStoreRepository repository;

    public MongoJuegoDomainEventRepository(EventStoreRepository repository) {
        this.repository = repository;
    }


    @Override
    public Flux<DomainEvent> obtenerEventosPor(String id) {
        return repository.getEventsBy(ApplicationConfig.STORE_NAME, id);
    }
}
