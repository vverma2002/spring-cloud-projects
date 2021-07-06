package com.vik;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SpringCloudDiscoveryEurekaServiceAbcApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudDiscoveryEurekaServiceAbcApplication.class, args);
	}

	@Value("${spring.application.name}")
	private String aplicationName;

	@Value("${server.port}")
	private String serverPort;

	@Value("${spring.profiles.active:default}")
	private String activeProfile;

	@Autowired
	private DiscoveryClient discoveryClient;

	@GetMapping("/")
	public HashMap<String, Object> get() {
		HashMap<String, Object> map = new LinkedHashMap<>();
		map.put("Concept", "Spring Eureka Service ABC");
		map.put("Application Name", aplicationName);
		map.put("ServerPort", serverPort);
		map.put("ActiveProfile", activeProfile);
		map.put("Actuator", "http://localhost:" + serverPort + "/actuator");
		map.put("----------", "----------");
		map.put("/greetings", "http://localhost:" + serverPort + "/greetings");
		map.put("/service-instances/{applicationName}",
				"http://localhost:" + serverPort + "/service-instances/" + aplicationName);
		map.put("/serviceAbc/fetchData", "http://localhost:" + serverPort + "/serviceAbc/fetchData");
		return map;
	}

	@GetMapping("/greetings")
	public String greetings(@RequestParam(value = "name", defaultValue = "World") String name) {

		String host = "...";
		List<ServiceInstance> list = discoveryClient.getInstances(aplicationName);
		if (list != null && list.size() > 0) {
			host = list.get(0).getUri().toString();
		}

		return String.format("Hello %s! From %s, Hosted at %s", name, aplicationName, host);
	}

	@GetMapping("/serviceAbc/fetchData")
	public String fetchData() {
		return greetings("ServiceAbc Fetched Data");
	}

	@GetMapping("/service-instances/{applicationName}")
	public List<ServiceInstance> serviceInstancesByApplicationName(@PathVariable String applicationName) {
		return this.discoveryClient.getInstances(applicationName);
	}

}
