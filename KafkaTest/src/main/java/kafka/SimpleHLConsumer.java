package kafka;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import org.apache.log4j.BasicConfigurator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SimpleHLConsumer {

    private final ConsumerConnector consumer;
    private final String topic;

    public SimpleHLConsumer(String zookeeper, String groupId, String topic) {
//        BasicConfigurator.configure();
        Properties props = new Properties();
        props.put("zookeeper.connect", zookeeper);
        props.put("zookeeper.connection.timeout.ms", "10000");
        props.put("zookeeper.session.timeout.ms", "5000");
        props.put("zookeeper.sync.time.ms", "500");

        props.put("auto.commit.enable", "true");
        props.put("auto.commit.interval.ms", "500");

        props.put("auto.offset.reset", "largest");

        props.put("group.id", groupId);
        props.put("consumer.id", "cg");

        consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(props));
        this.topic = topic;
    }

    public void testConsumer() {
        Map<String, Integer> topicCount = new HashMap<>();
        topicCount.put(topic, 1);

        Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreams = consumer.createMessageStreams(topicCount);
        List<KafkaStream<byte[], byte[]>> streams = consumerStreams.get(topic);
        for (final KafkaStream<byte[], byte[]> stream : streams) {
            for (MessageAndMetadata<byte[], byte[]> aStream : stream) {
                System.out.println("partition: " + aStream.partition() + ", offset: " + aStream.offset() + ", message: " + new String(aStream.message()));
            }
        }
        consumer.shutdown();
    }

    public static void main(String[] args) {
        SimpleHLConsumer simpleHLConsumer = new SimpleHLConsumer("172.16.66.252:2181",  "test", "test");
        simpleHLConsumer.testConsumer();
    }

}