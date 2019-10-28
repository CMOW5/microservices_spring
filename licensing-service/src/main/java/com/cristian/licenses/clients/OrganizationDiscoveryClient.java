package com.cristian.licenses.clients;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.cristian.licenses.model.Organization;

/**
 * 
 * @author bit5 
 * 
 * Uses the discovery client and a standard Spring 
 * RestTemplate class to invoke the organization service
 * 
 * The reality is that you should only use the Discovery-
 * Client directly when your service needs to query Ribbon 
 * to understand what services and service instances 
 * are registered with it
 *       
 */
@Component
public class OrganizationDiscoveryClient {

	@Autowired
	private DiscoveryClient discoveryClient;

	public Organization getOrganization(String organizationId) {
		RestTemplate restTemplate = new RestTemplate();
		List<ServiceInstance> instances = discoveryClient.getInstances("organizationservice");

		if (instances.size() == 0)
			return null;
		String serviceUri = String.format("%s/v1/organizations/%s", instances.get(0).getUri().toString(),
				organizationId);

		ResponseEntity<Organization> restExchange = restTemplate.exchange(serviceUri, HttpMethod.GET, null,
				Organization.class, organizationId);

		return restExchange.getBody();
	}
}
