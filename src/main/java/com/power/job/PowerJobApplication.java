package com.power.job;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * JOB程序入口
 *
 * @author lzf
 */
@MapperScan("com.power.**.mapper")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.power"})
@EnableScheduling
public class PowerJobApplication {
    public static void main(String[] args) {
        SpringApplication.run(PowerJobApplication.class, args);
        System.out.println("http://127.0.0.1:9527");
    }
}
