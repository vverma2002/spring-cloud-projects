package com.vik;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class SpringCloudDiscoveryEurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudDiscoveryEurekaServerApplication.class, args);
	}

}
