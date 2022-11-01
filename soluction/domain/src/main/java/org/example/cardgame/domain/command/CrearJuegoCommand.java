package org.example.cardgame.domain.command;

import org.example.cardgame.generic.Command;


public class CrearJuegoCommand extends Command {
    private String juegoId;
    private String jugadorPrincipalId;
    private Integer montoRequerido;

    public void setMontoRequerido(Integer montoRequerido) {
        this.montoRequerido = montoRequerido;
    }

    public Integer getMontoRequerido() {
        return montoRequerido;
    }

    public String getJugadorPrincipalId() {
        return jugadorPrincipalId;
    }

    public void setJugadorPrincipalId(String jugadorPrincipalId) {
        this.jugadorPrincipalId = jugadorPrincipalId;
    }

    public String getJuegoId() {
        return juegoId;
    }

    public void setJuegoId(String juegoId) {
        this.juegoId = juegoId;
    }
}
