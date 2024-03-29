package com.cristian.licenses.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Often in a REST -based environment you are going to want to pass 
 * contextual information to a service call that will help you operationally 
 * manage the service. For example, you might pass a correlation ID or 
 * authentication token in the HTTP header of the REST call that can then be 
 * propagated to any downstream service calls. The correlation ID allows you to 
 * have a unique identifier that can be traced across multiple service 
 * calls in a single transaction. 
 * 
 * To make this value available anywhere in your service call, you might use a Spring Filter 
 * class to intercept every call into your REST service and retrieve this information from 
 * the incoming HTTP request and store this contextual information in a custom 
 * UserContex t object. Then, anytime your code needs to access this value in your 
 * REST service call, your code can retrieve the UserContext from the ThreadLocal 
 * storage variable and read the value.
 */
@Component
public class UserContextFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(UserContextFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {


        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        UserContextHolder.getContext().setCorrelationId(  httpServletRequest.getHeader(UserContext.CORRELATION_ID) );
        UserContextHolder.getContext().setUserId(httpServletRequest.getHeader(UserContext.USER_ID));
        UserContextHolder.getContext().setAuthToken(httpServletRequest.getHeader(UserContext.AUTH_TOKEN));
        UserContextHolder.getContext().setOrgId(httpServletRequest.getHeader(UserContext.ORG_ID));

        logger.debug("UserContextFilter Correlation id: {}", UserContextHolder.getContext().getCorrelationId());

        filterChain.doFilter(httpServletRequest, servletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}
