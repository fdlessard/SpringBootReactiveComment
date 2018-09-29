package io.fdlessard.codebites.reactive.controllers;

import io.fdlessard.codebites.reactive.domain.Comment;
import io.fdlessard.codebites.reactive.domain.Response;
import io.fdlessard.codebites.reactive.gateways.CommentGateway;
import io.fdlessard.codebites.reactive.gateways.GatewayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class WebFluxCommentController {

    private CommentGateway commentGateway;

    public WebFluxCommentController(CommentGateway commentGateway) {
        this.commentGateway = commentGateway;
    }

    @GetMapping(value = "/webflux/comments/{id}")
    public Comment getComment(@PathVariable int id) {
        return commentGateway.getComment(id);
    }

    @GetMapping("/webflux/comments")
    public List<Response<Comment>> getAllComments() {

        Map<String, Response<Comment>> map = commentGateway.getAllComments();

        return map.values().stream().collect(Collectors.toList());
    }

    @ExceptionHandler(GatewayException.class)
    public ResponseEntity<String> handleWebClientResponseException(GatewayException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ex.getMessage());
    }
}

