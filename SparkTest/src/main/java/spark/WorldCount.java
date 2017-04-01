package spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.util.LongAccumulator;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Created by Administrator on 2017/3/28.
 */
public class WorldCount {
    private static final Pattern SPACE = Pattern.compile(" ");

    public static void main(String[] args) throws InterruptedException {

        String appName = "My First Python Spark";
        String master = "local";
        SparkConf conf = new SparkConf().setAppName(appName).setMaster(master);
        JavaSparkContext jsc = new JavaSparkContext(conf);

//        JavaRDD<String> lines = jsc.textFile("file:\\C:\\Users\\Administrator\\Desktop\\saveMap\\pom.xml", 3);
        JavaRDD<String> lines = jsc.textFile("file:\\G:\\PycharmProjects\\python-club\\machine_learn_IV\\4.python\\bigdata\\data_huge.txt");
        LongAccumulator ac = jsc.sc().longAccumulator("EventAccumulator");
        System.out.println("level = " + lines.getStorageLevel().description());

        JavaRDD<String> words = lines.flatMap(s -> Arrays.asList(SPACE.split(s))
                .iterator());

        JavaPairRDD<String, Integer> ones = words.mapToPair(a -> {
            ac.add(1);
            return new Tuple2<>(a, 1);
        });

//        ones.cache();

        JavaPairRDD<String, Integer> counts = ones.reduceByKey((i1, i2) -> i1 + i2);


        System.out.println("---------------------------------");
        System.out.println(counts.count());
//        List<Tuple2<String, Integer>> output1 = counts.collect();
//        for (Tuple2<String, Integer> tuple : output1) {
//            System.out.println(tuple._1() + ": " + tuple._2());
//        }
        System.out.println("ac = " + ac.count());

        // ones.cache() 直接起作用，ones.unpersist() 不起作用，而ones.unpersist().count()起作用
        // This is relevant because a cache or persist call just adds the RDD to a
        // Map of RDDs that marked themselves to be persisted during job execution.
        // However, unpersist directly tells the blockManager to evict the RDD from
        // storage and removes the reference in the Map of persistent RDDs.
        // RDD的persist()和unpersist()操作，都是由SparkContext执行的（SparkContext的persistRDD和unpersistRDD方法）。
        // Persist过程是把该RDD存在上下文的TimeStampedWeakValueHashMap里维护起来。也就是说，其实persist并不是action，并不会触发任何计算。
        // Unpersist过程如下，会交给SparkEnv里的BlockManager处理。

//        Thread.sleep(1000 * 15);
////        ones.unpersist();
//        ones.unpersist().count();

        System.out.println("level = " + ones.getStorageLevel().description());

        System.out.println("---------------------------------");
        System.out.println(counts.count());
//        List<Tuple2<String, Integer>> output2 = counts.collect();
//        for (Tuple2<?, ?> tuple : output2) {
//            System.out.println(tuple._1() + ": " + tuple._2());
//        }
        System.out.println("ac = " + ac.count());

        jsc.stop();
    }
}
