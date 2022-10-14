package org.example.cardgame.application.queries.handle.materialize;

import org.example.cardgame.application.queries.handle.materialize.gameview.*;
import org.example.cardgame.application.queries.handle.materialize.mazoview.HandleCartaQuitadaDelMazo;
import org.example.cardgame.application.queries.handle.materialize.mazoview.HandleCartasAsignadasAJugador;
import org.example.cardgame.application.queries.handle.materialize.mazoview.HandleMazoAgregado;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

@Component
public class MaterializeLookUp {
    private final Map<String, Flux<MaterializeService>> business = new HashMap<>();


    public MaterializeLookUp(ApplicationContext context){
        business.put("cardgame.cartapuestaentablero", Flux.just(
                context.getBean(HandleCartaPuestaEnTablero.class)
        ));
        business.put("cardgame.juegocreado", Flux.just(
                context.getBean(HandleJuegoCreado.class)
        ));
        business.put("cardgame.juegofinalizado", Flux.just(
                context.getBean(HandleJuegoFinalizado.class)
        ));
        business.put("cardgame.jugadoragregado", Flux.just(
                context.getBean(HandleJugadorAgregado.class),
                context.getBean(HandleMazoAgregado.class)
        ));
        business.put("cardgame.rondacreada", Flux.just(
                context.getBean(HandleRondaCreada.class)
        ));
        business.put("cardgame.rondainiciada", Flux.just(
                context.getBean(HandleRondaIniciada.class)
        ));
        business.put("cardgame.rondaTterminada", Flux.just(
                context.getBean(HandleRondaTerminada.class)
        ));
        business.put("cardgame.tablerocreado", Flux.just(
                context.getBean(HandleTableroCreado.class)
        ));
        business.put("cardgame.tiempocambiadodeltablero", Flux.just(
                context.getBean(HandleTiempoCambiadoDelTablero.class)
        ));
        business.put("cardgame.cartaquitadadelmazo", Flux.just(
                context.getBean(HandleCartaQuitadaDelMazo.class)
        ));
        business.put("cardgame.cartasasignadasajugador", Flux.just(
                context.getBean(HandleCartasAsignadasAJugador.class)
        ));
    }


    public Flux<MaterializeService> get(String eventType) {
        return business.getOrDefault(eventType, Flux.empty());
    }
}
