package org.example.cardgame.websocket;


import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import org.example.cardgame.generic.DomainEvent;
import org.springframework.stereotype.Component;



@Component
public class RabbitMQEventConsumer  {
    private final Tracer tracer;
    private final GsonEventSerializer serializer;

    private final SocketController socketController;

    public RabbitMQEventConsumer(Tracer tracer,  GsonEventSerializer serializer,  SocketController socketController) {
        this.tracer = tracer;
        this.serializer = serializer;
        this.socketController = socketController;
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
            Span span = createSpan(notification, context, event).start();
            socketController.send(event.aggregateRootId(), event);
            span.finish();
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