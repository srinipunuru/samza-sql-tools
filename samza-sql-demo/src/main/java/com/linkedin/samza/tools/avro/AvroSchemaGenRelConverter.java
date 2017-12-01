package com.linkedin.samza.tools.avro;

import com.linkedin.samza.tools.schemas.TestClass;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.reflect.ReflectData;
import org.apache.samza.config.Config;
import org.apache.samza.operators.KV;
import org.apache.samza.sql.avro.AvroRelConverter;
import org.apache.samza.sql.avro.AvroRelSchemaProvider;
import org.apache.samza.sql.data.SamzaSqlRelMessage;
import org.apache.samza.system.SystemStream;


public class AvroSchemaGenRelConverter extends AvroRelConverter {

  private final Schema arraySchema;
  private final Schema mapSchema;
  private final String streamName;
  private Map<String, Schema> _schemas = new HashMap<>();

  public AvroSchemaGenRelConverter(SystemStream systemStream, AvroRelSchemaProvider schemaProvider, Config config) {
    super(systemStream, schemaProvider, config);
    Schema testClassSchema = ReflectData.get().getSchema(TestClass.class);
    arraySchema = testClassSchema.getField("arrayValue").schema();
    mapSchema = testClassSchema.getField("mapValue").schema();
    streamName = systemStream.getStream();
  }

  @Override
  public KV<Object, Object> convertToSamzaMessage(SamzaSqlRelMessage relMessage) {
    Schema schema = computeSchema(streamName, relMessage);
    GenericRecord record = new GenericData.Record(schema);
    List<String> fieldNames = relMessage.getFieldNames();
    List<Object> values = relMessage.getFieldValues();
    for (int index = 0; index < fieldNames.size(); index++) {
      record.put(fieldNames.get(index), values.get(index));
    }

    return new KV<>(relMessage.getKey(), record);
  }

  private Schema computeSchema(String streamName, SamzaSqlRelMessage relMessage) {
    List<Schema.Field> keyFields = new ArrayList<>();
    List<String> fieldNames = relMessage.getFieldNames();
    List<Object> values = relMessage.getFieldValues();

    for (int index = 0; index < fieldNames.size(); index++) {
      if (fieldNames.get(index).equals(SamzaSqlRelMessage.KEY_NAME) || values.get(index) == null) {
        continue;
      }

      Object value = values.get(index);
      Schema avroType;
      if (value instanceof GenericData.Record) {
        avroType = ((GenericData.Record) value).getSchema();
      } else if (value instanceof Collection) {
        avroType = arraySchema;
      } else if (value instanceof Map) {
        avroType = mapSchema;
      } else {
        avroType = ReflectData.get().getSchema(value.getClass());
      }
      keyFields.add(new Schema.Field(fieldNames.get(index), avroType, "", null));
    }

    Schema ks = Schema.createRecord(streamName, "", streamName + "_namespace", false);
    ks.setFields(keyFields);
    String schemaStr = ks.toString();
    Schema schema;
    // See whether we have a schema object corresponding to the schemaValue and reuse it.
    // CachedSchemaRegistryClient doesn't like if we recreate schema objects.
    if (_schemas.containsKey(schemaStr)) {
      schema = _schemas.get(schemaStr);
    } else {
      schema = Schema.parse(schemaStr);
      _schemas.put(schemaStr, schema);
    }

    return schema;
  }
}
