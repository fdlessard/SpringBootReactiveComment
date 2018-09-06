package io.fdlessard.codebites.reactive.configurations;

import io.fdlessard.codebites.reactive.configurations.ConnectionProperties;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.ipc.netty.resources.PoolResources;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Slf4j
@Configuration
public class ReactiveConfiguration {

    public static final String COMMENT_BASE_URL = "http://jsonplaceholder.typicode.com/comments";
    public static final int MAX_COMMENT = 275;

    @Autowired
    private ConnectionProperties connectionProperties;

    @Bean
    public ClientHttpConnector clientHttpConnector() {

        return new ReactorClientHttpConnector(options -> {
            options.option(ChannelOption.SO_TIMEOUT, connectionProperties.getSoTimeout());
            options.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionProperties.getConnectTimeout());
            options.poolResources(PoolResources.fixed("myPool", connectionProperties.getMaxPoolSize()));
        });
    }

    @Bean
    public RestTemplate commentRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public WebClient commentWebClient(ClientHttpConnector clientHttpConnector) {
        return WebClient.builder()
                .baseUrl(COMMENT_BASE_URL)
                .clientConnector(clientHttpConnector)
                .filter(logRequest())
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return (clientRequest, next) -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers()
                    .forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return next.exchange(clientRequest);
        };
    }

    public static List<Integer> buildIds() {

        return IntStream.range(1,MAX_COMMENT).boxed()
                .collect(toList());
    }
}
