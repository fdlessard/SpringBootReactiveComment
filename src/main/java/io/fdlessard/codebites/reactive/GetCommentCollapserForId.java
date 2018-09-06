package io.fdlessard.codebites.reactive;

import com.netflix.hystrix.*;
import io.fdlessard.codebites.reactive.domain.Comment;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static io.fdlessard.codebites.reactive.configurations.ReactiveConfiguration.COMMENT_BASE_URL;

public class GetCommentCollapserForId extends HystrixCollapser<List<Comment>, Comment, Integer> {

    private final Integer key;

    private final RestTemplate restTemplate;

    public GetCommentCollapserForId(RestTemplate restTemplate, Integer key) {
        this.restTemplate = restTemplate;
        this.key = key;
    }

    @Override
    public Integer getRequestArgument() {
        return key;
    }

    @Override
    protected  HystrixCommand<List<Comment>> createCommand(Collection<CollapsedRequest<Comment, Integer>> requests) {
        return new BatchCommand(restTemplate, requests);
    }

    @Override
    protected void mapResponseToRequests(List<Comment> batchResponse, Collection<CollapsedRequest<Comment, Integer>> requests) {
        int count = 0;
        for (CollapsedRequest<Comment, Integer> request : requests) {
            request.setResponse(batchResponse.get(count++));
        }
    }

   private static final class BatchCommand extends HystrixCommand<List<Comment>> {

       private final RestTemplate restTemplate;


       private final Collection<CollapsedRequest<Comment, Integer>> requests;

        private BatchCommand( RestTemplate restTemplate, Collection<CollapsedRequest<Comment, Integer>> requests) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("CollapsedGroup"))
                    .andCommandKey(HystrixCommandKey.Factory.asKey("CollapsedGroup"))
            .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                    .withExecutionTimeoutInMilliseconds(2000)));
            this.restTemplate = restTemplate;
            this.requests = requests;
        }

        @Override
        protected List<Comment> run() {
            ArrayList<Comment> response = new ArrayList<Comment>();
            for (CollapsedRequest<Comment, Integer> request : requests) {
                // artificial response for each argument received in the batch
                response.add(restTemplate.getForObject(COMMENT_BASE_URL + "/" + request.getArgument(), Comment.class));
            }

            return response;
        }
    }
}