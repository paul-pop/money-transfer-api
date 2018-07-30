package com.revolut.api.transfers.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transfer {

    private static final AtomicLong COUNTER = new AtomicLong();

    private long id;

    private long sourceAccountId;

    private long destinationAccountId;

    @NotNull
    @Min(0)
    private BigDecimal amount;

    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Currency currency;

    @NotBlank
    private String reference;

    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime timestamp;

    private TransferStatus status;

    public Transfer(@JsonProperty("sourceAccountId") final long sourceAccountId,
                    @JsonProperty("destinationAccountId") final long destinationAccountId,
                    @JsonProperty("amount") final BigDecimal amount,
                    @JsonProperty("currency") final Currency currency,
                    @JsonProperty("reference") final String reference) {

        this.id = COUNTER.incrementAndGet();
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.currency = currency;
        this.reference = reference;
        this.timestamp = LocalDateTime.now();
    }
}
