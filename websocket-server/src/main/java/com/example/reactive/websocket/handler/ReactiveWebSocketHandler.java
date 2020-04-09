package com.example.reactive.websocket.handler;


import com.example.reactive.websocket.model.Quote;
import com.example.reactive.websocket.service.HotQuotePublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component("websocket")
@RequiredArgsConstructor
public class ReactiveWebSocketHandler implements WebSocketHandler {

    private final HotQuotePublisherService hotPublisherService;


    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(hotPublisherService.getHotPublisher()
                .map(Quote::toString)
                .map(session::textMessage)
                .log());
    }

}
