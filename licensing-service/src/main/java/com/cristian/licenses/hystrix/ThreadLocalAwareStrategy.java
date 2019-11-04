package com.cristian.licenses.hystrix;

import com.cristian.licenses.utils.UserContextHolder;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableLifecycle;
import com.netflix.hystrix.strategy.properties.HystrixProperty;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This class is used to define a custom hystrix concurrency
 * strategy class, in order to propagate the thread context of 
 * the parent thread to your Hystrix command @HystrixCommand
 * 
 * Note a couple of things in the class implementation. 
 * First, because Spring Cloud already defines a HystrixConcurrencyStrategy, every method that 
 * could be overridden needs to check whether an existing concurrency strategy is present 
 * and then either call the existing concurrency strategy’s method or the base Hystrix 
 * concurrency strategy method. You have to do this as a convention to ensure that
 * you properly invoke the already-existing Spring Cloud’s 
 * HystrixConcurrency Strategy that deals with security. 
 * Otherwise, you can have nasty behavior when trying to use 
 * Spring security context in your Hystrix protected code
 */
public class ThreadLocalAwareStrategy extends HystrixConcurrencyStrategy {
    private HystrixConcurrencyStrategy existingConcurrencyStrategy;
    
    /**
     * Spring Cloud already has a concurrency class defined.
     * Pass the existing concurrency strategy into the class 
     * constructor of your HystrixConcurrencyStrategy.
     */
    public ThreadLocalAwareStrategy(
            HystrixConcurrencyStrategy existingConcurrencyStrategy) {
        this.existingConcurrencyStrategy = existingConcurrencyStrategy;
    }
    
    /*
     * Several methods need to be overridden. Either call the 
     * existingConcurrencyStrategy method implementation or call the base 
     * HystrixConcurrencyStrategy.
     */
    @Override
    public BlockingQueue<Runnable> getBlockingQueue(int maxQueueSize) {
        return existingConcurrencyStrategy != null
                ? existingConcurrencyStrategy.getBlockingQueue(maxQueueSize)
                : super.getBlockingQueue(maxQueueSize);
    }

    @Override
    public <T> HystrixRequestVariable<T> getRequestVariable(
            HystrixRequestVariableLifecycle<T> rv) {
        return existingConcurrencyStrategy != null
                ? existingConcurrencyStrategy.getRequestVariable(rv)
                : super.getRequestVariable(rv);
    }

    @Override
    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey,
                                            HystrixProperty<Integer> corePoolSize,
                                            HystrixProperty<Integer> maximumPoolSize,
                                            HystrixProperty<Integer> keepAliveTime, TimeUnit unit,
                                            BlockingQueue<Runnable> workQueue) {
        return existingConcurrencyStrategy != null
                ? existingConcurrencyStrategy.getThreadPool(threadPoolKey, corePoolSize,
                maximumPoolSize, keepAliveTime, unit, workQueue)
                : super.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize,
                keepAliveTime, unit, workQueue);
    }
    
    // inject your Callable implementation that will set the UserContext
    /**
     * In this method, you pass in Callable implementation, 
     * DelegatingUserContextCallable , that will be used to set the 
     * UserContext from the parent thread executing the user’s REST 
     * service call to the Hystrix command thread protecting the 
     * method that’s doing the work within.
     */
    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        return existingConcurrencyStrategy != null
                ? existingConcurrencyStrategy.wrapCallable(new DelegatingUserContextCallable<T>(callable, UserContextHolder.getContext()))
                : super.wrapCallable(new DelegatingUserContextCallable<T>(callable, UserContextHolder.getContext()));
    }
}
