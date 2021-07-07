package com.vik;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableEurekaClient
public class SpringCloudRoutingGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudRoutingGatewayApplication.class, args);
	}

	@Value("${spring.application.name}")
	private String aplicationName;

	@Value("${server.port}")
	private String serverPort;

	@Value("${spring.profiles.active:default}")
	private String activeProfile;

	@GetMapping("/")
	public HashMap<String, Object> get() {
		HashMap<String, Object> map = new LinkedHashMap<>();
		map.put("Concept", "Spring Gateway");
		map.put("Application Name", aplicationName);
		map.put("ServerPort", serverPort);
		map.put("ActiveProfile", activeProfile);
		map.put("Actuator", "http://localhost:" + serverPort + "/actuator");
		map.put("----------", "----------");
		map.put("/serviceAbc/greetings", "http://localhost:" + serverPort + "/serviceAbc/greetings");
		map.put("/serviceDef/greetings", "http://localhost:" + serverPort + "/serviceDef/greetings");
		return map;
	}
}

@Configuration
class SpringCloudConfig {

	@Bean
	public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
                .route(r -> r.path("/serviceAbc/**")
            			.filters(f -> f.rewritePath("/serviceAbc(?<path>/?.*)", "$\\{path}")
            					       .addRequestHeader("first-request", "first-request-header")
								       .addResponseHeader("first-response", "first-response-header"))        
//            			.uri("http://localhost:8070/"))
                		.uri("lb://EUREKA-SERVICE-ABC"))
                .route(r -> r.path("/serviceDef/**")
                		.filters(f -> f.rewritePath("/serviceDef(?<path>/?.*)", "$\\{path}"))
//                        .uri("http://localhost:8071/"))
                		.uri("lb://EUREKA-SERVICE-DEF"))
//                .route(p -> p
//                        .host("*.hystrix.com")
//                        .filters(f -> f.hystrix(config -> config.setName("mycmd")))
//                        .uri("http://httpbin.org:80"))
				.build();
	}

}