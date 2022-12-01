package org.example.cardgame.application.gateway;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;


@Component
public class JWTFilter extends AbstractNameValueGatewayFilterFactory {
    private static final String WWW_AUTH_HEADER = "WWW-Authenticate";


    private static final Logger logger = LoggerFactory.getLogger(JWTFilter.class);


    @Override
    public GatewayFilter apply(NameValueConfig config) {
        return (exchange, chain) -> {

            try {
                String token = this.extractJWTToken(exchange.getRequest());

                ServerHttpRequest request = exchange.getRequest().mutate().
                        build();

                //TODO: valida token

                logger.info(token);
                return chain.filter(exchange.mutate().request(request).build());

            } catch (JWTVerificationException ex) {

                logger.error(ex.toString());
                return this.onError(exchange, ex.getMessage());
            }
        };
    }

    private String extractJWTToken(ServerHttpRequest request)
    {
        if (!request.getHeaders().containsKey("Authorization")) {
            throw new JWTVerificationException("Authorization header is missing");
        }

        List<String> headers = request.getHeaders().get("Authorization");
        if (headers.isEmpty()) {
            throw new JWTVerificationException("Authorization header is empty");
        }

        String credential = headers.get(0).trim();
        String[] components = credential.split("\\s");

        if (components.length != 2) {
            throw new JWTVerificationException("Malformat Authorization content");
        }

        if (!components[0].equals("Bearer")) {
            throw new JWTVerificationException("Bearer is needed");
        }

        return components[1].trim();
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(WWW_AUTH_HEADER, err);
        return response.setComplete();
    }

}