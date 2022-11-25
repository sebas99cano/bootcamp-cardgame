package org.example.usecase.usecase;


import org.example.cardgame.domain.events.*;
import org.example.cardgame.domain.values.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class StoreEvent_JuegoIniciado implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        var mazo1 = new Mazo(Set.of(
                StoreEvent_JuegoIniciado.cartaMayor(),
                StoreEvent_JuegoIniciado.cartaMenor()
        ));
        var mazo2 = new Mazo(Set.of(
                StoreEvent_JuegoIniciado.cartaMayor(),
                StoreEvent_JuegoIniciado.cartaMenor()
        ));
        return Stream.of(
                Arguments.of(List.of(
                        new JuegoCreado(JugadorId.of("1"), new MontoRequerido(3000)),
                        new JugadorAgregado(JugadorId.of("1"), new Alias("Raul")),
                        new JugadorAgregado(JugadorId.of("2"), new Alias("Pedro")),
                        new MazoAsignadoAJugador(JugadorId.of("1"), mazo1),
                        new MazoAsignadoAJugador(JugadorId.of("2"), mazo2),
                        new TableroCreado(TableroId.of("1"), Set.of(JugadorId.of("1"), JugadorId.of("2"))),
                        new RondaCreada(new Ronda(1, Set.of(JugadorId.of("1"), JugadorId.of("2"))), new TiempoLimite(20)),
                        new RondaIniciada()
                ))
        );

    }

    public static Carta cartaMayor(){
        return  new Carta(CartaMaestraId.of("1"), 30, false, true);
    }

    public static Carta cartaMenor(){
        return  new Carta(CartaMaestraId.of("2"), 10, false, true);
    }




}