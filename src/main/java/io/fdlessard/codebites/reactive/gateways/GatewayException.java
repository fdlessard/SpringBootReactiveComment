package io.fdlessard.codebites.reactive.gateways;

public class GatewayException extends Exception{

    private int status;

    public GatewayException() {
        super();
    }


    public GatewayException(String message, int status) {
        super(message);
        this.status = status;
    }
}
