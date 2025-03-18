package com.gennerator.mingzi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.gennerator.mingzi.dao")
public class MingziGenneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MingziGenneratorApplication.class, args);
    }

}
