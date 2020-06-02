package com.example.websocket.server;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

public class WebFluxTest {

    @Test
    void test_パラレル() throws InterruptedException {
        Mono.fromCallable(() -> System.currentTimeMillis())
                .repeat()
                .delayElements(Duration.ofMillis(100L))
                .parallel(8) //parallelism
                .runOn(Schedulers.parallel())
                .doOnNext(d -> System.out.println("I'm on thread " + Thread.currentThread()))
                .subscribe();
        Thread.sleep(1000L);
    }


    @Test
    void test_リピート() throws InterruptedException {
        Flux<String> flux1 = Mono.fromCallable(() -> System.currentTimeMillis())
                .repeat()
                .map(i -> "aaaa")
                .delayElements(Duration.ofMillis(10L));

        Flux<String> flux2 = Mono.fromCallable(() -> System.currentTimeMillis())
                .repeat()
                .map(i -> "bbbb")
                .delayElements(Duration.ofMillis(10L));


        flux2.mergeWith(flux1)
                .doOnNext(d -> System.out.println(d))
                .subscribe();

        Thread.sleep(1000L);
    }
}
