package org.udg.pds.springtodo.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.entity.Views;

import java.io.IOException;

/**
 * Created by imartin on 14/02/17.
 */
public class JsonUserSerializer extends JsonSerializer<User> {

  @Override
  public void serialize(User user, JsonGenerator gen, SerializerProvider provider)
          throws IOException, JsonProcessingException {
      gen.writeStartObject();
      gen.writeNumberField("id", user.getId());
      gen.writeStringField("name", user.getUsername());
      gen.writeEndObject();
  }
}
