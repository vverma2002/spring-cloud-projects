package com.vik;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableConfigServer
@RestController
public class SpringCloudConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudConfigServerApplication.class, args);
	}

	@Value("${spring.profiles.active:Unknown}")
	private String activeProfile;
	
	@Value("${spring.application.name}")
	private String aplicationName;
	
	@Autowired
	private ServletWebServerApplicationContext webServerAppCtxt;

	@GetMapping("/json")
	public HashMap<String, Object> get() {
		HashMap<String, Object> map = new LinkedHashMap<>();
		map.put("Concept", "Spring Boot Config Server");
		map.put("Application Name", aplicationName);
		map.put("ActiveProfile",activeProfile);
		map.put("Port", webServerAppCtxt.getWebServer().getPort());
		map.put("config-client", "http://localhost:" + webServerAppCtxt.getWebServer().getPort() + "/config-client/default");
		map.put("config-client-dev", "http://localhost:" + webServerAppCtxt.getWebServer().getPort() + "/config-client/dev");
		map.put("config-client-test", "http://localhost:" + webServerAppCtxt.getWebServer().getPort() + "/config-client/test");
		map.put("config-client-dev.properties","http://localhost:" + webServerAppCtxt.getWebServer().getPort() + "/config-client-dev.properties");
		map.put("config-client-test.properties","http://localhost:" + webServerAppCtxt.getWebServer().getPort() + "/config-client-test.properties");
		return map;
	}
}
