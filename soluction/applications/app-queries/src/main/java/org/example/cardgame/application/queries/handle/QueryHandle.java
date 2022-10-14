package org.example.cardgame.application.queries.handle;



import org.example.cardgame.application.queries.generic.ModelViewRepository;
import org.example.cardgame.application.queries.handle.model.JuegoListViewModel;
import org.example.cardgame.application.queries.handle.model.MazoViewModel;
import org.example.cardgame.application.queries.handle.model.TableroViewModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class QueryHandle {

    private final ModelViewRepository repository;

    public QueryHandle(ModelViewRepository repository) {
        this.repository = repository;
    }

    @Bean
    public RouterFunction<ServerResponse> listarJuego() {
        return route(
                GET("/juego/listar/{uid}"),
                request -> repository.find(Map.of("uid", request.pathVariable("uid")), JuegoListViewModel.class, "gameview")
                        .collectList()
                        .flatMap(list -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromPublisher(Flux.fromIterable(list), JuegoListViewModel.class)))
        );
    }


    @Bean
    public RouterFunction<ServerResponse> getJuego() {
        return route(
                GET("/juego/{id}"),
                request -> repository.get(Map.of("_id", request.pathVariable("id")), TableroViewModel.class, "gameview")
                        .flatMap(element -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromPublisher(Mono.just(element), TableroViewModel.class)))
        );
    }

    @Bean
    public RouterFunction<ServerResponse> getMazo() {
        return route(
                GET("/juego/mazo/{juegoId}/{uid}"),
                request -> repository.get(Map.of("juegoId", request.pathVariable("juegoId"), "uid", request.pathVariable("uid")), MazoViewModel.class, "mazoview")
                        .flatMap(element -> ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromPublisher(Mono.just(element), MazoViewModel.class)))
        );
    }

}
