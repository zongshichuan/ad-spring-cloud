package com.chuan.ad.sender.kafka;

import com.alibaba.fastjson.JSON;
import com.chuan.ad.dto.MySqlRowData;
import com.chuan.ad.sender.ISender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 监听到Binlog增量数据, 数据投递(把增量数据添加到kafka 中)
 */
@Component
@Slf4j
public class KafkaSender implements ISender {

    @Value("${adconf.kafka.topic}")
    private String topic;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaSender(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sender(MySqlRowData rowData) {
        log.info("<<<  binlog kafka service send MySqlRowData:{}  >>>",rowData.toString());
        kafkaTemplate.send(
                topic, JSON.toJSONString(rowData)
        );
    }
}
