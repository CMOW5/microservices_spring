package com.cristian.licenses;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
/*
 * the @EnableDiscoveryClient and @EnableFeignClients application aren’t needed
 * when using the Ribbon backed RestTemplate and can be removed.
 */
@EnableDiscoveryClient
@EnableFeignClients
@RefreshScope
public class LicensingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LicensingServiceApplication.class, args);
	}
	
	/*
	 * The @LoadBalanced annotation tells Spring Cloud to create a
	 * Ribbon backed RestTemplate class.
	 */
	@LoadBalanced
	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

}
