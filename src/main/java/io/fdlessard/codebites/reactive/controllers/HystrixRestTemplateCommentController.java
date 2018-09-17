package io.fdlessard.codebites.reactive.controllers;

import io.fdlessard.codebites.reactive.GetCommentRestTemplateObservableCommand;
import io.fdlessard.codebites.reactive.domain.Comment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import rx.Observable;
import rx.Single;

import java.util.ArrayList;
import java.util.List;

import static io.fdlessard.codebites.reactive.configurations.ReactiveConfiguration.COMMENT_BASE_URL;
import static io.fdlessard.codebites.reactive.configurations.ReactiveConfiguration.buildIds;

@Slf4j
@RestController
public class HystrixRestTemplateCommentController {

    private RestTemplate commentRestTemplate;

    public HystrixRestTemplateCommentController(RestTemplate commentRestTemplate) {
        this.commentRestTemplate = commentRestTemplate;
    }

    @GetMapping(value = "/hystrix/resttemplate/comments/{id}")
    public Single<Comment> getComment(@PathVariable int id) {
        GetCommentRestTemplateObservableCommand getCommentCommand =
                new GetCommentRestTemplateObservableCommand(COMMENT_BASE_URL + "/" + id, commentRestTemplate);
        return getCommentCommand.toObservable().toSingle();
    }

    @GetMapping(value = "/hystrix/resttemplate/comments")
    public Observable<Comment> getAllComments() {

        List<String> ids = buildIds();
        List<Observable<Comment>> observables = new ArrayList<>();

        for (int i = 0; i < ids.size(); i++) {
            String url = COMMENT_BASE_URL + "/" + ids.get(i);
            GetCommentRestTemplateObservableCommand getCommentObservableCommand =
                    new GetCommentRestTemplateObservableCommand(url, commentRestTemplate);
            observables.add(getCommentObservableCommand.observe());
        }

        return Observable.merge(observables);
    }
}


