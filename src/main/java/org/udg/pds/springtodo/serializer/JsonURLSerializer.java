package org.udg.pds.springtodo.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class JsonURLSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String objectName, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
        gen.writeString("http://localhost:8080/images/" + objectName);
    }
}
