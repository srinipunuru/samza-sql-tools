package com.linkedin.samza.tools;

import com.linkedin.samza.tools.schemas.PageViewEvent;
import com.linkedin.samza.tools.schemas.ProfileChangeEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.function.Function;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GenerateKafkaEvents {

  private static final String OPT_SHORT_TOPIC_NAME = "t";
  private static final String OPT_LONG_TOPIC_NAME = "topic";
  private static final String OPT_ARG_TOPIC_NAME = "TOPIC_NAME";
  private static final String OPT_DESC_TOPIC_NAME = "Name of the topic to write events to.";

  private static final String DEFAULT_NUM_PARTITIONS = "4";
  private static final String OPT_SHORT_NUM_PARTITIONS = "p";
  private static final String OPT_LONG_NUM_PARTITIONS = "partitions";
  private static final String OPT_ARG_NUM_PARTITIONS = "NUM_PARTITIONS";
  private static final String OPT_DESC_NUM_PARTITIONS =
      String.format("Number of partitions in the topic, Default (%s).", DEFAULT_NUM_PARTITIONS);

  private static final String OPT_SHORT_BROKER = "b";
  private static final String OPT_LONG_BROKER = "broker";
  private static final String OPT_ARG_BROKER = "BROKER";
  private static final String OPT_DESC_BROKER = "Kafka broker endpoint.";
  private static final String DEFAULT_BROKER = "localhost:9092";

  private static final String OPT_SHORT_NUM_EVENTS = "n";
  private static final String OPT_LONG_NUM_EVENTS = "numEvents";
  private static final String OPT_ARG_NUM_EVENTS = "NUM_EVENTS";
  private static final String OPT_DESC_NUM_EVENTS = "Number of events to be produced.";

  private static final String OPT_SHORT_EVENT_TYPE = "e";
  private static final String OPT_LONG_EVENT_TYPE = "eventtype";
  private static final String OPT_ARG_EVENT_TYPE = "EVENT_TYPE";
  private static final String OPT_DESC_EVENT_TYPE = "Type of the event (PageView|ProfileChange) Default(ProfileChange).";

  private static final Logger LOG = LoggerFactory.getLogger(GenerateKafkaEvents.class);
  private static RandomValueGenerator _randValueGenerator;

  private static String[] companies =
      new String[]{"Microsoft", "LinkedIn", "Google", "Facebook", "Amazon", "Apple", "Twitter", "Snap"};

  private static final String PAGEVIEW_TOPICNAME = "page";

  public static void main(String[] args) throws UnsupportedEncodingException, InterruptedException {
    _randValueGenerator = new RandomValueGenerator(System.currentTimeMillis());
    Options options = new Options();
    options.addOption(
        Utils.createOption(OPT_SHORT_TOPIC_NAME, OPT_LONG_TOPIC_NAME, OPT_ARG_TOPIC_NAME, true, OPT_DESC_TOPIC_NAME));

    options.addOption(
        Utils.createOption(OPT_SHORT_NUM_PARTITIONS, OPT_LONG_NUM_PARTITIONS, OPT_ARG_NUM_PARTITIONS, false,
            OPT_DESC_NUM_PARTITIONS));

    options.addOption(Utils.createOption(OPT_SHORT_BROKER, OPT_LONG_BROKER, OPT_ARG_BROKER, false, OPT_DESC_BROKER));

    options.addOption(
        Utils.createOption(OPT_SHORT_NUM_EVENTS, OPT_LONG_NUM_EVENTS, OPT_ARG_NUM_EVENTS, false, OPT_DESC_NUM_EVENTS));

    options.addOption(
        Utils.createOption(OPT_SHORT_EVENT_TYPE, OPT_LONG_EVENT_TYPE, OPT_ARG_EVENT_TYPE, false, OPT_DESC_EVENT_TYPE));

    CommandLineParser parser = new BasicParser();
    CommandLine cmd;
    try {
      cmd = parser.parse(options, args);
    } catch (Exception e) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(String.format("Error: %s%ngenerate-events.sh", e.getMessage()), options);
      return;
    }

    String topicName = cmd.getOptionValue(OPT_SHORT_TOPIC_NAME);
    String broker = cmd.getOptionValue(OPT_SHORT_BROKER, DEFAULT_BROKER);
    long numEvents = Long.parseLong(cmd.getOptionValue(OPT_SHORT_NUM_EVENTS, "-1"));
    String eventType = cmd.getOptionValue(OPT_SHORT_EVENT_TYPE);
//    int numPartitions = Integer.parseInt(cmd.getOptionValue(OPT_SHORT_NUM_PARTITIONS, DEFAULT_NUM_PARTITIONS));
//    createTopicIfNotExists(broker, topicName, numPartitions);
    generateEvents(broker, topicName, eventType, numEvents);
  }

