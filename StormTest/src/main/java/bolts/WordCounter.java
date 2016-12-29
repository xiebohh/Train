package bolts;

/**
 * Created by Administrator on 2016/12/27.
 */

import java.util.HashMap;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.topology.base.BaseRichBolt;

public class WordCounter extends BaseRichBolt {
    Integer id;
    String name;
    Map<String, Integer> counters;
    private OutputCollector collector;

    /**
     * 这个bolts结束时（集群关闭的时候），我们会显示单词数量
     */
    // 5
    @Override
    public void cleanup() {
        System.out.println("WordCounter cleanup");
        System.out.println("-- 单词数 【" + name + "-" + id + "】 --");
        for (Map.Entry<String, Integer> entry : counters.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    /**
     * 为每个单词计数
     */
    // 4
    @Override
    public void execute(Tuple input) {
        System.out.println("WordCounter execute");
        String str = input.getStringByField("word");;
        /**
         * 如果单词尚不存在于map，我们就创建一个，如果已在，我们就为它加1
         */
        if (!counters.containsKey(str)) {
            counters.put(str, 1);
        } else {
            Integer c = counters.get(str) + 1;
            counters.put(str, c);
        }
        //对元组作为应答
        collector.ack(input);
    }

    /**
     * 初始化
     */
    // 3
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        System.out.println("WordCounter prepare");
        this.counters = new HashMap<String, Integer>();
        this.collector = collector;
        this.name = context.getThisComponentId();
        this.id = context.getThisTaskId();
    }

    // 2
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        System.out.println("WordCounter declareOutputFields");
    }

    // 1
    @Override
    public Map<String, Object> getComponentConfiguration() {
        System.out.println("WordCounter getComponentConfiguration");
        return null;
    }
}