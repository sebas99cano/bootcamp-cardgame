package org.example.cardgame.domain.command;

import org.example.cardgame.generic.Command;


public class UniserAlJuegoCommand extends Command {
    private String juegoId;
    private String jugadorId;
    private String alias;

    public String getJuegoId() {
        return juegoId;
    }

    public void setJuegoId(String juegoId) {
        this.juegoId = juegoId;
    }

    public String getJugadorId() {
        return jugadorId;
    }

    public void setJugadorId(String jugadorId) {
        this.jugadorId = jugadorId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