//  private static void createTopicIfNotExists(String brokers, String topic, int numPartitions) {
//    Properties props = new Properties();
//    props.put("bootstrap.servers", brokers);
//
//
//    AdminClient adminClient = AdminClient.create(props);
//    try {
//      adminClient.describeTopics(Collections.singletonList(topic));
//    } catch (InvalidTopicException e) {
//      adminClient.createTopics(Collections.singletonList(new NewTopic(topic, numPartitions, (short) 1)));
//    }
//    adminClient.close();
//  }

  private static void generateEvents(String brokers, String topicName, String eventType, long numEvents)
      throws UnsupportedEncodingException, InterruptedException {
    Properties props = new Properties();
    props.put("bootstrap.servers", brokers);
    props.put("retries", 100);
    props.put("batch.size", 16384);
    props.put("key.serializer", ByteArraySerializer.class.getCanonicalName());
    props.put("value.serializer", ByteArraySerializer.class.getCanonicalName());

    Function<Integer, Pair<String, byte[]>> eventGenerator;
    if (eventType.toLowerCase().contains(PAGEVIEW_TOPICNAME)) {
      eventGenerator = GenerateKafkaEvents::generatePageViewEvent;
    } else {
      eventGenerator = GenerateKafkaEvents::generateProfileChangeEvent;
    }
    if (numEvents < 0) {
      numEvents = Long.MAX_VALUE;
    }

    try (Producer<byte[], byte[]> producer = new KafkaProducer<>(props)) {
      for (int index = 0; index < numEvents; index++) {
        final int finalIndex = 0;
        Pair<String, byte[]> record = eventGenerator.apply(index);
        producer.send(new ProducerRecord<>(topicName, record.getLeft().getBytes("UTF-8"), record.getRight()),
            (metadata, exception) -> {
              if (exception == null) {
                LOG.info("send completed for event {} at offset {}", finalIndex, metadata.offset());
              } else {
                throw new RuntimeException("Failed to send message.", exception);
              }
            });
        System.out.println(String.format("Published event %d to topic %s", index, topicName));
        Thread.sleep(1000);
      }

      producer.flush();
    }
  }

  private static Pair<String, byte[]> generateProfileChangeEvent(Integer index) {
    ProfileChangeEvent event = new ProfileChangeEvent();
    String name = _randValueGenerator.getNextString(10, 20);
    event.Name = name;
    event.NewCompany = companies[_randValueGenerator.getNextInt(0, companies.length - 1)];
    event.OldCompany = companies[_randValueGenerator.getNextInt(0, companies.length - 1)];
    event.ProfileChangeTimestamp = System.currentTimeMillis();
    byte[] value;
    try {
      value = encodeAvroSpecificRecord(ProfileChangeEvent.class, event);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return new ImmutablePair<>(name, value);
  }

  /**
   * Encode an Avro record into byte array
   *
   * @param clazz The class type of the Avro record
   * @param record the instance of the avro record
   * @param <T> The type of the avro record.
   * @return encoded bytes
   * @throws java.io.IOException
   */
  public static <T> byte[] encodeAvroSpecificRecord(Class<T> clazz, T record) throws IOException {
    DatumWriter<T> msgDatumWriter = new SpecificDatumWriter<>(clazz);
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    Encoder encoder = EncoderFactory.get().binaryEncoder(os, null);
    msgDatumWriter.write(record, encoder);
    encoder.flush();
    return os.toByteArray();
  }

  private static Pair<String, byte[]> generatePageViewEvent(int index) {
    PageViewEvent event = new PageViewEvent();
    String name = _randValueGenerator.getNextString(10, 20);
    event.id = _randValueGenerator.getNextInt();
    event.Name = name;
    event.ViewerName = _randValueGenerator.getNextString(10, 20);
    event.ProfileViewTimestamp = System.currentTimeMillis();
    byte[] value;
    try {
      value = encodeAvroSpecificRecord(PageViewEvent.class, event);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return new ImmutablePair<>(name, value);
  }
}
