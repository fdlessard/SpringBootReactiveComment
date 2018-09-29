package io.fdlessard.codebites.reactive.gateways;

import lombok.Getter;
import reactor.core.publisher.Mono;

@Getter
public class GatewayException2 extends RuntimeException{

    private int status;

    public GatewayException2() {
        super();
    }


    public GatewayException2(String message, int status) {
        super(message);
        this.status = status;
    }

    public GatewayException2(Throwable throwable) {
        super(throwable);
    }
}
