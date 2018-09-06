package io.fdlessard.codebites.reactive;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import io.fdlessard.codebites.reactive.domain.Comment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

@Slf4j
public class GetCommentRestTemplateObservableCommand extends HystrixObservableCommand<Comment> {

    private String url;

    private RestTemplate restTemplate;

    public GetCommentRestTemplateObservableCommand(String url, RestTemplate restTemplate) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("RestTemplateGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionTimeoutInMilliseconds(1000)
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)));
        this.url = url;
        this.restTemplate = restTemplate;
    }

    @Override
    protected Observable<Comment> construct() {
        return Observable.just(restTemplate.getForObject(url, Comment.class));
    }
}