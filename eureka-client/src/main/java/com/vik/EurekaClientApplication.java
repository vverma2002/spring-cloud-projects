package com.vik;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

@SpringBootApplication
@RestController
public class EurekaClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaClientApplication.class, args);
	}

	@Autowired
	private EurekaClient eurekaClient;

	@Autowired
	private DiscoveryClient discoveryClient;

	@Autowired
	@Lazy(value = false)
	private RestTemplateBuilder restTemplateBuilder;

	@GetMapping("/")
	public String invokeService() {
		RestTemplate restTemplate = restTemplateBuilder.build();
		InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka("spring-cloud-eureka-client", false);
		String serviceBaseUrl = instanceInfo.getHomePageUrl();
		System.out.printf("serviceBaseUrl : {}", serviceBaseUrl);
		return restTemplate.getForObject(serviceBaseUrl + "/{link}?name={name}", String.class, "greeting", "Vikrant");
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
