package spark;

import com.clearspring.analytics.util.Lists;
import com.google.common.collect.ImmutableMap;
import org.apache.spark.Accumulator;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.util.AccumulatorV2;
import org.apache.spark.util.CollectionAccumulator;
import org.apache.spark.util.LongAccumulator;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/3/28.
 */
public class FilePrintLine {

    public static void main(String[] args) {

        String appName = "My First Python Spark";
        String master = "local";
        SparkConf conf = new SparkConf().setAppName(appName).setMaster(master);
        JavaSparkContext jsc = new JavaSparkContext(conf);

        final Integer[] i = {0};
        System.out.println(i[0]);

        LongAccumulator ac = jsc.sc().longAccumulator("EventAccumulator");
        CollectionAccumulator<Map<Integer, String>> cac = jsc.sc().collectionAccumulator("EventAccumulator");
        JavaRDD<String> lines = jsc.textFile("file:\\C:\\Users\\Administrator\\Desktop\\2017-03-24-10.log", 4);
        lines.cache();

        // 不能用System.out::println，因为存在序列化的问题
        System.out.println("---------------------------------");
        lines.foreach(e ->
        {
            i[0] += 1;
            ac.add(2);
            cac.add(ImmutableMap.of(e.length(),e));
            System.out.println(i[0]);
            System.out.println(e);
        });

        jsc.stop();

        System.out.println(i[0]);
        System.out.println(ac.count());
        System.out.println(ac.value());
        cac.value().forEach(System.out::println);
    }
}
