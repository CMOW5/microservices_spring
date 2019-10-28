package com.cristian.licenses.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cristian.licenses.model.Organization;
/**
 * 
 * @author bit5
 * Uses Netflix’s Feign client library to invoke a service 
 * via Ribbon
 * 
 * an alternative to the Spring Ribbon-enabled RestTemplate class is Netflix’s Feign
 *	client library. The Feign library takes a different approach to calling a REST service by
 *	having the developer first define a Java interface and then annotating that interface
 *	with Spring Cloud annotations to map what Eureka-based service Ribbon will invoke.
 *	The Spring Cloud framework will dynamically generate a proxy class that will be used
 *	to invoke the targeted REST service. There’s no code being written for calling the ser-
 *	vice other than an interface definition.
 *	To enable the Feign client for use in your licensing service, you need to add a new
 *	annotation, @EnableFeignClients
 */
@FeignClient("organizationservice")
public interface OrganizationFeignClient {
	@RequestMapping(method = RequestMethod.GET, value = "/v1/organizations/{organizationId}", consumes = "application/json")
	Organization getOrganization(@PathVariable("organizationId") String organizationId);
}
