package com.example.reactive.websocket.handler;


import com.example.reactive.websocket.model.Quote;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.UnicastProcessor;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReactiveQuoteHandler {

    private final UnicastProcessor<Quote> unicastProcessor;

    private final List<Quote> quotes;

    private final ObjectMapper objectMapper;


    @Bean
    RouterFunction<ServerResponse> routes(ReactiveQuoteHandler reactiveQuoteHandler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/"), reactiveQuoteHandler::ping)
                .andRoute(RequestPredicates.GET("/quote"), reactiveQuoteHandler::findAll)
                .andRoute(RequestPredicates.POST("/quote"), reactiveQuoteHandler::register)
                ;
    }

    private Mono<ServerResponse> ping(ServerRequest request) {
        return Mono.just("ping!")
                .flatMap(text -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(text)))
                ;
    }

    private Mono<ServerResponse> findAll(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Flux.fromIterable(quotes), Quote.class))
                .doOnError(ex -> log.error("http error : ", ex))
                .onErrorResume(Exception.class, ex -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue("internal server error")))
                ;
    }

    private Mono<ServerResponse> register(ServerRequest request) {
        return request.bodyToMono(String.class)
                .map(this::mapper)
                .doOnNext(unicastProcessor::onNext)
                .doOnNext(ReactiveQuoteHandler::log)
                .then()
                .flatMap(empty -> ServerResponse.accepted()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue("queueにデータを追加しました！")))
                .doOnError(ex -> log.error("http error : ", ex))
                .onErrorResume(IllegalArgumentException.class, ex -> ServerResponse.badRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue("データフォーマットが違います。")))
                .onErrorResume(Exception.class, ex -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue("internal server error")))
                ;
    }

    private Quote mapper(String data) {
        try {
            return objectMapper.readValue(data, Quote.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static void log(Quote quote) {
        log.info("TEST: {}", quote);
    }

}

