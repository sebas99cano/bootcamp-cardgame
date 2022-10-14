package org.example.cardgame.application.queries.adapter.repo;

import org.bson.Document;
import org.example.cardgame.application.queries.generic.ModelViewRepository;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class MongoModelViewRepository implements ModelViewRepository {
    protected final ReactiveMongoTemplate template;

    public MongoModelViewRepository(ReactiveMongoTemplate template) {
        this.template = template;
    }

    @Override
    public Mono<Void> create(Map<String, Object> data, String collection) {
        return template.save(data, collection).retry(3).then();
    }

    @Override
    public Mono<Void> update(Map<String, Object> query, Map<String, Object> data, String collection) {
        var updated = new Update();
        data.forEach(updated::set);

        return template.updateMulti(
                new BasicQuery(new Document(query)),
                updated,
                collection
        ).then();
    }

    @Override
    public Mono<Void> delete(Map<String, Object> query, Map<String, Object> data, String collection) {
        return null;
    }

    @Override
    public <T> Mono<T> get(Map<String, Object> query, Class<T> entityClass, String collection) {
        return template.findOne(
                new BasicQuery(new Document(query)),
                entityClass,
                collection
        );
    }

    @Override
    public <T> Flux<T> find(Map<String, Object> query, Class<T> entityClass, String collection) {
        return template.find(
                new BasicQuery(new Document(query)),
                entityClass,
                collection
        );
    }
}
