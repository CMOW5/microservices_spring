package com.cristian.licenses;

import java.util.Collections;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.cristian.licenses.utils.UserContextInterceptor;

@SpringBootApplication
/**
 * The @EnableDiscoveryClient annotation is the trigger 
 * for Spring Cloud to enable the application to use 
 * the DiscoveryClient and Ribbon libraries
 *
 */
@EnableDiscoveryClient
@EnableFeignClients 
@EnableCircuitBreaker // ells Spring Cloud you’re going to use Hystrix for your service
@RefreshScope
public class LicensingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LicensingServiceApplication.class, args);
	}
	
	/*
	 * The @LoadBalanced annotation tells Spring Cloud to create a
	 * Ribbon backed RestTemplate class.
	 * 
	 * this Ribbon-aware RestTemplate class is used with the
	 * OrganizationRestTemplateClient class
	 * 
	 * the @EnableDiscoveryClient
	 * and @EnableFeignClients application aren’t needed 
	 * when using the Ribbon backed RestTemplate
	 */
	@LoadBalanced
    @Bean
    public RestTemplate getRestTemplate(){
        RestTemplate template = new RestTemplate();
        List interceptors = template.getInterceptors();
        if (interceptors==null){
            template.setInterceptors(Collections.singletonList(new UserContextInterceptor()));
        }
        else{
            interceptors.add(new UserContextInterceptor());
            template.setInterceptors(interceptors);
        }

        return template;
    }

}
