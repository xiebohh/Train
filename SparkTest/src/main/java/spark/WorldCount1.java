package spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.util.LongAccumulator;
import scala.Tuple2;

import java.util.Arrays;
import java.util.regex.Pattern;


/**
 * Created by Administrator on 2017/3/28.
 */
public class WorldCount1 {
    private static final Pattern SPACE = Pattern.compile("\\|");

    public static void main(String[] args) throws InterruptedException {

        String appName = "My First Python Spark";
        String master = "local";
        SparkConf conf = new SparkConf().setAppName(appName).setMaster(master);
        JavaSparkContext jsc = new JavaSparkContext(conf);

//        JavaRDD<String> lines = jsc.textFile("file:\\C:\\Users\\Administrator\\Desktop\\saveMap\\pom.xml", 3);
//        JavaRDD<String> lines = jsc.textFile("file:\\G:\\PycharmProjects\\python-club\\machine_learn_IV\\4.python\\bigdata\\data_huge.txt");
        JavaRDD<String> lines = jsc.textFile("file:\\G:\\PycharmProjects\\python-club\\machine_learn_IV\\4.python\\bigdata\\data_small.txt");

        LongAccumulator a = jsc.sc().longAccumulator("EventAccumulator");
        LongAccumulator b = jsc.sc().longAccumulator("EventAccumulator");

//        lines = lines.flatMap(s -> {
//            a.add(1);
//            return Arrays.asList(SPACE.split(s))
//                    .iterator();
//        });

        JavaPairRDD<String, Integer> ones = lines.mapToPair(s -> {
            b.add(1);
            return new Tuple2<>(s, 1);
        });

        // shuffle可能会导致自动触发cache(硬盘)机制，后面的action都不会触发重新计算，累加器不变
        ones = ones.reduceByKey((i1, i2) -> i1 + i2);

//        lines = lines.map(e -> {
//            ac.add(1);
//            return e;
//        });

//        lines.cache();

        System.out.println("---------------------------------");
        System.out.println(ones.count());
        System.out.println("a = " + a.count());
        System.out.println("b = " + b.count());


//        Thread.sleep(1000 * 15);

        System.out.println("---------------------------------");
        System.out.println(ones.count());
        System.out.println("a = " + a.count());
        System.out.println("b = " + b.count());

        jsc.stop();
    }
}
