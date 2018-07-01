package io.fdlessard.codebites.reactive;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.ipc.netty.resources.PoolResources;

@SpringBootApplication
public class ReactiveCommentApplication {

    @Autowired
    private ConnectionConfiguration connectionConfiguration;

    public static void main(String[] args) {
        SpringApplication.run(ReactiveCommentApplication.class, args);
    }

    @Bean
    public ClientHttpConnector clientHttpConnector() {

        return new ReactorClientHttpConnector(options -> {
            options.option(ChannelOption.SO_TIMEOUT, connectionConfiguration.getSoTimeout());
            options.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionConfiguration.getConnectTimeout());
            options.poolResources(PoolResources.fixed("myPool", connectionConfiguration.getMaxPoolSize()));
        });
    }
}
