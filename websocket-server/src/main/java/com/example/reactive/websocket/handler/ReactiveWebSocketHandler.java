package com.example.reactive.websocket.handler;


import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component("test")
public class ReactiveWebSocketHandler implements WebSocketHandler {

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(Flux.interval(Duration.ofMillis(100))
                .map(this::createMessage)
                .map(Message::toString)
                .map(session::textMessage));
    }


    private Message createMessage(long i) {
        return Message.builder().name("once").number(i).build();
    }


    @Data
    @Builder
    public static class Message{

        private String name;

        private Long number;
    }

}
