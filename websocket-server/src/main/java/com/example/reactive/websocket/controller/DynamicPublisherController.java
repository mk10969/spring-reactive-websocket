package com.example.reactive.websocket.controller;


import com.example.reactive.websocket.model.Quote;
import com.example.reactive.websocket.service.HotQuotePublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DynamicPublisherController {

    private final HotQuotePublisherService hotPublisherService;


    @PostMapping("/dynamic")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<String> dynamic(@RequestBody Quote body) {
        log.info("TEST: {}", body);
        hotPublisherService.onNext(body);

        return Mono.just("OK");
    }

}



