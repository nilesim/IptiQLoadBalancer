package com.iptq.loadbalance;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.iptq.service.Provider;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;

@EnableAsync
@Service
@Aspect
public abstract class LoadBalancer {

    int MAX_NUM_OF_PROVIDERS = 10;
    int Y_MAX_NUM_OF_PARALLEL_REQS = 18;
    final int X_SECONDS = 20000;
    int requestCount;

    protected Random random;
    public List<String> instances = new ArrayList();
    public Set<String> excludedInstances = new HashSet<String>();
    public Set<String> aliveProviders = new HashSet<String>();
    public Set<String> heartbeatChecked = new HashSet<String>();
    public Map<String, Provider> providerMap = new HashMap<>();

    public LoadBalancer () {
        requestCount = 0;
        random = new Random();
    }

    @Before("execution(* com.iptq.loadbalance.LoadBalancer.get(..))")
    public void beforeAdvice() throws Exception {
        requestCount++;
        if(requestCount> (Y_MAX_NUM_OF_PARALLEL_REQS * aliveProviders.size())) {
            throw new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS);
        }
    }


    public abstract String get();

    public void excludeNode(String instanceId) {
        excludedInstances.add(instanceId);
        aliveProviders.remove(instanceId);
    }

    public void includeNode(String instanceId) {
        excludedInstances.remove(instanceId);
        aliveProviders.add(instanceId);
    }

    @Async
    @Scheduled(fixedRate = X_SECONDS)
    public void healthChecker() throws InterruptedException {
        System.out.println(
                "Health check started - " + System.currentTimeMillis() / 1000);
        for (String instanceId : instances) {
            System.out.println("Health check started for following instance " + instanceId);
            if (providerMap.get(instanceId).check()) {
                if (heartbeatChecked.contains(instanceId)) {
                    includeNode(instanceId);
                    System.out.println("Heartbeat checked for 2 consecutive " +
                            "times for following instance " + instanceId);
                } else {
                    heartbeatChecked.add(instanceId);
                }
            } else {
                System.out.println("Node with following instance: " + instanceId
                        + " is not alive, excluded from active providers ");
                excludeNode(instanceId);
            }
        }
    }

    public void register() throws Exception {
        if (instances.size() < MAX_NUM_OF_PROVIDERS) {
            Provider provider = new Provider();
            instances.add(provider.getInstance());
            providerMap.put(provider.getInstance(), provider);
        } else {
            throw new Exception("the maximum number of providers accepted from the load balancer exceeded!");
        }
    }
}
