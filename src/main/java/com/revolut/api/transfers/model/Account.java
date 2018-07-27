package com.revolut.api.transfers.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@EqualsAndHashCode
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"id"}, allowGetters = true)
public class Account {

    private final Long id;
    private final String name;
    private final BigDecimal balance;
    private final String currency;

    @JsonCreator
    public Account(@JsonProperty("id") final Long id,
                   @JsonProperty("name") final String name,
                   @JsonProperty("balance") final BigDecimal balance,
                   @JsonProperty("currency") final String currency) {

        this.id = id;
        this.name = name;
        this.balance = balance;
        this.currency = currency;
    }
}
