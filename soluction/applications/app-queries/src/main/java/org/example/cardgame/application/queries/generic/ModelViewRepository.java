package org.example.cardgame.application.queries.generic;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface ModelViewRepository {

    Mono<Void> create(Map<String, Object> data, String collection);
    Mono<Void> update(Map<String, Object> query, Map<String, Object> data, String collection);
    Mono<Void> delete(Map<String, Object> query, Map<String, Object> data, String collection);
    <T> Mono<T> get(Map<String, Object> query, Class<T> entityClass, String collection);
    <T> Flux<T> find(Map<String, Object>  query, Class<T> entityClass, String collection);
}