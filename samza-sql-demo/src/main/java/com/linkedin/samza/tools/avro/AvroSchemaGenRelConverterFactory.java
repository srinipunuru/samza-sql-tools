package com.linkedin.samza.tools.avro;

import java.util.HashMap;
import org.apache.samza.config.Config;
import org.apache.samza.sql.avro.AvroRelSchemaProvider;
import org.apache.samza.sql.interfaces.RelSchemaProvider;
import org.apache.samza.sql.interfaces.SamzaRelConverter;
import org.apache.samza.sql.interfaces.SamzaRelConverterFactory;
import org.apache.samza.system.SystemStream;


public class AvroSchemaGenRelConverterFactory implements SamzaRelConverterFactory {

  private final HashMap<SystemStream, SamzaRelConverter> relConverters = new HashMap<>();

  @Override
  public SamzaRelConverter create(SystemStream systemStream, RelSchemaProvider relSchemaProvider, Config config) {
    return relConverters.computeIfAbsent(systemStream,
        ss -> new AvroSchemaGenRelConverter(ss, (AvroRelSchemaProvider) relSchemaProvider, config));
  }
}
