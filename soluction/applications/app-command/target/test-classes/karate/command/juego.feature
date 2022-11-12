Feature: Crear un Juego...

  Background:
    * url baseUrl
    * def command = '/juego/crear'
    * def query = '/juego/listar/'
    * def sleep = function(pause){ java.lang.Thread.sleep(pause*1000) }
    * def uuid = function(){ return java.util.UUID.randomUUID() + '' }

  Scenario: Crear un juego

    Given path command
    And request { "juegoId": uuid(), "jugadores": {"1": "raul", "2":"andres", "3": "pedro"}, "jugadorPrincipalId": "1" }
    And header Accept = 'application/json'
    When method post
    Then status 200

  Scenario: Obtener la lista de juegos
    * sleep(5)
    Given path query + uuid()
    When method GET
    Then status 200
    And match response == [{"id":uuid(),"iniciado":false,"finalizado":false,"uid":"1","cantidadJugadores":3,"jugadores":{"2":{"alias":"andres","jugadorId":"2"},"3":{"alias":"pedro","jugadorId":"3"},"1":{"alias":"raul","jugadorId":"1"}},"ganador":null}]