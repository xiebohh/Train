package spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/3/27.
 */
public class HelloWorld {
    public static void main(String[] args) throws InterruptedException {
//        System.out.println("current system environment variable:");
//        Map<String,String> map = System.getenv();
//        Set<Map.Entry<String,String>> entries = map.entrySet();
//        for (Map.Entry<String, String> entry : entries) {
//            System.out.println(entry.getKey() + ":" + entry.getValue());
//        }
//
//        System.out.println("---------------");
//        System.out.println("current system properties:");
//        Properties properties = System.getProperties();
//        Set<Map.Entry<Object, Object>> set = properties.entrySet();
//        for (Map.Entry<Object, Object> objectObjectEntry : set) {
//            System.out.println(objectObjectEntry.getKey() + ":" + objectObjectEntry.getValue());
//        }

        String appName = "My First Python Spark";
        String master = "local";
        SparkConf conf = new SparkConf().setAppName(appName).setMaster(master);
        JavaSparkContext sc = new JavaSparkContext(conf);
        System.out.println("spark home:" + sc.getSparkHome());

        List<Integer> data = Arrays.asList(1, 2, 3, 4, 5);
        JavaRDD<Integer> distData = sc.parallelize(data);

        distData.reduce((a, b) -> a + b);
        sc.stop();
    }
}
