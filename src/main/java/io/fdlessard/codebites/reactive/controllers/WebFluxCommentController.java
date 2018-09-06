package io.fdlessard.codebites.reactive.controllers;

import io.fdlessard.codebites.reactive.domain.Comment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static io.fdlessard.codebites.reactive.configurations.ReactiveConfiguration.buildIds;

@Slf4j
@RestController
public class WebFluxCommentController {

    private WebClient webClient;

    public WebFluxCommentController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping(value = "/webflux/comments/{id}")
    public Mono<Comment> getComment(@PathVariable int id) {

        return webClient.get()
                .uri("/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Comment.class);
    }

    @GetMapping("/webflux/comments")
    public Flux<Comment> getAllComments() {

        List<Integer> ids = buildIds();

        return Flux.fromIterable(ids)
                .flatMap(id -> webClient.get()
                        .uri("/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(Comment.class), 256);
    }
}

