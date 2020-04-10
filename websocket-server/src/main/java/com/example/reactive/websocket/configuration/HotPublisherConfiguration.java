package com.example.reactive.websocket.configuration;

import com.example.reactive.websocket.model.Quote;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import java.util.concurrent.ConcurrentLinkedQueue;

@Configuration
public class HotPublisherConfiguration {

    @Bean
    public UnicastProcessor<Quote> hotSource() {
        return UnicastProcessor.create(new ConcurrentLinkedQueue<>());
    }

    @Bean
    public Flux<Quote> hotPublisher(UnicastProcessor<Quote> processor) {
        return processor.publish().autoConnect();
//               .doOnCancel(() -> System.out.println("キャンセル"));
    }

}
