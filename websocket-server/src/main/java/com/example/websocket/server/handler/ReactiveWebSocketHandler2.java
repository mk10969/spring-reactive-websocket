package com.example.websocket.server.handler;


import com.example.websocket.server.WebSocketProperties;
import com.example.websocket.server.model.Quote;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;

@Slf4j
@Component("websocket2")
@RequiredArgsConstructor
public class ReactiveWebSocketHandler2 implements WebSocketHandler {

    private final WebSocketProperties webSocketProperties;

    private final ObjectMapper objectMapper;


    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(quoteFlux()
                .doOnNext(this::log)
                .map(session::textMessage))
                .doOnSubscribe(i -> log.info("Subscribe On WebSocket SessionId: {}",
                        session.getId()))
                .doOnTerminate(() -> log.info("UnSubscribe On WebSocket SessionId: {}",
                        session.getId()))
                .doOnError(ex -> log.error("WebSocket Error: ", ex));
    }

    private Flux<String> quoteFlux() {
        return webSocketProperties.getQuotes().stream()
                .map(quate -> Flux.interval(Duration.ofMillis(webSocketProperties.getInterval()))
                        .map(i -> quate))
                .reduce(Flux::mergeWith)
                .orElseThrow();
    }

    private String toPrettyJson(Quote quote) {
        try {
            return objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(quote);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }


    private void log(Object message) {
        if (webSocketProperties.isMessageDebug()) {
            log.info("MESSAGE: {}", message);
        }
    }

}
