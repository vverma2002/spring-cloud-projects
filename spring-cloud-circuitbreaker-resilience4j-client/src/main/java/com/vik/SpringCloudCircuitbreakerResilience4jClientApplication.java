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
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@RestController
public class SpringCloudCircuitbreakerResilience4jClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudCircuitbreakerResilience4jClientApplication.class, args);
	}

	@Value("${spring.application.name}")
	private String aplicationName;

	@Value("${server.port}")
	private String serverPort;

	@Value("${spring.profiles.active:default}")
	private String activeProfile;

	@Autowired
	private SomeService someService;

	@GetMapping("/")
	public HashMap<String, Object> get() {
		HashMap<String, Object> map = new LinkedHashMap<>();
		map.put("Concept", "Spring Resilience4j Client");
		map.put("Application Name", aplicationName);
		map.put("ServerPort", serverPort);
		map.put("ActiveProfile", activeProfile);
		map.put("Actuator", "http://localhost:" + serverPort + "/actuator");
		map.put("----------", "----------");
		map.put("ServiceAbc /greetings",
				"http://localhost:" + serverPort + "/greetings?name=client&serviceName=eureka-service-abc");
		map.put("ServiceDef /greetings",
				"http://localhost:" + serverPort + "/greetings?name=client&serviceName=eureka-service-def");
		map.put("-----------", "-----------");
		map.put("ServiceAbc /greetingsCircuitBreaker", "http://localhost:" + serverPort
				+ "/greetingsCircuitBreaker?name=Resilience4j&serviceName=eureka-service-abc");
		map.put("ServiceDef /greetingsCircuitBreaker", "http://localhost:" + serverPort
				+ "/greetingsCircuitBreaker?name=Resilience4j&serviceName=eureka-service-def");
		return map;
	}

	@GetMapping("/greetings")
	public String greetings(@RequestParam(value = "name", defaultValue = "World") String name,
			@RequestParam(value = "serviceName") String serviceName) {
		return someService.getRemoteGreetings(name, serviceName);
	}

	@GetMapping("/greetingsCircuitBreaker")
	public String greetingsCircuitBreaker(@RequestParam(value = "name", defaultValue = "World") String name,
			@RequestParam(value = "serviceName") String serviceName) {
		return someService.getCircuitBreakerRemoteGreetings(name, serviceName);
	}
}

@Service
class SomeService {

	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	@Autowired
	private DiscoveryClient discoveryClient;

	@SuppressWarnings("rawtypes")
	@Autowired
	private CircuitBreakerFactory circuitBreakerFactory;

	public String getRemoteGreetings(String name, String serviceName) {

		String serviceBaseUrl = "...";
		List<ServiceInstance> list = discoveryClient.getInstances(serviceName);
		if (list != null && list.size() > 0) {
			serviceBaseUrl = list.get(0).getUri().toString();
		}
		System.out.printf("serviceBaseUrl : {%s}\n", serviceBaseUrl);

		RestTemplate restTemplate = restTemplateBuilder.build();

		return restTemplate.getForObject(serviceBaseUrl + "/{link}?name={name}", String.class, "greetings", name);
	}

	public String getCircuitBreakerRemoteGreetings(String name, String serviceName) {

		CircuitBreaker circuitBreaker = circuitBreakerFactory.create("backendB");

		return circuitBreaker.run(() -> {

			String serviceBaseUrl = "...";
			List<ServiceInstance> list = discoveryClient.getInstances(serviceName);
			if (list != null && list.size() > 0) {
				serviceBaseUrl = list.get(0).getUri().toString();
			}
			System.out.printf("serviceBaseUrl : {%s}\n", serviceBaseUrl);

			RestTemplate restTemplate = restTemplateBuilder.build();

			return restTemplate.getForObject(serviceBaseUrl + "/{link}?name={name}", String.class, "greetings", name);

		}, throwable -> getDefaultAlbumList(throwable, name, serviceName));

	}

	private String getDefaultAlbumList(Throwable throwable, String name, String serviceName) {
		return String.format("Sorry %s, %s is not accessible because of %s", name, serviceName, throwable);
	}

}
