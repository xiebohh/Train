package spark;

import com.clearspring.analytics.util.Lists;
import com.google.common.collect.Maps;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.util.LongAccumulator;
import scala.Tuple2;
import scala.Tuple3;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * Created by Administrator on 2017/3/28.
 */
public class LogFilter {
    private static final Pattern SPACE = Pattern.compile(" ");

    private static final Map<String, Integer> itemIndexMap = getItemIndexMap();

    private static Map<String, Integer> getItemIndexMap() {
        Map<String, Integer> itemIndexMap = Maps.newLinkedHashMap();
        List<String> itemList = Lists.newArrayList();
        itemList.add("first_name");
        itemList.add("last_name");
        itemList.add("email");
        itemList.add("company");
        itemList.add("job");
        itemList.add("street_address");
        itemList.add("city");
        itemList.add("state_abbr");
        itemList.add("zipcode_plus4");
        itemList.add("url");
        itemList.add("phone_number");
        itemList.add("user_agent");
        itemList.add("user_name");
        for (int i = 0; i <= 328; i++) {
            itemList.add("letter_" + i);
            itemList.add("number_" + i);
            itemList.add("bool_" + i);
        }
        final Integer[] order = {0};
        itemList.forEach(e -> {
            itemIndexMap.put(e, order[0]);
            order[0] += 1;
        });
        return itemIndexMap;
    }

    public static void main(String[] args) throws InterruptedException {
        Long start = Instant.now().toEpochMilli();

        Integer slice = 4;

        String appName = "My First Python Spark";
        String master = "local";
        SparkConf conf = new SparkConf().setAppName(appName).setMaster(master);
        conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
        JavaSparkContext jsc = new JavaSparkContext(conf);

        LongAccumulator ac = jsc.sc().longAccumulator("EventAccumulator");


        JavaRDD<String> peopleTxtRdd = jsc.textFile("file:\\G:\\PycharmProjects\\python-club\\machine_learn_IV\\4.python\\bigdata\\data_huge.txt", 1);
//        JavaRDD<String> peopleTxtRdd = jsc.textFile("file:\\G:\\PycharmProjects\\python-club\\machine_learn_IV\\4.python\\bigdata\\data_small.txt", 3);


        JavaRDD<String[]> resultRdd1 = peopleTxtRdd.map(e -> e.split("\\|")).filter(e -> {
                    ac.add(1);
//                    return e[getIndex("last_name")].matches("^w.*") &&
//                            e[getIndex("email")].matches(".*com.*") &&
//                            e[getIndex("letter_77")].equals("r") &&
//                            Integer.valueOf(e[getIndex("number_106")]) < 300 &&
//                            Boolean.valueOf(e[getIndex("bool_143")]) &&
//                            !e[getIndex("letter_252")].equals("o") &&
//                            Integer.valueOf(e[getIndex("number_311")]) > 400;
////                    System.out.println(e[getIndex("first_name")]);
////                    System.out.println(e[getIndex("first_name")].matches("^M.*"));
                    return e[getIndex("first_name")].matches("^M.*");
                }
        );

        System.out.println("Partitions Number: " + resultRdd1.getNumPartitions());
        resultRdd1 = resultRdd1.coalesce(resultRdd1.getNumPartitions());
        System.out.println("Partitions Number: " + resultRdd1.getNumPartitions());

        JavaPairRDD<Tuple3<String, String, String>, Integer> resultRdd2 = resultRdd1
                .mapToPair(e ->
                        new Tuple2<>(new Tuple3<>(e[getIndex("city")], e[getIndex("state_abbr")], e[getIndex("state_abbr")]), 1))
                .reduceByKey((i1, i2) -> i1 + i2);

        System.out.println("Partitions Number: " + resultRdd2.getNumPartitions());
//        resultRdd.coalesce(resultRdd.getNumPartitions());

        List<Tuple2<Tuple3<String, String, String>, Integer>> output2 = resultRdd2.collect();

        System.out.println("ac = " + ac.value());

        for (Tuple2<?, ?> tuple : output2) {
            System.out.println(tuple._1() + ": " + tuple._2());
        }
        jsc.stop();

        Long end = Instant.now().toEpochMilli();
        System.out.println("cost " + (end - start) / 1000.0 + "seconds");
    }

    public static Integer getIndex(String item) {
        return itemIndexMap.getOrDefault(item, 0);
    }
}
