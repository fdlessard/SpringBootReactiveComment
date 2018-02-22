package io.fdlessard.codebites.reactive.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer implements Serializable {


    private long id;

    private long version;

    private String lastName;

    private String firstName;

    private String company;

    private BigDecimal accountBalance;

}
