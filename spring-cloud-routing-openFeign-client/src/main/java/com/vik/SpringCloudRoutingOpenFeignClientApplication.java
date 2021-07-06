package com.vik;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableFeignClients
public class SpringCloudRoutingOpenFeignClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudRoutingOpenFeignClientApplication.class, args);
	}

	@Value("${spring.application.name}")
	private String aplicationName;

	@Value("${server.port}")
	private String serverPort;

	@Value("${spring.profiles.active:default}")
	private String activeProfile;

	@Autowired
	private EurekaServiceAbcClient eurekaServiceAbcClient;

	@Autowired
	private EurekaServiceDefClient eurekaServiceDefClient;

	@GetMapping("/")
	public HashMap<String, Object> get() {
		HashMap<String, Object> map = new LinkedHashMap<>();
		map.put("Concept", "Spring Open Feign Client");
		map.put("Application Name", aplicationName);
		map.put("ServerPort", serverPort);
		map.put("ActiveProfile", activeProfile);
		map.put("Actuator", "http://localhost:" + serverPort + "/actuator");
		map.put("----------", "----------");
		map.put("/Feigh EurekaServiceAbc", "http://localhost:" + serverPort + "/feighGreetings-withEurekaServiceAbc");
		map.put("/Feigh EurekaServiceDef", "http://localhost:" + serverPort + "/feighGreetings-withEurekaServiceDef");

		return map;
	}

	@RequestMapping("/feighGreetings-withEurekaServiceAbc")
	public String greetingWithFeignwithEurekaServiceAbc() {
		return eurekaServiceAbcClient.greetings();
	}

	@RequestMapping("/feighGreetings-withEurekaServiceDef")
	public String greetingWithFeignwithEurekaServiceDef() {
		return eurekaServiceDefClient.greetings();
	}

}

@FeignClient("eureka-service-abc")
interface EurekaServiceAbcClient {

	@RequestMapping("/greetings?name=OpenFeigh Client")
	public String greetings();
}

@FeignClient("eureka-service-def")
//@FeignClient(name = "weather", url = "weather.livedoor.com")
//@FeignClient(name = "localServiceClient", url = "localhost:1234", path = "/api/server")
interface EurekaServiceDefClient {

	@RequestMapping("/greetings?name=OpenFeigh Client")
	public String greetings();

//  @GetMapping("/firstService")
//  String getServerFirstServiceData();

//	@RequestMapping(method = RequestMethod.GET, value = "/forecast/webservice/json/v1") 
//	ResponseEntity<WeatherInfo> getWeatherInfo(@RequestParam("city") Long city);
}
