package com.cristian.licenses.clients;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.cristian.licenses.model.Organization;
import com.cristian.licenses.utils.UserContextHolder;

/**
 * 
 * @author bit5
 * Uses an enhanced Spring RestTemplate to invoke the 
 * Ribbon-based service
 * 
 * Using the Ribbon-backed RestTemplate class pretty much behaves like a stan-
 *	dard Spring RestTemplate class, except for one small difference in how the URL for
 *	target service is defined. Rather than using the physical location of the service in the
 *  RestTemplate call, you’re going to build the target URL using the Eureka service ID
 *	of the service you want to call.
 */
@Component
public class OrganizationRestTemplateClient {
    @Autowired
    RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(OrganizationRestTemplateClient.class);

    public Organization getOrganization(String organizationId){
        logger.debug(">>> In Licensing Service.getOrganization: {}. Thread Id: {}", UserContextHolder.getContext().getCorrelationId(), Thread.currentThread().getId());
        ResponseEntity<Organization> restExchange =
                restTemplate.exchange(
                        "http://zuulservice/api/organization/v1/organizations/{organizationId}",
                        HttpMethod.GET,
                        null, Organization.class, organizationId);
        return restExchange.getBody();
    }
}
