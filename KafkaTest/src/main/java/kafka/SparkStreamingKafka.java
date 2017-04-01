//package kafka;
//
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.apache.spark.SparkConf;
//import org.apache.spark.api.java.JavaPairRDD;
//import org.apache.spark.api.java.JavaSparkContext;
//import org.apache.spark.streaming.Duration;
//import org.apache.spark.streaming.api.java.JavaInputDStream;
//import org.apache.spark.streaming.api.java.JavaPairDStream;
//import org.apache.spark.streaming.api.java.JavaStreamingContext;
//import org.apache.spark.streaming.kafka010.ConsumerStrategies;
//import org.apache.spark.streaming.kafka010.KafkaUtils;
//import org.apache.spark.streaming.kafka010.LocationStrategies;
//import scala.Tuple2;
//
//import java.time.Instant;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by Administrator on 2017/3/31.
// */
//public class SparkStreamingKafka {
//    public static void main(String[] args) {
//
//        Long start = Instant.now().toEpochMilli();
//
//        String appName = "PI";
//        String master = "local";
//        SparkConf conf = new SparkConf().setAppName(appName).setMaster(master);
//        // executor 向 the driver 汇报心跳的时间间隔，单位毫秒，默认10000
//        conf.set("spark.executor.heartbeatInterval", "5000");
//
//        Duration batchInterval = new Duration(5000);
//
//        JavaSparkContext jsc = new JavaSparkContext(conf);
//        JavaStreamingContext jssc = new JavaStreamingContext(jsc, batchInterval);
//
//        Map<String, Object> kafkaParams = new HashMap<>();
//        kafkaParams.put("bootstrap.servers", "172.16.66.252:9092");
//        kafkaParams.put("key.deserializer", StringDeserializer.class);
//        kafkaParams.put("value.deserializer", StringDeserializer.class);
//        kafkaParams.put("group.id", "");
//        kafkaParams.put("auto.offset.reset", "latest");
//        kafkaParams.put("enable.auto.commit", false);
//
//        Collection<String> topics = Arrays.asList("test");
//
//        final JavaInputDStream<ConsumerRecord<String, String>> stream =
//                KafkaUtils.createDirectStream(
//                        jssc,
//                        LocationStrategies.PreferConsistent(),
//                        ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams)
//                );
//
//        stream.foreachRDD(s -> s.foreach(r -> {
//            System.out.println(r.offset());
//            System.out.println(r.key());
//            System.out.println(r.value());
//        }));
//
////        JavaPairDStream<Long, Tuple2<String, String>> result = stream.mapToPair(
////                (record -> new Tuple2<>(record.offset(), new Tuple2<>(record.key(), record.value()))));
//
//        jssc.close();
//        jsc.close();
////        List<Tuple2<String, Integer>> output2 = result..compute().c.();
////        for (Tuple2<?, ?> tuple : output2) {
////            System.out.println(tuple._1() + ": " + tuple._2());
////        }
//    }
//}
