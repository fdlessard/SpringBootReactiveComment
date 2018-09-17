package io.fdlessard.codebites.reactive.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class ErrorResponse implements Serializable {

    private int status;
    private String message;
}
