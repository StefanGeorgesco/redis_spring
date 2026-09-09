package fr.stefangeorgesco.redisspring;

import org.springframework.boot.SpringApplication;

public class TestRedisSpringApplication {

    public static void main(String[] args) {
        SpringApplication.from(RedisSpringApplication::main).with(TestcontainersConfiguration.class).run(args);
    }
}
