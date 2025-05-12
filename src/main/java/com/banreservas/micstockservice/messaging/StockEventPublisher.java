package com.banreservas.micstockservice.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class StockEventPublisher<T> {

    private final KafkaTemplate<String, T> kafkaTemplate;

    private final String topic;

    public Mono<SendResult<String, T>> send(String key, T event) {

        return Mono.fromFuture(kafkaTemplate.send(topic, key, event))
                .doOnNext(sendResult -> log.trace("Published event class: [{}] to topic: [{}]", event.getClass(), topic))
                .doOnError(Mono::error);
    }
}
