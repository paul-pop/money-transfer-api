package com.revolut.api.transfers.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Account {

    private static final AtomicLong COUNTER = new AtomicLong();

    private long id;

    @NotBlank
    private String name;

    @NotNull
    @Min(0)
    private BigDecimal balance;

    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Currency currency;

    public Account(@JsonProperty("name") final String name,
                   @JsonProperty("balance") final BigDecimal balance,
                   @JsonProperty("currency") final Currency currency) {

        this.id = COUNTER.incrementAndGet();
        this.name = name;
        this.balance = balance;
        this.currency = currency;
    }

    /**
     * Substracts the given {@link BigDecimal} from the balance
     *
     * @param amount The amount to substract
     */
    public Account withdraw(BigDecimal amount) {
        this.balance = balance.subtract(amount);
        return this;
    }

    /**
     * Adds the given {@link BigDecimal} to the balance
     *
     * @param amount The amount to add
     */
    public Account deposit(BigDecimal amount) {
        this.balance = balance.add(amount);
        return this;
    }
}
