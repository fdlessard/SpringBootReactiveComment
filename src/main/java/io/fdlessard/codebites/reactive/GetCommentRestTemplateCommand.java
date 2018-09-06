package io.fdlessard.codebites.reactive;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import io.fdlessard.codebites.reactive.domain.Comment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

@Slf4j
public class GetCommentRestTemplateCommand extends HystrixCommand<Comment> {

    private String url;

    private RestTemplate restTemplate;

    public GetCommentRestTemplateCommand(String url, RestTemplate restTemplate) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("RestTemplateGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionTimeoutInMilliseconds(1000)));
        this.url = url;
        this.restTemplate = restTemplate;
    }

    @Override
    protected Comment run() {
        return restTemplate.getForObject(url, Comment.class);
    }
}