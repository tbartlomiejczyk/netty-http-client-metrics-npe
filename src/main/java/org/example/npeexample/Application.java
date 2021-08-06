package org.example.npeexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.cloud.gateway.config.HttpClientCustomizer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    @Bean
    public HttpClientCustomizer metricsHttpClientCustomizer() {
        return httpClient -> httpClient.metrics(true, uri -> "" /* limit uri values*/);
    }


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
