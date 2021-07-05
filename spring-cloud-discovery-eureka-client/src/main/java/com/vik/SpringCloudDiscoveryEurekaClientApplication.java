package com.vik;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

@SpringBootApplication
@RestController
public class SpringCloudDiscoveryEurekaClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudDiscoveryEurekaClientApplication.class, args);
	}

	static final String REMOTE_SERVICE = "eureka-service-abc";

	@Value("${spring.application.name}")
	private String aplicationName;

	@Value("${server.port}")
	private String serverPort;

	@Value("${spring.profiles.active:default}")
	private String activeProfile;

	@Autowired
	private EurekaClient eurekaClient;

	@Autowired
	private DiscoveryClient discoveryClient;

	@Autowired
	@Lazy(value = false)
	private RestTemplateBuilder restTemplateBuilder;

	@GetMapping("/")
	public HashMap<String, Object> get() {
		HashMap<String, Object> map = new LinkedHashMap<>();
		map.put("Concept", "Spring Eureka Client");
		map.put("Application Name", aplicationName);
		map.put("ServerPort", serverPort);
		map.put("ActiveProfile", activeProfile);
		map.put("Actuator", "http://localhost:" + serverPort + "/actuator");
		map.put("----------", "----------");
		map.put("/greetings", "http://localhost:" + serverPort + "/greetings");
		map.put("/service-instances/{applicationName1}",
				"http://localhost:" + serverPort + "/service-instances/" + aplicationName);
		map.put("/service-instances/{applicationName2}",
				"http://localhost:" + serverPort + "/service-instances/" + REMOTE_SERVICE);
		map.put("-----------", "-----------");
		map.put("via discoveryClient", "http://localhost:" + serverPort + "/discoveryClient");
		map.put("via eurekaClient", "http://localhost:" + serverPort + "/eurekaClient");
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

	@GetMapping("/service-instances/{applicationName}")
	public List<ServiceInstance> serviceInstancesByApplicationName(@PathVariable String applicationName) {
		return this.discoveryClient.getInstances(applicationName);
	}

	@GetMapping("/discoveryClient")
	public String serviceDiscoveryClient() {

		String serviceBaseUrl = "...";
		List<ServiceInstance> list = discoveryClient.getInstances(REMOTE_SERVICE);
		if (list != null && list.size() > 0) {
			serviceBaseUrl = list.get(0).getUri().toString();
		}
		System.out.printf("serviceBaseUrl : {%s}\n", serviceBaseUrl);

		RestTemplate restTemplate = restTemplateBuilder.build();
		return restTemplate.getForObject(serviceBaseUrl + "/{link}?name={name}", String.class, "greetings",
				"discoveryClient");
	}

	@GetMapping("/eurekaClient")
	public String serviceEurekaClient() {

//		InstanceInfo instanceInfo = eurekaClient.getApplication(REMOTE_SERVICE).getInstances().get(0);
		InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka(REMOTE_SERVICE, false);
		String serviceBaseUrl = instanceInfo.getHomePageUrl();
		System.out.printf("serviceBaseUrl : {%s}\n", serviceBaseUrl);

		RestTemplate restTemplate = restTemplateBuilder.build();
		return restTemplate.getForObject(serviceBaseUrl + "/{link}?name={name}", String.class, "greetings",
				"eurekaClient");
	}

}
