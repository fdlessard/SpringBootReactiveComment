package io.fdlessard.codebites.reactive.controllers;

import io.fdlessard.codebites.reactive.GetCommentWebClientObservableCommand;
import io.fdlessard.codebites.reactive.domain.Comment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import rx.Observable;
import rx.Single;

import java.util.ArrayList;
import java.util.List;

import static io.fdlessard.codebites.reactive.configurations.ReactiveConfiguration.COMMENT_BASE_URL;
import static io.fdlessard.codebites.reactive.configurations.ReactiveConfiguration.buildIds;

@Slf4j
@RestController
public class HystrixWebClientCommentController {

    private WebClient commentWebClient;

    public HystrixWebClientCommentController(WebClient commentWebClient) {
        this.commentWebClient = commentWebClient;
    }

    @GetMapping(value = "/hystrix/webclient/comments/{id}")
    public Single<Comment> getComment(@PathVariable int id) {
        GetCommentWebClientObservableCommand getCommentCommand =
                new GetCommentWebClientObservableCommand("/" + id, commentWebClient);
        return getCommentCommand.toObservable().toSingle();
    }

    @GetMapping(value = "/hystrix/webclient/comments")
    public Observable<Comment> getAllComments() {

        List<String> ids = buildIds();
        List<Observable<Comment>> observables = new ArrayList<>();

        for (int i = 0; i < ids.size(); i++) {
            GetCommentWebClientObservableCommand observableCommand =
                    new GetCommentWebClientObservableCommand("/" + ids.get(i), commentWebClient);
            observables.add(observableCommand.observe());
        }

        return Observable.merge(observables);
    }
}


