package com.example.reactive.websocket.handler;


import com.example.reactive.websocket.model.Quote;
import com.example.reactive.websocket.service.HotQuotePublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;

@Component("websocket")
@RequiredArgsConstructor
public class ReactiveWebSocketHandler implements WebSocketHandler {

    private final HotQuotePublisherService hotPublisherService;

    private final List<Quote> quotes;

    private static final long millisecond = 500L;

    private static final Random random = new Random();


    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(Flux.interval(Duration.ofMillis(millisecond))
                .map(i -> getQuote())
                .mergeWith(hotPublisherService.getHotPublisher())
                .map(Quote::toString)
                .map(session::textMessage)
                .log());
    }

    private Quote getQuote() {
        int x = random.nextInt(quotes.size());
        return quotes.get(x);
    }

}
