package com.ams.stream;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * @author lixinm
 */
public class StreamingJob {

    public static void main(String[] args) throws Exception {
        ParameterTool params = ParameterTool.fromArgs(args);
        String host = params.get("host") == null ? "localhost" : params.get("host");
        int port = Integer.parseInt(params.get("port") == null ? "8989" : params.get("port"));

        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> dataStream = env.socketTextStream(host, port, "\n");

        DataStream<Tuple2<String, Integer>> ds = dataStream.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
                //空白字符分隔
                String[] tokens = value.split("\\s");
                for (String token : tokens) {
                    if (token.length() > 0) {
                        out.collect(new Tuple2<>(token, 1));
                    }
                }
            }
        }).keyBy(0).sum(1);
        ds.print().setParallelism(1);
        // execute program
        env.execute("Flink Streaming Java API Skeleton");
    }
}