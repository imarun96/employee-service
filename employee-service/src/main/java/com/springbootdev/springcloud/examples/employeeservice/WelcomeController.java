package com.springbootdev.springcloud.examples.employeeservice;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RefreshScope
@RestController
public class WelcomeController {
	@Autowired
	private DiscoveryClient client;

	@Autowired
	RestTemplate template;

	@Value("${app.service-name}")
	private String serviceName;

	@GetMapping("/service")
	public String getServiceName() {
		return "service name [" + this.serviceName + "]";
	}

	@GetMapping("/refresh-specific-endpoint")
	public String getInstance(@RequestParam(name = "target", required = true) String ServerName) {
		System.out.println("Into the method");
		client.getInstances(ServerName).stream().map(ServiceInstance::getUri).forEach(action -> {
			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> entity = new HttpEntity<>(header);
			URI refreshURI = URI.create("http://localhost:" + action.getPort() + "/actuator/refresh");
			template.exchange(refreshURI, HttpMethod.POST, entity, String.class);
			System.out.println("URI = " + refreshURI);
			refreshURI.toString();
		});
		return "Config Properties of the service [" + ServerName + "] has been refreshed.";
	}
}