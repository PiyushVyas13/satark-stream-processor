package org.cyducks.reportmanager.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.ArrayList;
import java.util.List;

public class JsonArraySerde<T> implements Serde<List<T>> {

    public static final ObjectMapper mapper = new ObjectMapper();
    private final Class<T> type;

    public JsonArraySerde(Class<T> type) {
        this.type = type;
    }

    @Override
    public Serializer<List<T>> serializer() {
        return (topic, data) -> serialize(data);
    }

    @Override
    public Deserializer<List<T>> deserializer() {
        return (topic, bytes) -> deserialize(bytes);
    }


    @SneakyThrows
    private byte[] serialize(List<T> data) {
        return mapper.writeValueAsBytes(data);
    }

    @SneakyThrows
    private List<T> deserialize(byte[] bytes) {
        return mapper.readValue(bytes, mapper.getTypeFactory().constructCollectionType(ArrayList.class, type));
    }
}
