package bolts;

/**
 * Created by Administrator on 2016/12/27.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.topology.base.BaseRichBolt;

public class WordNormalizer extends BaseRichBolt {
    private OutputCollector collector;

    // 3
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        System.out.println("WordNormalizer prepare");
        this.collector = collector;
    }

    /**
     * *bolt*从单词文件接收到文本行，并标准化它。
     * 文本行会全部转化成小写，并切分它，从中得到所有单词。
     */
    // 4
    @Override
    public void execute(Tuple input) {
        System.out.println("WordNormalizer execute");
        String sentence = input.getStringByField("sentence");
        String[] words = sentence.split(" ");
        for (String word : words) {
            word = word.trim();
            if (!word.isEmpty()) {
                word = word.toLowerCase();
                collector.emit(new Values(word));
            }
        }
        //对元组做出应答
        collector.ack(input);
    }

    // 5
    @Override
    public void cleanup() {
        System.out.println("WordNormalizer cleanup");
    }


    /**
     * 这个*bolt*只会发布“word”域
     */
    // 2
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        System.out.println("WordNormalizer declareOutputFields");
        declarer.declare(new Fields("word"));
    }

    // 1
    @Override
    public Map<String, Object> getComponentConfiguration() {
        System.out.println("WordNormalizer getComponentConfiguration");
        return null;
    }
}
