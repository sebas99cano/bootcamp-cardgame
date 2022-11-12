package org.example.cardgame.application.queries.adapter.bus;


import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.CurrentTraceContext;
import brave.propagation.TraceContext;
import org.example.cardgame.application.queries.ConfigProperties;
import org.example.cardgame.application.queries.GsonEventSerializer;
import org.example.cardgame.application.queries.handle.materialize.MaterializeLookUp;
import org.example.cardgame.generic.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.AcknowledgableDelivery;
import reactor.rabbitmq.ConsumeOptions;
import reactor.rabbitmq.ExceptionHandlers;
import reactor.rabbitmq.Receiver;

import java.time.Duration;



@Component
public class RabbitMQEventConsumer implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQEventConsumer.class);
    private final Tracer tracer;
    private final Tracing tracing;
    private final GsonEventSerializer serializer;
    private final ConfigProperties configProperties;
    private final Receiver receiver;
    private final MaterializeLookUp materializeLookUp;

    public RabbitMQEventConsumer(Tracer tracer, Tracing tracing, GsonEventSerializer serializer, ConfigProperties configProperties, Receiver receiver, MaterializeLookUp materializeLookUp) {
        this.tracer = tracer;
        this.tracing = tracing;
        this.serializer = serializer;
        this.configProperties = configProperties;
        this.receiver = receiver;
        this.materializeLookUp = materializeLookUp;
    }


    @Override
    public void run(String... args) {
        receiver.consumeManualAck(configProperties.getQueue(), new ConsumeOptions()
                .exceptionHandler(new ExceptionHandlers.RetryAcknowledgmentExceptionHandler(
                        Duration.ofSeconds(20), Duration.ofMillis(500),
                        ExceptionHandlers.CONNECTION_RECOVERY_PREDICATE
                )))
                .onBackpressureBuffer()
                .flatMap(message -> {
                    var notification = Notification.from(new String(message.getBody()));
                    var context = TraceContext.newBuilder()
                            .parentId(notification.getParentId())
                            .spanId(notification.getSpanId())
                            .traceId(notification.getTraceId())
                            .sampled(true)
                    .build();
                    try {
                        var event = serializer.deserialize(
                                notification.getBody(), Class.forName(notification.getType())
                        );
                        Span span = tracer.newChild(context)
                                .name("consumer")
                                .tag("eventType", event.type)
                                .tag("aggregateRootId", event.aggregateRootId())
                                .tag("aggregate", event.getAggregateName())
                                .tag("uuid", event.uuid.toString())
                                .annotate(notification.getBody())
                                .start();
                        return materializeLookUp.get(event.type)
                                .flatMap(materializeService -> materializeService.doProcessing(event))
                                .then(Mono.defer(() -> {
                                    span.finish();
                                    return Mono.just(message);
                                }));
                    } catch (ClassNotFoundException e) {
                       throw new IllegalArgumentException();
                    }

                }).subscribe(AcknowledgableDelivery::ack);
    }
}
