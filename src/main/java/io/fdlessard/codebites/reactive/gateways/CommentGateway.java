package io.fdlessard.codebites.reactive.gateways;

import io.fdlessard.codebites.reactive.domain.Comment;
import io.fdlessard.codebites.reactive.domain.ErrorResponse;
import io.fdlessard.codebites.reactive.domain.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CommentGateway {

    // https://www.callicoder.com/spring-5-reactive-webclient-webtestclient-examples/

    private WebClient webClient;

    public CommentGateway(WebClient webClient) {
        this.webClient = webClient;
    }

    public Comment getComment(@PathVariable int id) {

        return webClient.get()
                .uri("/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Comment.class)
                .doOnError(WebClientResponseException.class, e -> handleError(e))
                .block();

    }


    private void handleError(WebClientResponseException e) {

        throw new GatewayException2(e.getResponseBodyAsString(), e.getRawStatusCode());

    }

    public Map<String, Response<Comment>> getAllComments() {

/*        List<String> ids = buildIds();
        ids.add(10, "toto");*/

        List<String> ids = Arrays.asList("1", "2", "3", "4", "5", "6");


        return Flux.fromIterable(ids)
                .flatMap(id -> webClient.get()
                                .uri("/{id}", id)
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .bodyToMono(Comment.class)
                                .flatMap(c -> buildResponse(id, c))
                                .onErrorResume(e -> buildResponse(id, e)),
                        256)
                .collectMap(id -> id.getT1(), id -> id.getT2())
                .block();
    }

    private Mono<Tuple2<String, Response<Comment>>> buildResponse(String id, Comment comment) {
        return Mono.just(Tuples.of(id, new Response(HttpStatus.OK, comment, null)));
    }

    private Mono<Tuple2<String, Response<Comment>>> buildResponse(String id, Throwable e) {

        if (e instanceof WebClientResponseException) {
            WebClientResponseException wce = (WebClientResponseException) e;
            return Mono.just(Tuples.of(id, new Response(HttpStatus.BAD_REQUEST, null, new ErrorResponse(wce.getRawStatusCode(), wce.getResponseBodyAsString()))));
        }

        return Mono.just(Tuples.of(id, new Response(HttpStatus.BAD_REQUEST, null, new ErrorResponse())));
    }
}
