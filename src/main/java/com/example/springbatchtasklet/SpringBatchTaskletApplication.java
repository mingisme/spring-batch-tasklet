package com.example.springbatchtasklet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SpringBatchTaskletApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchTaskletApplication.class, args);
	}

}
