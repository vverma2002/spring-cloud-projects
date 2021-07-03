package com.vik;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SpringCloudConfigClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudConfigClientApplication.class, args);
	}

	@Value("${spring.profiles.active:Unknown}")
	private String activeProfile;

	@Value("${spring.application.name}")
	private String aplicationName;

	@Value("${default.msg1:Unable to connect to config server}")
	private String msg1;
	
	@Value("${default.msg2:Unable to connect to config server}")
	private String msg2;

	@GetMapping("/")
	public HashMap<String, Object> get() {
		HashMap<String, Object> map = new LinkedHashMap<>();
		map.put("Concept", "Spring Boot Config Client");
		map.put("Application Name", aplicationName);
		map.put("ActiveProfile", activeProfile);
		map.put("default.msg1", msg1);
		map.put("default.msg2", msg2);
		return map;
	}
}
