package io.fdlessard.codebites.reactive;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import io.fdlessard.codebites.reactive.domain.Comment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import rx.Observable;
import rx.RxReactiveStreams;

import static com.netflix.hystrix.HystrixCommandProperties.ExecutionIsolationStrategy.THREAD;

@Slf4j
public class GetCommentWebClientObservableCommand extends HystrixObservableCommand<Comment> {

    private String url;

    private WebClient webClient;

    public GetCommentWebClientObservableCommand(String url, WebClient webClient) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("WebClientGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionTimeoutInMilliseconds(1000)
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)));
        this.url = url;
        this.webClient = webClient;
    }

    @Override
    protected Observable<Comment> construct() {

        Mono<Comment> comment = webClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Comment.class);

        return RxReactiveStreams.toObservable(comment);
    }
}