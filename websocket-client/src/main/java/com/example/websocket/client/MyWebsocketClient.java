package com.example.websocket.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;
import reactor.netty.tcp.TcpClient;

import java.net.URI;

@Slf4j
@Configuration
public class MyWebsocketClient {

    private static final String wsUri = "ws://localhost:8080/websocket";


    @Bean
    public CommandLineRunner runner() {
        return args -> run();
    }

    /**
     * proxyは、突破できない。。バグかもね。
     */
    public void run() {
        TcpClient tcpClient = TcpClient.create()
                .host("example.com")
                .port(80)
                .proxy(options -> options.type(ProxyProvider.Proxy.HTTP)
                        .host("proxy")
                        .port(8080));
        HttpClient httpClient = HttpClient.from(tcpClient);

        WebSocketClient client = new ReactorNettyWebSocketClient(httpClient);

        client.execute(URI.create(wsUri), this::websocketHandler).subscribe();
    }

    private Mono<Void> websocketHandler(WebSocketSession session) {
        return session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .doOnSubscribe(i -> log.info("session Id: {}", session.getId()))
                .log()
                .then();
    }

}
