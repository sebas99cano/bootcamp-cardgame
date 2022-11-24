package org.example.cardgame.application.queries.adapter.bus;


import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import org.example.cardgame.application.queries.GsonEventSerializer;
import org.example.cardgame.application.queries.handle.materialize.MaterializeLookUp;
import org.example.cardgame.generic.DomainEvent;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;


@Component
public class RabbitMQEventConsumer  {
    private final Tracer tracer;
    private final GsonEventSerializer serializer;

    private final MaterializeLookUp materializeLookUp;

    public RabbitMQEventConsumer(Tracer tracer, GsonEventSerializer serializer, MaterializeLookUp materializeLookUp) {
        this.tracer = tracer;
        this.serializer = serializer;
        this.materializeLookUp = materializeLookUp;
    }


    public void receiveMessage(String message) {
        var notification = Notification.from(message);
        var context = TraceContext.newBuilder()
                .parentId(notification.getParentId())
                .spanId(notification.getSpanId())
                .traceId(notification.getTraceId())
                .build();
        try {
            var event = serializer.deserialize(
                    notification.getBody(), Class.forName(notification.getType())
            );
            Span span = createSpan(notification, context, event);
            materializeLookUp.get(event.type)
                    .doFirst(span::start)
                    .flatMap(materializeService -> materializeService.doProcessing(event))
                    .then(Mono.defer(() -> {
                        span.finish();
                        return Mono.just(message);
                    })).subscribe();
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException();
        }
    }

    private Span createSpan(Notification notification, TraceContext context, DomainEvent event) {
        return tracer.newChild(context)
                .name("consumer")
                .tag("eventType", event.type)
                .tag("aggregateRootId", event.aggregateRootId())
                .tag("aggregate", event.getAggregateName())
                .tag("uuid", event.uuid.toString())
                .annotate(notification.getBody())
                .kind(Span.Kind.SERVER);
    }
}