package com.example.websocket.server.handler;


import com.example.websocket.server.WebSocketProperties;
import com.example.websocket.server.model.Quote;
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
@Component("websocket")
@RequiredArgsConstructor
public class ReactiveWebSocketHandler implements WebSocketHandler {

    private final Flux<Quote> hotPublisher;

    private final List<Quote> quotes;

    private final WebSocketProperties webSocketProperties;

    private static final Random random = new Random();


    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(Flux.interval(Duration.ofMillis(webSocketProperties.getInterval()))
                .map(i -> getQuote())
                .mergeWith(hotPublisher)
                .map(Quote::toString)
                .doOnNext(this::log)
                .map(session::textMessage))
                .doOnSubscribe(i -> log.info("Subscribe On WebSocket SessionId: {}",
                        session.getId()))
                .doOnTerminate(() -> log.info("UnSubscribe On WebSocket SessionId: {}",
                        session.getId()))
                .doOnError(ex -> log.error("WebSocket Error: ", ex))
                ;
    }

    private Quote getQuote() {
        int x = random.nextInt(quotes.size());
        return quotes.get(x);
    }

    private void log(String message) {
        if (webSocketProperties.isMessageDebug()) {
            log.info("MESSAGE: {}", message);
        }
    }

}
