package com.ams.wc;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.shaded.netty4.io.netty.util.internal.ResourcesUtil;
import org.apache.flink.util.Collector;

import java.io.File;

/**
 * @author Cody
 */
public class BatchJob {

    public static void main(String[] args) throws Exception {
        File file = ResourcesUtil.getFile(BatchJob.class, "/wordcount.txt");
        //加载环境
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<String> text = env.readTextFile(file.getAbsolutePath());
        DataSet<Tuple2<String, Integer>> counts = text.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
                // \W+  数字、字母、下划线的多个字符
                String[] tokens = value.toLowerCase().split("\\W+");
                for (String token : tokens) {
                    if (token.length() > 0) {
                        out.collect(new Tuple2<>(token, 1));
                    }
                }
            }
        }).groupBy(0).sum(1);
        counts.print();
    }
}