package kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.BasicConfigurator;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by Administrator on 2017/3/31.
 */
public class AutoCommitConsumer {

    public static void main(String[] args) {
        BasicConfigurator.configure();

        Properties props = new Properties();
        //brokerServer(kafka)ip地址,不需要把所有集群中的地址都写上，可是一个或一部分
//        props.put("bootstrap.servers", "172.16.66.252:9092");
//        //设置consumer group name,必须设置
//        props.put("group.id", "test");
//        //设置自动提交偏移量(offset),由auto.commit.interval.ms控制提交频率
//        props.put("enable.auto.commit", "true");
//        //偏移量(offset)提交频率
//        props.put("auto.commit.interval.ms", "1000");
//
//        //设置使用最开始的offset偏移量为该group.id的最早。如果不设置，则会是latest即该topic最新一个消息的offset
//        //如果采用latest，消费者只能得道其启动后，生产者生产的消息
//        props.put("auto.offset.reset", "earliest");
//
//        //设置心跳时间
//        props.put("session.timeout.ms", "10000");

//        props.put("bootstrap.servers", "172.16.66.252:9092");
        props.put("bootstrap.servers", "172.16.67.200:9092");
        props.put("zookeeper.connect", "172.16.67.200:2181/kafkagroup");
        props.put("group.id", "pv");
        props.put("zookeeper.session.timeout.ms", "400");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset", "earliest");

        //设置key以及value的解析（反序列）类
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        System.out.println(111);
        //利用try自动close
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            System.out.println(222);
            consumer.subscribe(Arrays.asList("66rpg"));
            System.out.println(333);
//            while (true) {
            ConsumerRecords<String, String> records = consumer.poll(3);
            System.out.println(444);
            for (ConsumerRecord<String, String> record : records)
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
//            }
        }
        System.out.println(555);
    }

}
