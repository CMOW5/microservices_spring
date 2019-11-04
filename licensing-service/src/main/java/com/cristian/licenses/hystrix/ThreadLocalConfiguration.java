package com.cristian.licenses.hystrix;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * class used to hook your HystrixConcurrencyStrategy via the
 * ThreadLocalAwareStrategy class and your Callable class defined via the
 * DelegatingUserContextCallable class.
 * 
 * This Spring configuration class basically rebuilds the Hystrix plugin that manages all 
 * the different components running within your service. In the init() method, you’re 
 * grabbing references to all the Hystrix components used by the plugin. You then register 
 * your custom HystrixConcurrencyStrategy ( ThreadLocalAwareStrategy)
 * 
 * @author bit5
 *
 */
@Configuration
public class ThreadLocalConfiguration {
	
	@Autowired(required = false)
	private HystrixConcurrencyStrategy existingConcurrencyStrategy;

	@PostConstruct
	public void init() {
		// Keeps references of existing Hystrix plugins.
		HystrixEventNotifier eventNotifier = HystrixPlugins.getInstance().getEventNotifier();
		HystrixMetricsPublisher metricsPublisher = HystrixPlugins.getInstance().getMetricsPublisher();
		HystrixPropertiesStrategy propertiesStrategy = HystrixPlugins.getInstance().getPropertiesStrategy();
		HystrixCommandExecutionHook commandExecutionHook = HystrixPlugins.getInstance().getCommandExecutionHook();

		HystrixPlugins.reset();

		HystrixPlugins.getInstance()
				.registerConcurrencyStrategy(new ThreadLocalAwareStrategy(existingConcurrencyStrategy));
		HystrixPlugins.getInstance().registerEventNotifier(eventNotifier);
		HystrixPlugins.getInstance().registerMetricsPublisher(metricsPublisher);
		HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);
		HystrixPlugins.getInstance().registerCommandExecutionHook(commandExecutionHook);
	}
}
