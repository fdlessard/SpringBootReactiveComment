package io.fdlessard.codebites.reactive.controllers;

import io.fdlessard.codebites.reactive.domain.Customer;
import org.reactivestreams.Publisher;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
public class CustomerController {


    public CustomerController() {
    }

/*    @PostMapping("/customer")
    public Mono<Void> create(@RequestBody Publisher<Customer> customerPublisher) {

    }

    @GetMapping("/customer")
    public Flux<Customer> list() {
    }*/

    @GetMapping("/customer/{id}")
    public Mono<Customer> findById(@PathVariable String id) {

        return Mono.just(buildCustomer(Long.valueOf(id).longValue()));
    }


    private Customer buildCustomer(long id) {

        return Customer.builder()
                .id(id)
                .version(0)
                .lastName("lastname")
                .firstName("firstname")
                .company("company")
                .accountBalance(BigDecimal.ZERO)
                .build();
    }

}

