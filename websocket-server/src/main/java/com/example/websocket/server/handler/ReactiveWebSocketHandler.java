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
@Component("websocket")
@RequiredArgsConstructor
public class ReactiveWebSocketHandler implements WebSocketHandler {

    private final Flux<Quote> hotPublisher;

    private final List<Quote> quotes;

    private final WebSocketProperties webSocketProperties;

    private final ObjectMapper objectMapper;

    private static final Random random = new Random();


    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(quoteFlux1()
                .mergeWith(quoteFluxY2())
                .mergeWith(quoteFluxY3())
                .mergeWith(quoteFluxY4())
                .mergeWith(hotPublisher)
                .doOnNext(this::log)
                .map(this::toPrettyJson)
                .map(session::textMessage))
                .doOnSubscribe(i -> log.info("Subscribe On WebSocket SessionId: {}",
                        session.getId()))
                .doOnTerminate(() -> log.info("UnSubscribe On WebSocket SessionId: {}",
                        session.getId()))
                .doOnError(ex -> log.error("WebSocket Error: ", ex));
    }


    private Flux<Quote> quoteFluxZZZ() {
        return Flux.fromIterable(quotes)
                .delayElements(Duration.ofMillis(webSocketProperties.getInterval()))
                .repeat();
    }


    private Flux<Quote> quoteFluxY1() {
        return Mono.fromCallable(() -> System.currentTimeMillis())
                .repeat()
                .map(i -> quotes.get(0))
                .delayElements(Duration.ofMillis(webSocketProperties.getInterval()));
    }

    private Flux<Quote> quoteFluxY2() {
        return Mono.fromCallable(() -> System.currentTimeMillis())
                .repeat()
                .map(i -> quotes.get(1))
                .delayElements(Duration.ofMillis(webSocketProperties.getInterval()));
    }

    private Flux<Quote> quoteFluxY3() {
        return Mono.fromCallable(() -> System.currentTimeMillis())
                .repeat()
                .map(i -> quotes.get(2))
                .delayElements(Duration.ofMillis(webSocketProperties.getInterval()));
    }

    private Flux<Quote> quoteFluxY4() {
        return Mono.fromCallable(() -> System.currentTimeMillis())
                .repeat()
                .map(i -> quotes.get(3))
                .delayElements(Duration.ofMillis(webSocketProperties.getInterval()));
    }


    private Flux<Quote> quoteFluxX1() {
        return Flux.<Quote>create(fluxSink -> {
            while (!fluxSink.isCancelled()) {
                fluxSink.next(quotes.get(0));
            }
        }).delayElements(Duration.ofMillis(webSocketProperties.getInterval())).share();
    }

    private Flux<Quote> quoteFluxX2() {
        return Flux.<Quote>create(fluxSink -> {
            while (!fluxSink.isCancelled()) {
                fluxSink.next(quotes.get(1));
            }
        }).delayElements(Duration.ofMillis(webSocketProperties.getInterval())).share();
    }

    private Flux<Quote> quoteFluxX3() {
        return Flux.<Quote>create(fluxSink -> {
            while (!fluxSink.isCancelled()) {
                fluxSink.next(quotes.get(2));
            }
        }).delayElements(Duration.ofMillis(webSocketProperties.getInterval())).share();
    }

    private Flux<Quote> quoteFluxX4() {
        return Flux.<Quote>create(fluxSink -> {
            while (!fluxSink.isCancelled()) {
                fluxSink.next(quotes.get(3));
            }
        }).delayElements(Duration.ofMillis(webSocketProperties.getInterval())).share();
    }


    private Flux<Quote> quoteFlux1() {
        return Flux.interval(Duration.ofMillis(webSocketProperties.getInterval()))
                .map(i -> quotes.get(0));
    }

    private Flux<Quote> quoteFlux2() {
        return Flux.interval(Duration.ofMillis(webSocketProperties.getInterval()))
                .map(i -> quotes.get(1));
    }

    private Flux<Quote> quoteFlux3() {
        return Flux.interval(Duration.ofMillis(webSocketProperties.getInterval()))
                .map(i -> quotes.get(2));
    }

    private Flux<Quote> quoteFlux4() {
        return Flux.interval(Duration.ofMillis(webSocketProperties.getInterval()))
                .map(i -> quotes.get(3));
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
