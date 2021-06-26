package com.vik;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

@SpringBootApplication
@RestController
@EnableFeignClients
public class EurekaFeignClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaFeignClientApplication.class, args);
	}

	@Autowired
	private GreetingClient greetingClient;

	@RequestMapping("/get-greeting-withFeign")
	public String greetingWithFeign() {
		return greetingClient.greeting();
	}

	@Autowired
	@Lazy
	private EurekaClient eurekaClient;

	@RequestMapping("/withoutFeign")
	public String greetingWithoutFeign() {

		InstanceInfo service = eurekaClient.getApplication("spring-cloud-eureka-client").getInstances().get(0);

		String hostName = service.getHostName();
		int port = service.getPort();
		return String.format("Greeting Without Feign from host : %s, port : %s", hostName, port);
	}

}

@FeignClient("spring-cloud-eureka-client")
interface GreetingClient {
	@RequestMapping("/greeting?name=Vikrant")
	String greeting();
}
