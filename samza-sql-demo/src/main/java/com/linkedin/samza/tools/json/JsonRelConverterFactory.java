package com.linkedin.samza.tools.json;

import java.io.IOException;
import java.util.List;
import org.apache.commons.lang.NotImplementedException;
import org.apache.samza.SamzaException;
import org.apache.samza.config.Config;
import org.apache.samza.operators.KV;
import org.apache.samza.sql.data.SamzaSqlRelMessage;
import org.apache.samza.sql.interfaces.RelSchemaProvider;
import org.apache.samza.sql.interfaces.SamzaRelConverter;
import org.apache.samza.sql.interfaces.SamzaRelConverterFactory;
import org.apache.samza.system.SystemStream;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;


public class JsonRelConverterFactory implements SamzaRelConverterFactory {

  ObjectMapper mapper = new ObjectMapper();

  @Override
  public SamzaRelConverter create(SystemStream systemStream, RelSchemaProvider relSchemaProvider, Config config) {
    return new JsonrelConverter();
  }

  public class JsonrelConverter implements SamzaRelConverter {

    @Override
    public SamzaSqlRelMessage convertToRelMessage(KV<Object, Object> kv) {
      throw new NotImplementedException();
    }

    @Override
    public KV<Object, Object> convertToSamzaMessage(SamzaSqlRelMessage relMessage) {

      String jsonValue;
      ObjectNode node = mapper.createObjectNode();

      List<String> fieldNames = relMessage.getFieldNames();
      List<Object> values = relMessage.getFieldValues();

      for (int index = 0; index < fieldNames.size(); index++) {
        Object value = values.get(index);
        if (value == null) {
          continue;
        }

        // TODO limited support right now.
        if (Long.class.isAssignableFrom(value.getClass())) {
          node.put(fieldNames.get(index), (Long) value);
        } else if (Integer.class.isAssignableFrom(value.getClass())) {
          node.put(fieldNames.get(index), (Integer) value);
        } else if (Double.class.isAssignableFrom(value.getClass())) {
          node.put(fieldNames.get(index), (Double) value);
        } else if (String.class.isAssignableFrom(value.getClass())) {
          node.put(fieldNames.get(index), (String) value);
        } else {
          throw new SamzaException("Unsupported field type" + value.getClass());
        }
      }
      try {
        jsonValue = mapper.writeValueAsString(node);
      } catch (IOException e) {
        throw new SamzaException("Error json serializing object", e);
      }

      return new KV<>(relMessage.getKey(), jsonValue);
    }
  }
}
