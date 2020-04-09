package com.example.reactive.websocket.controller;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DynamicPublisherControllerTest {

    @Test
    void test() {
        assertTrue(true);
    }


    private final UnicastProcessor<Long> hotSource = UnicastProcessor.create(new ConcurrentLinkedQueue<>());


    @Test
    void test_mono() throws InterruptedException {
        Flux<Long> hotPublisher = hotSource.publish()
                .autoConnect()
                .publishOn(Schedulers.parallel())
                .log();
//                .sample(Duration.ofMillis(100));

        hotPublisher.subscribe(System.out::println);


        Executors.newSingleThreadExecutor();
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
        executor.execute(this::run);
        ScheduledThreadPoolExecutor executor2 = new ScheduledThreadPoolExecutor(2);
        executor2.execute(this::run2);


        Thread.sleep(10000L);

    }

    private void run2() {
        while (true) {
            long t = 111111111111111111L;
//            System.out.println(t);
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            hotSource.onNext(t);
        }
    }


    private void run() {
        while (true) {
            long t = System.currentTimeMillis();
//            System.out.println(t);
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            hotSource.onNext(t);
        }
    }

}