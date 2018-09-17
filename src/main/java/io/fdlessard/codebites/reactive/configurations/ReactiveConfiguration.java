package io.fdlessard.codebites.reactive.configurations;

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

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Slf4j
@Configuration
public class ReactiveConfiguration {

    public static final String COMMENT_BASE_URL = "http://jsonplaceholder.typicode.com/comments";
    public static final int MAX_COMMENT = 500;

    @Autowired
    private ConnectionProperties connectionProperties;

    @Bean
    public ClientHttpConnector clientHttpConnector() {

/*        return new ReactorClientHttpConnector(options -> {
            options.option(ChannelOption.SO_TIMEOUT, connectionProperties.getSoTimeout());
            options.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionProperties.getConnectTimeout());
            options.poolResources(PoolResources.fixed("myPool", connectionProperties.getMaxPoolSize()));
        });*/

        return new ReactorClientHttpConnector();
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

    public static List<String> buildIds() {

        return IntStream.range(1,MAX_COMMENT).boxed()
                .map(Objects::toString)
                .collect(toList());
    }
}
