package kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by Administrator on 2017/3/31.
 */
public class ManualCommitConsumer {

    final static Integer minBatchSize = 5;  //批量提交数量

    public static void main(String[] args) {
        Properties props = new Properties();
        //brokerServer(kafka)ip地址,不需要把所有集群中的地址都写上，可是一个或一部分
        props.put("bootstrap.servers", "172.16.66.252:9092");
        //设置consumer group name,必须设置
        props.put("group.id", "test");

        //设置不自动提交偏移量(offset)
        props.put("enable.auto.commit", "false");

        //设置使用最开始的offset偏移量为该group.id的最早。如果不设置，则会是latest即该topic最新一个消息的offset
        //如果采用latest，消费者只能得道其启动后，生产者生产的消息
        props.put("auto.offset.reset", "earliest");

        //设置心跳时间
        props.put("session.timeout.ms", "10000");

        //设置key以及value的解析（反序列）类
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        //利用try自动close
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            List<ConsumerRecord<String, String>> buffer = new ArrayList<>();
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println("consumer message values is "+record.value()+" and the offset is "+ record.offset());
                    buffer.add(record);
                }
                if (buffer.size() >= minBatchSize) {
                    System.out.println("now commit offset");
                    consumer.commitSync();
                    buffer.clear();
                }
            }
        }
    }

}
