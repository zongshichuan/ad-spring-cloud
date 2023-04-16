package com.chuan.ad.runner;

import com.chuan.ad.mysql.BinlogClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * CommandLineRunner
 *
 *CommandLineRunner和ApplicationRunner接口的run()方法在SpringApplication完成启动时执行。启动完成之后，应用开始运行。
 * CommandLineRunner和ApplicationRunner的作用是在程序开始运行前执行任务或记录信息。
 *
 *
 * 实现当应用程序启动后对Binlog的监听
 */
@Slf4j
@Component
public class BinlogRunner implements CommandLineRunner {

    private final BinlogClient client;

    @Autowired
    public BinlogRunner(BinlogClient client) {
        this.client = client;
    }

    @Override
    public void run(String... strings) throws Exception {

        log.info("Coming in BinlogRunner...");
        client.connect();
    }
}
