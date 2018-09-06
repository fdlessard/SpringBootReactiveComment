package io.fdlessard.codebites.reactive.controllers;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import io.fdlessard.codebites.reactive.GetCommentCollapserForId;
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
import java.util.concurrent.Future;

import static io.fdlessard.codebites.reactive.configurations.ReactiveConfiguration.COMMENT_BASE_URL;
import static io.fdlessard.codebites.reactive.configurations.ReactiveConfiguration.buildIds;

@Slf4j
@RestController
public class HystrixCollapserRestTemplateCommentController {

    private RestTemplate commentRestTemplate;

    public HystrixCollapserRestTemplateCommentController(RestTemplate commentRestTemplate) {
        this.commentRestTemplate = commentRestTemplate;
    }

    @GetMapping(value = "/hystrixcollapser/resttemplate/comments")
    public List<Comment> getAllComments() throws Exception {

        List<Integer> ids = buildIds();
        List<Future<Comment>> futures = new ArrayList<>();

        HystrixRequestContext context = HystrixRequestContext.initializeContext();

        for (int i = 0; i < ids.size(); i++) {
            Future<Comment> getCommentCollapserForId = new GetCommentCollapserForId(commentRestTemplate, ids.get(i)).queue();
            futures.add(getCommentCollapserForId);
        }

        List<Comment> comments = new ArrayList<>();

        for (int i = 0; i < ids.size(); i++) {
            comments.add(futures.get(i).get());
        }

        return comments;
    }
}


