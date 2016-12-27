package spouts;

/**
 * Created by Administrator on 2016/12/27.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class WordReader implements IRichSpout {
    private SpoutOutputCollector collector;
    private FileReader fileReader;
    private boolean completed = false;
    private TopologyContext context;

    /**
     * 第一个被调用的spout方法都是public void open(Map conf, TopologyContext context, SpoutOutputCollector collector)。
     * 它接收如下参数：
     * Map配置对象，在定义topology对象时创建；
     * TopologyContext对象，包含所有拓扑数据；
     * SpoutOutputCollector对象，它能让我们发布交给bolts处理的数据。
     * 我们将创建一个文件并维持一个collector对象
     */
    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        System.out.println("WordReader open");
        try {
            this.context = context;
            System.out.println(conf.get("wordsFile"));
            this.fileReader = new FileReader(conf.get("wordsFile").toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error reading file [" + conf.get("wordsFile") + "]");
        }
        this.collector = collector;
    }

    /**
     * 要通过它向bolts发布待处理的数据。在这个例子里，这个方法要读取文件并逐行发布数据。
     */
    @Override
    public void nextTuple() {
        System.out.println("WordReader nextTuple");
        /**
         * 这个方法会不断的被调用，直到整个文件都读完了，我们将等待并返回。
         */
        if (completed) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //什么也不做
            }
            return;
        }
        String str;
        //创建reader
        BufferedReader reader = new BufferedReader(fileReader);
        try {
            //读所有文本行
            while ((str = reader.readLine()) != null) {
                /**
                 * 按行发布一个新值
                 */
                collector.emit(new Values(str), str);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error reading tuple", e);
        } finally {
            completed = true;
        }
    }

    public boolean isDistributed() {
        return false;
    }


    @Override
    public void close() {
        System.out.println("WordReader nextTuple");
    }

    @Override
    public void activate() {
        System.out.println("WordReader activate");

    }

    @Override
    public void deactivate() {
        System.out.println("WordReader deactivate");

    }

    @Override
    public void ack(Object msgId) {
        System.out.println("WordReader ack");
        System.out.println("OK:" + msgId);
    }

    @Override
    public void fail(Object msgId) {
        System.out.println("WordReader fail");
        System.out.println("FAIL:" + msgId);
    }


    /**
     * 声明输出域"line"
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        System.out.println("WordReader declareOutputFields");
        declarer.declare(new Fields("line"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        System.out.println("WordReader getComponentConfiguration");
        return null;
    }
}
