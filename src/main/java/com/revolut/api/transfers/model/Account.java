package com.revolut.api.transfers.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Currency;

@Getter
@EqualsAndHashCode
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Account {

    private final Long id;

    @NotBlank
    private final String name;

    @NotNull
    @Min(0)
    private final BigDecimal balance;

    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull
    private final Currency currency;

    @JsonCreator
    public Account(@JsonProperty("id") final Long id,
                   @JsonProperty("name") final String name,
                   @JsonProperty("balance") final BigDecimal balance,
                   @JsonProperty("currency") final Currency currency) {

        this.id = id;
        this.name = name;
        this.balance = balance;
        this.currency = currency;
    }
}
