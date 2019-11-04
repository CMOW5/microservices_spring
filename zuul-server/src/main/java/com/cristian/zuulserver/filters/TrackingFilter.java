package com.cristian.zuulserver.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This filter will inspect all incoming requests to the gateway 
 * and determine whether there’s an HTTP header called tmx-correlation-id 
 * present in the request. The tmx-correlation-id header will contain a unique 
 * GUID (Globally Universal Id) that can be used to track a user’s request 
 * across multiple microservices.
 * 
 * If the tmx-correlation-id isn’t present on the HTTP header, 
 * your Zuul TrackingFilter will generate and set the correlation ID . 
 * If there’s already a correlation ID present, Zuul won’t do anything 
 * with the correlation ID . The presence of a correlation ID means that 
 * this particular service call is part of a chain of service calls 
 * carrying out the user’s request. In this case, your TrackingFilter 
 * class will do nothing.
 * 
 * @author bit5
 */
@Component
public class TrackingFilter extends ZuulFilter {
	private static final int FILTER_ORDER = 1;
	private static final boolean SHOULD_FILTER = true;
	private static final Logger logger = LoggerFactory.getLogger(TrackingFilter.class);

	@Autowired
	FilterUtils filterUtils;

	@Override
	public String filterType() {
		return FilterUtils.PRE_FILTER_TYPE;
	}

	@Override
	public int filterOrder() {
		return FILTER_ORDER;
	}

	public boolean shouldFilter() {
		return SHOULD_FILTER;
	}

	private boolean isCorrelationIdPresent() {
		if (filterUtils.getCorrelationId() != null) {
			return true;
		}

		return false;
	}

	private String generateCorrelationId() {
		return java.util.UUID.randomUUID().toString();
	}

	public Object run() {

		if (isCorrelationIdPresent()) {
			logger.debug("tmx-correlation-id found in tracking filter: {}. ", filterUtils.getCorrelationId());
		} else {
			filterUtils.setCorrelationId(generateCorrelationId());
			logger.debug("tmx-correlation-id generated in tracking filter: {}.", filterUtils.getCorrelationId());
		}

		RequestContext ctx = RequestContext.getCurrentContext();
		logger.debug("Processing incoming request for {}.", ctx.getRequest().getRequestURI());
		return null;
	}
}
