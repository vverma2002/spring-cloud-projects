package com.vik;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

@SpringBootApplication
@RestController
public class EurekaClientApplication {

	@Autowired
	@Lazy
	private EurekaClient eurekaClient;

	@Autowired
	@Lazy
	private DiscoveryClient discoveryClient;

	public static void main(String[] args) {
		SpringApplication.run(EurekaClientApplication.class, args);
	}

	@GetMapping("/greeting")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

	@GetMapping("/eurekaClient")
	public String serviceEurekaClient() {
		InstanceInfo instance = eurekaClient.getNextServerFromEureka("spring-cloud-eureka-client", false);
		System.out.printf("instance : %s", instance);
		return instance.getHomePageUrl();
	}

	@GetMapping("/discoveryClient")
	public String serviceDiscoveryClient() {
		List<ServiceInstance> list = discoveryClient.getInstances("spring-cloud-eureka-client");
		if (list != null && list.size() > 0) {
			return list.get(0).getUri().toString();
		}
		return null;
	}

}
