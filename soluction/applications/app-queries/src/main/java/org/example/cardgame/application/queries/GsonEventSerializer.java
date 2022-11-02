package org.example.cardgame.application.queries;

import com.google.gson.Gson;
import org.example.cardgame.generic.DomainEvent;
import org.example.cardgame.generic.serialize.EventSerializer;
import org.springframework.stereotype.Component;

@Component
public class GsonEventSerializer extends AbstractSerializer implements EventSerializer {
    @Override
    public <T extends DomainEvent> T deserialize(String aSerialization, Class<?> aType) {
        return (T) new Gson().fromJson(aSerialization, aType);
    }

    @Override
    public String serialize(DomainEvent object) {
        return new Gson().toJson(object);
    }
}