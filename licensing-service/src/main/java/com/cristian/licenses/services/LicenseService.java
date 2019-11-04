package com.cristian.licenses.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cristian.licenses.clients.OrganizationDiscoveryClient;
import com.cristian.licenses.clients.OrganizationFeignClient;
import com.cristian.licenses.clients.OrganizationRestTemplateClient;
import com.cristian.licenses.config.ServiceConfig;
import com.cristian.licenses.model.License;
import com.cristian.licenses.model.Organization;
import com.cristian.licenses.repository.LicenseRepository;
import com.cristian.licenses.utils.UserContextHolder;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class LicenseService {
    private static final Logger logger = LoggerFactory.getLogger(LicenseService.class);

	@Autowired
	private LicenseRepository licenseRepository;

	@Autowired
	ServiceConfig config;

	@Autowired
	OrganizationFeignClient organizationFeignClient;

	@Autowired
	OrganizationRestTemplateClient organizationRestClient;

	@Autowired
	OrganizationDiscoveryClient organizationDiscoveryClient;

	private Organization retrieveOrgInfo(String organizationId, String clientType) {
		Organization organization = null;

		switch (clientType) {
		case "feign":
			System.out.println("I am using the feign client");
			organization = organizationFeignClient.getOrganization(organizationId);
			break;
		case "rest":
			System.out.println("I am using the rest client");
			organization = organizationRestClient.getOrganization(organizationId);
			break;
		case "discovery":
			System.out.println("I am using the discovery client");
			organization = organizationDiscoveryClient.getOrganization(organizationId);
			break;
		default:
			organization = organizationRestClient.getOrganization(organizationId);
		}

		return organization;
	}

	public License getLicense(String organizationId, String licenseId, String clientType) {
		License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);

		Organization org = retrieveOrgInfo(organizationId, clientType);

		return license.withOrganizationName(org.getName()).withContactName(org.getContactName())
				.withContactEmail(org.getContactEmail()).withContactPhone(org.getContactPhone())
				.withComment(config.getExampleProperty());
	}

	private void randomlyRunLong() {
		Random rand = new Random();

		int randomNum = rand.nextInt((3 - 1) + 1) + 1;
		if (randomNum == 3)
			sleep();
	}

	private void sleep() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * With the use of the @HystrixCommand annotation, any time the
	 * getLicensesByOrg() method is called, the call will be wrapped with a Hystrix
	 * circuit breaker. The circuit breaker will interrupt any call to the
	 * getLicensesByOrg() method any time the call takes longer than 1,000
	 * milliseconds.
	 * 
	 * The fallbackMethod attribute defines a single function in your class that
	 * will be called if the call from Hystrix fails.
	 */
	@HystrixCommand(
			fallbackMethod = "buildFallbackLicenseList", 
			commandProperties = {
				@HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="2000"),  // set the lenght of the timeout in ms 
				@HystrixProperty(name="circuitBreaker.requestVolumeThreshold", value="10"),
                @HystrixProperty(name="circuitBreaker.errorThresholdPercentage", value="75"),
                @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds", value="7000"),
                @HystrixProperty(name="metrics.rollingStats.timeInMilliseconds", value="15000"),
                @HystrixProperty(name="metrics.rollingStats.numBuckets", value="5")},
			threadPoolKey = "licenseByOrgThreadPool", // the unique name of the thread pool
			threadPoolProperties = 
				{@HystrixProperty(name = "coreSize", value = "30"),  // maximum number of threads
				 @HystrixProperty(name = "maxQueueSize", value = "10") }) // queue that sits in front of your thread pool and that can queue incoming requests.
	public List<License> getLicensesByOrg(String organizationId) {
        logger.debug("LicenseService.getLicensesByOrg  Correlation id: {}", UserContextHolder.getContext().getCorrelationId());
		
		randomlyRunLong();

		return licenseRepository.findByOrganizationId(organizationId);
	}

	/**
	 * Be aware of the actions you’re taking with your fallback functions. If you
	 * call out to another distributed service in your fallback service you may need
	 * to wrap the fallback with a @HystrixCommand annotation. Remember, the same
	 * failure that you’re experiencing with your primary course of action might
	 * also impact your secondary fallback option. Code defensively.
	 */
	@SuppressWarnings("unused")
	private List<License> buildFallbackLicenseList(String organizationId) {
		List<License> fallbackList = new ArrayList<>();
		License license = new License().withId("0000000-00-00000").withOrganizationId(organizationId)
				.withProductName("Sorry no licensing information currently available");
		fallbackList.add(license);
		return fallbackList;
	}

	public void saveLicense(License license) {
		license.withId(UUID.randomUUID().toString());

		licenseRepository.save(license);

	}

	public void updateLicense(License license) {
		licenseRepository.save(license);
	}

	public void deleteLicense(License license) {
		licenseRepository.deleteById(license.getLicenseId());
	}

}