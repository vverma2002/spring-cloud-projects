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

	static final String REMOTE_SERVICE_ABC = "eureka-service-abc";
	static final String REMOTE_SERVICE_DEF = "eureka-service-def";

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
		map.put("/service-instances/{applicationName2}",
				"http://localhost:" + serverPort + "/service-instances/" + REMOTE_SERVICE_ABC);
		map.put("/service-instances/{applicationName3}",
				"http://localhost:" + serverPort + "/service-instances/" + REMOTE_SERVICE_DEF);
		map.put("-----------", "-----------");
		map.put("eureka-service-abc via discoveryClient",
				"http://localhost:" + serverPort + "/discoveryClient/" + REMOTE_SERVICE_ABC);
		map.put("eureka-service-abc via eurekaClient",
				"http://localhost:" + serverPort + "/eurekaClient/" + REMOTE_SERVICE_ABC);
		map.put("------------", "------------");
		map.put("eureka-service-def via discoveryClient",
				"http://localhost:" + serverPort + "/discoveryClient/" + REMOTE_SERVICE_DEF);
		map.put("eureka-service-def via eurekaClient",
				"http://localhost:" + serverPort + "/eurekaClient/" + REMOTE_SERVICE_DEF);
		return map;
	}

	@GetMapping("/service-instances/{applicationName}")
	public List<ServiceInstance> serviceInstancesByApplicationName(@PathVariable String applicationName) {
		return this.discoveryClient.getInstances(applicationName);
	}

	@GetMapping("/discoveryClient/{serviceName}")
	public String serviceDiscoveryClient(@PathVariable String serviceName) {

		String serviceBaseUrl = "...";
		List<ServiceInstance> list = discoveryClient.getInstances(serviceName);
		if (list != null && list.size() > 0) {
			serviceBaseUrl = list.get(0).getUri().toString();
		}
		System.out.printf("serviceBaseUrl : {%s}\n", serviceBaseUrl);

		RestTemplate restTemplate = restTemplateBuilder.build();
		return restTemplate.getForObject(serviceBaseUrl + "/{link}?name={name}", String.class, "greetings",
				"discoveryClient");
	}

	@GetMapping("/eurekaClient/{serviceName}")
	public String serviceEurekaClient(@PathVariable String serviceName) {

//		InstanceInfo instanceInfo = eurekaClient.getApplication(REMOTE_SERVICE).getInstances().get(0);
		InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka(serviceName, false);
		String serviceBaseUrl = instanceInfo.getHomePageUrl();
		System.out.printf("serviceBaseUrl : {%s}\n", serviceBaseUrl);

		RestTemplate restTemplate = restTemplateBuilder.build();
		return restTemplate.getForObject(serviceBaseUrl + "/{link}?name={name}", String.class, "greetings",
				"eurekaClient");
	}

}
