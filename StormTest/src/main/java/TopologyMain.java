/**
 * Created by Administrator on 2016/12/27.
 */

import spouts.WordReader;
import bolts.WordCounter;
import bolts.WordNormalizer;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;


public class TopologyMain {
    public static void main(String[] args) throws InterruptedException {
        //定义拓扑
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("word-reader", new WordReader());
        builder.setBolt("word-normalizer", new WordNormalizer()).shuffleGrouping("word-reader");
        builder.setBolt("word-counter", new WordCounter(), 2).fieldsGrouping("word-normalizer", new Fields("word"));

        //配置
        Config conf = new Config();
        conf.put("wordsFile", "/words.txt");
        conf.setDebug(false);

        //运行拓扑
        conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("Getting-Started-Topologie", conf, builder.createTopology());
        Thread.sleep(50000);
        cluster.killTopology("Getting-Started-Topologie");
        cluster.shutdown();
    }
}
