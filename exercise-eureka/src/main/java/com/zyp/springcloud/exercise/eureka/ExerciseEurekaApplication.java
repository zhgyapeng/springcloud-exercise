package com.zyp.springcloud.exercise.eureka;

import com.google.common.base.Preconditions;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
@EnableHystrixDashboard
@EnableZuulProxy
@EnableFeignClients
public class ExerciseEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExerciseEurekaApplication.class, args);
    }

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @GetMapping("/")
    @HystrixCommand(fallbackMethod = "failHandle", commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    public String home() {
        ServiceInstance instance = loadBalancerClient.choose("stores");
        System.out.println(instance.getHost() + ":" + instance.getPort());
        Preconditions.checkArgument(false, "Fail Message.");
        return "Hello World.";
    }

    public String failHandle() {
        return "Fails";
    }
}