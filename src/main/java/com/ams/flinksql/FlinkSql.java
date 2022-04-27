package com.ams.flinksql;

import java.io.File;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.MapOperator;
import org.apache.flink.shaded.netty4.io.netty.util.internal.ResourcesUtil;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;

public class FlinkSql {

    public static void main(String[] args) throws Exception {
        File file = ResourcesUtil.getFile(FlinkSql.class, "/tb.csv");
        ExecutionEnvironment env =  ExecutionEnvironment.getExecutionEnvironment();
        DataSet<String> ins = env.readTextFile(file.getAbsolutePath());
        MapOperator<String, Human> map = ins.map(new MapFunction<String, Human>() {
            @Override
            public Human map(String value) throws Exception {
                String[] split = value.split(",");
                return new Human(split[0], split[1]);
            }
        });
        map.print();

        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .inStreamingMode()
                .build();

        TableEnvironment tEnv = TableEnvironment.create(settings);

    }

}
