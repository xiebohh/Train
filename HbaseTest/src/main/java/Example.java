/**
 * Created by Administrator on 2016/12/29.
 */

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

public class Example {

    private static final String CLASS_NAME = Example.class.getSimpleName();

    public static void main(String[] args) {
        if (args.length < 1)
            return;
        String date = args[0];

        try {

            Configuration conf = HBaseConfiguration.create();
            conf.set("hbase.zookeeper.quorum", "all2:2181,all3:2181,all4:2181");
            conf.set("hbase.zookeeper.property.clientPort", "2181");
            conf.set("hbase.master", "all2:60000");
            conf.setBoolean("hbase.cluster.distributed", true);
            conf.setInt("hbase.client.scanner.caching", 10000);
            conf.setInt("ipc.socket.timeout", 10000);

            conf.set("p_date", date);


            Job job = Job.getInstance(conf, CLASS_NAME);
            job.setJarByClass(Example.class);     // class that contains mapper and reducer

            Scan scan = new Scan();
            scan.setCaching(500);        // 1 is the default in Scan, which will be bad for MapReduce jobs
            scan.setCacheBlocks(false);  // don't set to true for MR jobs

            // set other scan attrs
            //scan.addColumn(family, qualifier);
            scan.addColumn("actor".getBytes(), "status".getBytes());
            scan.addColumn("actor".getBytes(), "entryId".getBytes());
            scan.addColumn("actor".getBytes(), "position".getBytes());

            scan.addColumn("event".getBytes(), "payChannel".getBytes());
            scan.addColumn("event".getBytes(), "type".getBytes());
            scan.addColumn("event".getBytes(), "step".getBytes());

            scan.addColumn("recviver".getBytes(), "gindex".getBytes());
            scan.addColumn("recviver".getBytes(), "uid".getBytes());

            scan.addColumn("gift".getBytes(), "id".getBytes());
            scan.addColumn("gift".getBytes(), "goodId".getBytes());
            scan.addColumn("gift".getBytes(), "value".getBytes());

            scan.addColumn("device".getBytes(), "osType".getBytes());
            scan.addColumn("device".getBytes(), "osVer".getBytes());
            scan.addColumn("device".getBytes(), "name".getBytes());
            scan.addColumn("device".getBytes(), "imei".getBytes());
            scan.addColumn("device".getBytes(), "imsi".getBytes());

            scan.addColumn("explorer".getBytes(), "name".getBytes());
            scan.addColumn("explorer".getBytes(), "ver".getBytes());

            scan.addColumn("app".getBytes(), "ver".getBytes());
            scan.addColumn("app".getBytes(), "channel".getBytes());

            scan.addColumn("cli".getBytes(), "addr".getBytes());


            TableMapReduceUtil.initTableMapperJob(
                    "event_" + date,        // input table
                    scan,               // Scan instance to control CF and attribute selection
                    MyMapper.class,     // mapper class
                    Text.class,         // mapper output key
                    Text.class,         // mapper output value
                    job);

            TableMapReduceUtil.initTableReducerJob(
                    "userDaySummary",   // output table
                    MyTableReducer.class,    // reducer class
                    job);

            boolean b = job.waitForCompletion(true);
            if (!b) {
                throw new IOException("error with job!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // KEYIN：表示每一行的偏移量 Object | 主键rowKey ImmutableBytesWritable
    // VALUEIN：表示每一行的内容 Text | 列值 Result
    // KEYOUT：表示每一行中的每个单词 Text | 输出key Text
    // VALUEOUT：表示每一行中每个单词的出现次数，常量为1 IntWritable | 输出value Text
    public static class MyMapper extends TableMapper<Text, Text> {

        private String readData(Result r, String family, String col) {
            if (r.containsColumn(family.getBytes(), col.getBytes())) {
                return new String(r.getValue(family.getBytes(), col.getBytes()));
            } else
                return "";
        }

        /**
         * 将行键的类型强制转化为ImmutableBytesWritable，将value的类型强制转化为Result
         * 对比普通的map方法中key是Object表征的行偏移量，value是Text表征的行的值
         */

        // KEYIN：表示每一行的偏移量 Object | 主键rowKey ImmutableBytesWritable
        // VALUEIN：表示每一行的内容 Text | 列值 Result
        @Override
        public void map(ImmutableBytesWritable row, Result value, Context context) throws IOException, InterruptedException {
            String userId = Bytes.toString(row.get()).split("_")[0];
            if (null == userId || userId.equals(""))
                return;
            String osType = readData(value, "device", "osType");
            // KEYOUT：表示每一行中的每个单词 Text | 新主键 Text
            // VALUEOUT：表示每一行中每个单词的出现次数，常量为1 IntWritable | 统计量 Text
            context.write(new Text("all"), new Text(userId));
            context.write(new Text(osType), new Text(userId));

        }
    }

    /**
     * KEYIN：表示整个文件中的不同单词 Text | 输入key Text
     * VALUEIN：表示整个文件中的不同单词出现的次数 IntWritable | 输入value Text
     * KEYOUT：表示整个文件中的不同单词 Text | 主键rowKey ImmutableBytesWritable
     * VALUEOUT：表示整个文件中的不同单词出现的总次数 IntWritable | Hbase操作 Mutation（Put等操作的弗雷）
     */
    public static class MyTableReducer extends TableReducer<Text, Text, ImmutableBytesWritable> {

        @Override
        /**
         * KEYIN：表示整个文件中的不同单词 Text | 输入key Text
         * VALUEIN：表示整个文件中的不同单词出现的次数 IntWritable | 输入value Iterable<Text>
         */
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

            HashSet<String> set = new HashSet<String>();

            int event = 0;
            String e = "";
            String b = "";
            String row = "";
            String r[] = new String[10];
            // 时间顺序
            //time, values
            Map<String, String> mapTimeSort = new TreeMap();
            for (Text val : values) {
                set.add(val.toString());
            }

            String date = context.getConfiguration().get("p_date");

            Put put3 = new Put(Bytes.toBytes(date));
            put3.addColumn(Bytes.toBytes("day"), Bytes.toBytes(key.toString()), Bytes.toBytes(String.valueOf(set.size())));
            /**
             * KEYOUT：表示整个文件中的不同单词 Text | 主键rowKey ImmutableBytesWritable
             * VALUEOUT：表示整个文件中的不同单词出现的总次数 IntWritable | Hbase操作 Mutation（Put等操作的父类）
             */
            context.write(null, put3);

        }
    }
}
