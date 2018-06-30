package io.fdlessard.codebites.reactive.controllers;

import io.fdlessard.codebites.reactive.domain.Comment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class CommentController {

    public CommentController() {
    }

    @GetMapping("/comments")
    public Flux<Comment> getAllComments() {

        WebClient webClient = getWebClient();
        List<Integer> ids = buildIds();

        List<Mono<Comment>> monos = ids.stream()
                .map(id -> webClient.get()
                        .uri("/comments/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(Comment.class))
                .collect(Collectors.toList());

        return Flux.concat(monos);
    }

    private WebClient getWebClient() {
        return WebClient.builder()
                .baseUrl("http://jsonplaceholder.typicode.com")
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

    private List<Integer> buildIds() {
        List<Integer> ids = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            ids.add(i);
        }

        return ids;
    }
}

