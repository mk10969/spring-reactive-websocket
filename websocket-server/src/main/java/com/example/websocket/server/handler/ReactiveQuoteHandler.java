package com.example.websocket.server.handler;


import com.example.websocket.server.model.Quote;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReactiveQuoteHandler {

    private final UnicastProcessor<Quote> unicastProcessor;

    private final List<Quote> quotes;

    private final ObjectMapper objectMapper;


    @Bean
    RouterFunction<ServerResponse> routes() {
        return RouterFunctions
                .route(RequestPredicates.GET("/"), this::ping)
                .andRoute(RequestPredicates.GET("/quote"), this::findAll)
                .andRoute(RequestPredicates.POST("/quote"), this::put)
                .andRoute(RequestPredicates.GET("/grafana"), this::ping)
                .andRoute(RequestPredicates.POST("/grafana/search"), this::grafanaSearch)
                .andRoute(RequestPredicates.POST("/grafana/query"), this::grafanaQuery)
                ;
    }

    private Mono<ServerResponse> grafanaSearch(ServerRequest request) {
        List<String> list = new ArrayList<>();
        list.add("aaa");
        list.add("bbb");
        list.add("ccc");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(list))
                .doOnError(ex -> log.error("http error : ", ex))
                .onErrorResume(Exception.class, ex -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue("internal server error")));
    }

    private Mono<ServerResponse> grafanaQuery(ServerRequest request) {

        GrafanaQueryResponse res = new GrafanaQueryResponse();
        List<GrafanaQueryResponse> data = new ArrayList<>();

        List<List<Object>> row = new ArrayList<>();

        res.setType("table");

        GrafanaQueryResponse.Column column = new GrafanaQueryResponse.Column();
        column.setText("Time");
        column.setType("time");
        GrafanaQueryResponse.Column column1 = new GrafanaQueryResponse.Column();
        column1.setText("Country");
        column1.setType("string");
        GrafanaQueryResponse.Column column2 = new GrafanaQueryResponse.Column();
        column2.setText("Number");
        column2.setType("number");
        res.setColumns(Arrays.asList(column, column1, column2));

        row.add(Arrays.asList(1234567, "SE", 123));
        row.add(Arrays.asList(1234567, "DE", 231));
        row.add(Arrays.asList(1234567, "US", 321));
        res.setRows(row);

        data.add(res);
        System.out.println(data);

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(data))
                .doOnError(ex -> log.error("http error : ", ex))
                .onErrorResume(Exception.class, ex -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue("internal server error")));
    }

    @Data
    static class GrafanaQueryResponse {

        private String type;
        private List<Column> columns;
        private List<List<Object>> rows;

        @Data
        static class Column {
            private String text;
            private String type;
        }
    }


    private Mono<ServerResponse> ping(ServerRequest request) {
        return Mono.just("ping!")
                .flatMap(text -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(text)));
    }

    private Mono<ServerResponse> findAll(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromPublisher(Flux.fromIterable(quotes), Quote.class))
                .doOnError(ex -> log.error("http error : ", ex))
                .onErrorResume(Exception.class, ex -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue("internal server error")));
    }

    private Mono<ServerResponse> put(ServerRequest request) {
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
                        .body(BodyInserters.fromValue("internal server error")));
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

