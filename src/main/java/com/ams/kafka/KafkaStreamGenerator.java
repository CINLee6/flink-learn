package com.ams.kafka;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

/**
 * Copyright (C), 2019-2022, 中冶赛迪重庆信息技术有限公司
 * Description: KafkaStreamGenerator
 *
 * @author xin.d.li
 * @date 2022-04-18 16:58
 */
public class KafkaStreamGenerator {

    //定义
    // KafkaSource => Map => Window => Avg => Sink(Printf)
    //
    
    public FlinkKafkaConsumer<String> buildKafkaSource(String server, String topic) {
        //Kafka Config
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "10.73.13.53:9092,10.73.13.54:9092,10.73.13.55:9092");
        properties.setProperty("group.id", "flink_dev");
        properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("auto.offset.reset", "latest");
        return new FlinkKafkaConsumer<String>("flink_dev", new SimpleStringSchema(), properties);
    }
}
