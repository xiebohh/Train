package spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Administrator on 2017/3/28.
 */
public class PI {

    public static void main(String[] args) {

        Long start = Instant.now().toEpochMilli();

        String appName = "PI";
        String master = "local";
        SparkConf conf = new SparkConf().setAppName(appName).setMaster(master);
        // executor 向 the driver 汇报心跳的时间间隔，单位毫秒，默认10000
        conf.set("spark.executor.heartbeatInterval", "5000");

        JavaSparkContext sc = new JavaSparkContext(conf);

        Integer slices = 4;
        Integer n = 50000000;
        System.out.println(sc.defaultParallelism());
        Integer count = sc.parallelize(IntStream.range(0, n).boxed().collect(Collectors.toList()), slices)
        .map(i -> {
            Double x = Math.random() * 2 - 1;
            Double y = Math.random() * 2 - 1;
            return x * x + y * y < 1 ? 1: 0;
        }).reduce((i1, i2) -> i1 + i2);

        System.out.println("PI is roughly " + 4.0 * count / n);

        sc.stop();
        Long end = Instant.now().toEpochMilli();
        System.out.println("cost " + (end - start) / 1000.0 + "seconds");
    }
}
