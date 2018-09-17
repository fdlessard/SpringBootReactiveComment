package io.fdlessard.codebites.reactive.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.awt.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class Response<T, E> implements Serializable {

    private HttpStatus status;
    private T data;
    private E error;

}
