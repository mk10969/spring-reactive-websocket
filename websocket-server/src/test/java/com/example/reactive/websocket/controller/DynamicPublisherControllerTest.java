package com.example.reactive.websocket.controller;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
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


    void test_mono() throws InterruptedException {
        Flux<Long> hotPublisher = hotSource.publish().autoConnect()
                .publishOn(Schedulers.parallel());
        Flux<Long> hotPublisher2 = hotSource.publish().autoConnect()
                .publishOn(Schedulers.parallel());


        hotPublisher.subscribe(System.out::println);
//        hotPublisher2.subscribe(System.out::println);

        Executors.newSingleThreadExecutor();
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
        executor.execute(this::run);
        ScheduledThreadPoolExecutor executor2 = new ScheduledThreadPoolExecutor(2);
        executor2.execute(this::run2);
        Thread.sleep(2000L);
        Thread.sleep(10000L);

    }

    private void run2() {
        while (true) {
            long t = 111111111111111111L;
//            System.out.println(t);
            try {
                Thread.sleep(500L);
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


    public void givenFluxes_whenZipWithIsInvoked_thenZipWith() throws InterruptedException {

        int min = 1;
        int max = 1000;

        Flux<Integer> evenNumbers = Flux
                .range(min, max)
                .filter(x -> x % 2 == 0);

        Flux<Integer> oddNumbers = Flux
                .range(min, max)
                .filter(x -> x % 2 > 0);


        Flux<Integer> fluxOfIntegers = evenNumbers
                .zipWith(oddNumbers, (a, b) -> a * b);

        fluxOfIntegers.subscribe(System.out::println);


        Thread.sleep(1000L);
//       StepVerifier.create(fluxOfIntegers)
//                .expectNext(2)  // 2 * 1
//                .expectNext(12) // 4 * 3
//                .expectComplete()
//                .verify();
    }


    public void givenFluxes_whenConcatIsInvoked_thenConcat() throws InterruptedException {

        int min = 1;
        int max = 10;

        Flux<Integer> evenNumbers = Flux
                .range(min, max)
                .filter(x -> x % 2 == 0);

        Flux<Integer> oddNumbers = Flux
                .range(min, max)
                .filter(x -> x % 2 > 0);

        Flux<Integer> fluxOfIntegers = Flux.concat(evenNumbers, oddNumbers);

        fluxOfIntegers.log().subscribe(System.out::println);

        Thread.sleep(1000L);
    }


    public void givenFluxes_whenMergeWithDelayedElementsIsInvoked_thenMergeWithDelayedElements() throws InterruptedException {
        int min = 1;
        int max = 5;

        Flux<Integer> evenNumbers = Flux
                .range(min, max)
                .filter(x -> x % 2 == 0);

        Flux<Integer> oddNumbers = Flux
                .range(min, max)
                .filter(x -> x % 2 > 0);

        Flux<Integer> fluxOfIntegers = Flux.merge(
                evenNumbers.delayElements(Duration.ofMillis(500L)),
                oddNumbers.delayElements(Duration.ofMillis(300L)));

        StepVerifier.create(fluxOfIntegers)
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(5)
                .expectNext(4)
                .expectComplete()
                .verify();

        Thread.sleep(1000L);

    }


    void test_merge() throws InterruptedException {
        Flux<Long> evenNumbers = Flux.interval(Duration.ofMillis(100))
                .map(Long::new);

        Flux<Long> hotPublisher = hotSource.publish()
                .autoConnect()
                .publishOn(Schedulers.parallel());

        evenNumbers.mergeWith(hotPublisher)
                .log()
                .subscribe(System.out::println);

        hotSource.onNext(1000000L);
        Thread.sleep(200L);
        hotSource.onNext(1000000L);
        Thread.sleep(200L);
        hotSource.onNext(1000000L);
        Thread.sleep(10000L);

    }
}