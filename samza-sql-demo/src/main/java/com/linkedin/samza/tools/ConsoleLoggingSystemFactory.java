package com.linkedin.samza.tools;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang.NotImplementedException;
import org.apache.samza.Partition;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemAdmin;
import org.apache.samza.system.SystemConsumer;
import org.apache.samza.system.SystemFactory;
import org.apache.samza.system.SystemProducer;
import org.apache.samza.system.SystemStreamMetadata;
import org.apache.samza.system.SystemStreamPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Console logging System factory that just writes the messages to the console output.
 * This system factory is useful when the user wants to print the output of the stream processing to console.
 */
public class ConsoleLoggingSystemFactory implements SystemFactory {

  private static final Logger LOG = LoggerFactory.getLogger(ConsoleLoggingSystemFactory.class);

  @Override
  public SystemConsumer getConsumer(String systemName, Config config, MetricsRegistry registry) {
    throw new NotImplementedException();
  }

  @Override
  public SystemProducer getProducer(String systemName, Config config, MetricsRegistry registry) {
    return new LoggingSystemProducer();
  }

  @Override
  public SystemAdmin getAdmin(String systemName, Config config) {
    return new SimpleSystemAdmin(config);
  }

  private class LoggingSystemProducer implements SystemProducer {
    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void register(String source) {
      LOG.info("Registering source" + source);
    }

    @Override
    public void send(String source, OutgoingMessageEnvelope envelope) {
      String msg = String.format("OutputStream:%s Key:%s Value:%s", envelope.getSystemStream(), envelope.getKey(),
          envelope.getMessage());
      LOG.info(msg);
      System.out.println(msg);
    }

    @Override
    public void flush(String source) {
    }
  }

  public static class SimpleSystemAdmin implements SystemAdmin {

    public SimpleSystemAdmin(Config config) {
    }

    @Override
    public Map<SystemStreamPartition, String> getOffsetsAfter(Map<SystemStreamPartition, String> offsets) {
      return offsets.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, null));
    }

    @Override
    public Map<String, SystemStreamMetadata> getSystemStreamMetadata(Set<String> streamNames) {
      return streamNames.stream()
          .collect(Collectors.toMap(Function.identity(), streamName -> new SystemStreamMetadata(streamName,
              Collections.singletonMap(new Partition(0),
                  new SystemStreamMetadata.SystemStreamPartitionMetadata(null, null, null)))));
    }

    @Override
    public Integer offsetComparator(String offset1, String offset2) {
      if (offset1 == null) {
        return offset2 == null ? 0 : -1;
      } else if (offset2 == null) {
        return 1;
      }
      return offset1.compareTo(offset2);
    }
  }
}