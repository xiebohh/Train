/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;
import java.util.StringTokenizer;

public class WordCount {

    public static void main(String[] args) throws Exception {
        /**
         * 运行mapreduce程序前都要初始化Configuration，该类主要是读取mapreduce系统配置信息，
         * 这些信息包括hdfs还有mapreduce，也就是安装hadoop时候的配置文件例如：core-site.xml、
         * hdfs-site.xml和mapred-site.xml等等文件里的信息，有些童鞋不理解为啥要这么做，
         * 这个是没有深入思考mapreduce计算框架造成，我们程序员开发mapreduce时候只是在填空，
         * 在map函数和reduce函数里编写实际进行的业务逻辑，其它的工作都是交给mapreduce框架自己操作的，
         * 但是至少我们要告诉它怎么操作啊，比如hdfs在哪里啊，mapreduce的jobstracker在哪里啊，而这些信息就在conf包下的配置文件里。
         */
        Configuration conf = new Configuration();
        /**
         * If的语句好理解，就是运行WordCount程序时候一定是两个参数，如果不是就会报错退出。
         * 至于第一句里的GenericOptionsParser类，它是用来解释常用hadoop命令，并根据需要为Configuration对象设置相应的值，
         * 其实平时开发里我们不太常用它，而是让类实现Tool接口，然后再main函数里使用ToolRunner运行程序，
         * 而ToolRunner内部会调用GenericOptionsParser。
         */
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length < 2) {
            System.err.println("Usage: wordcount <in> [<in>...] <out>");
            System.exit(2);
        }
        /**
         * 第一行就是在构建一个job，在mapreduce框架里一个mapreduce任务也叫mapreduce作业也叫做一个mapreduce的job，
         * 而具体的map和reduce运算就是task了，这里我们构建一个job，构建时候有两个参数，
         * 一个是conf这个就不累述了，一个是这个job的名称。
         */
        Job job = Job.getInstance(conf, "word count");
        /**
         * 第二行就是装载程序员编写好的计算程序，例如我们的程序类名就是WordCount了。
         * 这里我要做下纠正，虽然我们编写mapreduce程序只需要实现map函数和reduce函数，但是实际开发我们要实现三个类，
         * 第三个类是为了配置mapreduce如何运行map和reduce函数，准确的说就是构建一个mapreduce能执行的job了，例如WordCount类。
         */
        job.setJarByClass(WordCount.class);
        /**
         * 第三行和第五行就是装载map函数和reduce函数实现类了，这里多了个第四行，这个是装载Combiner类，
         * 这个我后面讲mapreduce运行机制时候会讲述，其实本例去掉第四行也没有关系，但是使用了第四行理论上运行效率会更好。
         */
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);
        /**
         * 这个是定义输出的key/value的类型，也就是最终存储在hdfs上结果文件的key/value的类型。
         */
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        /**
         * 第一行就是构建输入的数据文件，第二行是构建输出的数据文件，最后一行如果job运行成功了，我们的程序就会正常退出。
         * FileInputFormat和FileOutputFormat是很有学问的，我会在下面的mapreduce运行机制里讲解到它们。
         */
        for (int i = 0; i < otherArgs.length - 1; ++i) {
            FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
        }
        FileOutputFormat.setOutputPath(job,
                new Path(otherArgs[otherArgs.length - 1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

    // KEYIN：表示每一行的偏移量 Object
    // VALUEIN：表示每一行的内容 Text
    // KEYOUT：表示每一行中的每个单词 Text
    // VALUEOUT：表示每一行中每个单词的出现次数，常量为1 IntWritable
    // 继承Mapper类实现map方法
    public static class TokenizerMapper
            extends Mapper<Object, Text, Text, IntWritable> {

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        /**
         * 这里有三个参数，前面两个Object key, Text value就是输入的key和value，
         * 第三个参数Context context这是可以记录输入的key和value，
         * 例如：context.write(word, one);此外context还会记录map运算的状态。
         */
        /**
         * 源文件会被解析成2个键值对，分别为<0,hello you >,<10,hello mavs>
         * 每个<k,v>都调用一次函数
         */
        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString());
            while (itr.hasMoreTokens()) {
                word.set(itr.nextToken());
                context.write(word, one);
            }
        }
    }

    /**
     * // KEYIN：表示整个文件中的不同单词 Text
     * // VALUEIN：表示整个文件中的不同单词出现的次数 IntWritable
     * // KEYOUT：表示整个文件中的不同单词 Text
     * // VALUEOUT：表示整个文件中的不同单词出现的总次数 IntWritable
     // 继承Reducer类实现reduce方法
     */
    public static class IntSumReducer
            extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();

        /**
         * reduce函数的输入也是一个key/value的形式，不过它的value是一个迭代器的形式Iterable<IntWritable> values，
         * 也就是说reduce的输入是一个key对应一组的值的value，reduce也有context和map的context作用一致。
         *
         * /
     *  / **
         *  reduce会被调用3次，分别是<hello,{1,1}>、<mavs,{1}>、<you,{1}>
         */
        /**
         * // KEYIN：表示整个文件中的不同单词 Text
         * // VALUEIN：表示整个文件中的不同单词出现的次数 Iterable<IntWritable>
         * // KEYOUT：表示整个文件中的不同单词
         * // VALUEOUT：表示整个文件中的不同单词出现的总次数
         // 继承Reducer类实现reduce方法
         */
        public void reduce(Text key, Iterable<IntWritable> values,
                           Context context
        ) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }
}
