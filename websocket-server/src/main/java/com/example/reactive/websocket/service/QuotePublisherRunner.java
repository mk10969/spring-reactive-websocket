package com.example.reactive.websocket.service;

import com.example.reactive.websocket.model.Quote;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Random;

@Configuration
@RequiredArgsConstructor
public class QuotePublisherRunner {

    private final HotQuotePublisherService hotPublisherService;

    private final List<Quote> quotes;

    private static final Random random = new Random();


    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> Flux.interval(Duration.ofMillis(1000))
                .subscribe(i -> addQuote());
    }


    private void addQuote() {
        int x = random.nextInt(quotes.size());
        hotPublisherService.onNext(quotes.get(x));
    }

}
