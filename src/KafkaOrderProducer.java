/*
package com.k2data.qinghai.kafka.producer;

import com.k2data.qinghai.kafka.conf.Config;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

//内存中缓存一个文件的秒级数据，可设置基于原有时间增加多少天，发送至kafka

public class KafkaOrderProducer {

    private static Logger logger = LoggerFactory.getLogger(KafkaOrderProducer.class);

    private Config config;

    private Producer<String, String> producer;

    public KafkaOrderProducer(Config config) {
        this.config = config;

        Properties props = new Properties();

        logger.debug("kafka nodes: {}", config.KAFKA_NODES);
        props.put("metadata.broker.list", config.KAFKA_NODES);

        props.put("serializer.class", config.SERIALIZER_CLASS);

        props.put("key.serializer.class", config.SERIALIZER_CLASS);

        props.put("partitioner.class", "com.k2data.qinghai.kafka.producer.MyPartitioner");

        props.put("request.required.acks", "-1");

        logger.info("Basicfile data dir: {}", config.BASIC_FILE);

        //Producer instance
        producer = new Producer<>(new ProducerConfig(props));
    }

    private void produce() {
        //read file
        try {
            long lineNum = 0;
            List<String> allLines = new ArrayList<>();
                File dirFile = new File(config.BASIC_FILE);
                if (!dirFile.exists()) {
                    logger.error(config.BASIC_FILE + " do not exit");
                    return;
                }
                BufferedReader reader = new BufferedReader(new FileReader(dirFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("realtimedata")) {
                        allLines.add(line);
                    }
                }
                reader.close();


            for (int i = config.LOOP_START; i < config.LOOP_STOP; i++) {
                for (String oldLine : allLines) {
                    try {
                        String deviceId = oldLine.split("\\|")[3];
                        String newLine = replaceRealdataTime(oldLine, i);
                        if (newLine != null) {
                            producer.send(new KeyedMessage<>(config.KAFKA_TOPIC, deviceId, newLine));
                            lineNum++;
                        }
                    } catch (Exception ignored) {
                    }
                }
                logger.info("generate {} day which has {} lines", i, allLines.size());
            }

            logger.info("total generate {} lines", lineNum);
            System.exit(0);
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private String replaceRealdataTime(String line, int i) {
        String[] items = line.split("\\|");
        String[] fields = items[4].split(",");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date;
        try {
            date = simpleDateFormat.parse(fields[0]);
            long oldTime = date.getTime();    ////************************
            long newTime = oldTime + 86400000 * i;
            Date newDate = new Date(newTime);
            String newDateField = simpleDateFormat.format(newDate);
            fields[0] = newDateField;
            String newFields = String.join(",", fields);
            items[4] = newFields;
            return String.join("|", items);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        Config config;
        if (args.length > 0) {
            try {
                FileInputStream fileInputStream = new FileInputStream(args[0]);
                config = new Config(fileInputStream);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Load config from {} failed, using default config", args[0]);
                config = new Config();
            }
        } else {
            config = new Config();
        }

        new KafkaOrderProducer(config).produce();
    }

}
*/
