package com.revolut.api.transfers.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"id"}, allowGetters = true)
public class Account {

    private final Long id;

    @NotBlank
    private final String name;

    @NotNull
    @Min(0)
    private final BigDecimal balance;

    @NotBlank
    private final String currency;

    @JsonCreator
    public Account(@JsonProperty("id") final Long id,
                   @JsonProperty("name") final String name,
                   @JsonProperty("balance") final BigDecimal balance,
                   @JsonProperty("currency") final String currency) {

        this.id = id;
        this.name = name;
        this.balance = balance;
        this.currency = "GBP";
    }
}
