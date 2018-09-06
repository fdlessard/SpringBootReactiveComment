package io.fdlessard.codebites.reactive.configurations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "connection")
public class ConnectionProperties {

    private int soTimeout; // Socket Timeout ?
    private int connectTimeout;
    private int maxPoolSize;

}
