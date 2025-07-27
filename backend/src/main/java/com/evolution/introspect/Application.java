package com.evolution.introspect;

/**
 * @author zzz
 * @date 2025/7/24-19:05
 * @desc 启动类
 */

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.evolution.introspect")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
