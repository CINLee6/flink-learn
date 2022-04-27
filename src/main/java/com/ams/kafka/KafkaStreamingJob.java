package com.ams.kafka;

import com.ams.utils.JsonUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.dag.Pipeline;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.AllWindowedStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author lixinm
 */
public class KafkaStreamingJob {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //env.addOperator();


        //Kafka Config
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "10.73.13.53:9092,10.73.13.54:9092,10.73.13.55:9092");
        properties.setProperty("group.id", "flink_dev");
        properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("auto.offset.reset", "latest");

        //Pipeline
        //Source
        DataStream<String> kafkaSource = env.addSource(new FlinkKafkaConsumer<String>("flink_dev", new SimpleStringSchema(), properties));
        //{"id":1, "name":"Temp1", "value":3000, "timestamp":0L}
        //{"id":2, "name":"Temp2", "value":3001, "timestamp":0L}
        //{"id":3, "name":"Temp3", "value":3002, "timestamp":0L}

        //Translate
        SingleOutputStreamOperator<Tuple2<Integer, Integer>> map = kafkaSource.map(new MapFunction<String, Tuple2<Integer, Integer>>() {
            @Override
            public Tuple2<Integer, Integer> map(String value) throws Exception {
                final Map<Integer, String> vMap = JsonUtils.stringToMap(value, Integer.class, String.class);
                return Tuple2.of(Integer.valueOf(vMap.get("id")), Integer.valueOf(vMap.get("value")));
            }
        });
        KeyedStream<Tuple2<Integer, Integer>, Tuple> keyedStream = map.keyBy(0);
        AllWindowedStream<Tuple2<Integer, Integer>, TimeWindow> windowAllWindowedStream = keyedStream.timeWindowAll(Time.seconds(10));
        SingleOutputStreamOperator<Double> process = windowAllWindowedStream.process(new AvgFunc());

        //Sink
        process.print();

        env.execute("FlinkKafkaJob");
    }

    public static class AvgFunc extends ProcessAllWindowFunction<Tuple2<Integer, Integer>, Double, TimeWindow> {

        @Override
        public void process(ProcessAllWindowFunction<Tuple2<Integer, Integer>, Double, TimeWindow>.Context context, Iterable<Tuple2<Integer, Integer>> elements, Collector<Double> out) throws Exception {
            AtomicInteger size = new AtomicInteger();
            AtomicReference<Double> sum = new AtomicReference<>(0.0);
            elements.forEach(item -> {
                size.getAndIncrement();
                sum.updateAndGet(v -> v + item.f1);
            });
            out.collect(sum.get() / size.get());
        }
    }
}