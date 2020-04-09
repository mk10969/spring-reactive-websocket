package com.example.reactive.websocket.service;


import com.example.reactive.websocket.model.Quote;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class HotQuotePublisherService {

    private final UnicastProcessor<Quote> hotSource =
            UnicastProcessor.create(new ConcurrentLinkedQueue<>());


    public void onNext(Quote quote) {
        this.hotSource.onNext(quote);
    }


    public Flux<Quote> getHotPublisher() {
        return this.hotSource.publish().autoConnect().publishOn(Schedulers.elastic());
    }


}
