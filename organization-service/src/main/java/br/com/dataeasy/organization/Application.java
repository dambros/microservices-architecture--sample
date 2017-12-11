package br.com.dataeasy.organization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.sleuth.Sampler;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import javax.servlet.Filter;

import br.com.dataeasy.usercontext.utils.UserContextFilter;

@SpringBootApplication
@EnableCircuitBreaker
@EnableEurekaClient
@EnableResourceServer
@EnableBinding(Source.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Filter userContextFilter() {
        return new UserContextFilter();
    }

    @Bean
    public Sampler defaultSampler() {
        return new AlwaysSampler();
    }
}
