package org.example.cardgame.application.command;



import org.junit.jupiter.api.*;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;

import java.util.Map;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureWireMock(port = 8085)
public class WorkFlowIntegrationTest extends CommandBaseIntegrationTest{

    @Test
    @Order(1)
    void crearJuego() {

        executor(Map.of(
                "juegoId", "1",
                "jugadorPrincipalId", "1",
                "alias", "Andres",
                "montoRequerido", 30000
        ), "crearjuegocommand", "/juego/crear", requestFields(
                fieldWithPath("juegoId").description("ID del Juego"),
                fieldWithPath("jugadorPrincipalId").description("ID del Jugador Principal"),
                fieldWithPath("alias").description("El Alias del Jugador Principal"),
                fieldWithPath("montoRequerido").description("El monto requerido")
        ), 2);
    }


    @Test
    @Order(2)
    void unirseJuego() {
        executor(Map.of(
                "juegoId", "1",
                "jugadorId", "2",
                "alias", "Pedro"
        ), "unirsealjuegocommand","/juego/unirse", requestFields(
                fieldWithPath("juegoId").description("ID del Juego"),
                fieldWithPath("jugadorId").description("ID del Jugador Principal"),
                fieldWithPath("alias").description("El Alias del Jugador Principal")

        ), 1);
    }


    @Test
    @Order(3)
    void iniciarJuego() {

        stubFor(get(urlEqualTo("/list"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("master.json")));

        executor(Map.of(
                "juegoId", "1"
        ), "iniciarjuegocommand","/juego/iniciar", requestFields(
                fieldWithPath("juegoId").description("ID del Juego")
        ), 2);
    }


}