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
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"id", "status", "timestamp"}, allowGetters = true)
public class Transfer {

    private final Long id;
    private final Long sourceAccountId;
    private final Long destinationAccountId;
    private final BigDecimal amount;
    private final String currency;
    private final String reference;
    private final TransferStatus status;
    private final LocalDateTime timestamp;

    @JsonCreator
    public Transfer(@JsonProperty("id") final Long id,
                    @JsonProperty("sourceAccountId") final Long sourceAccountId,
                    @JsonProperty("destinationAccountId") final Long destinationAccountId,
                    @JsonProperty("amount") final BigDecimal amount,
                    @JsonProperty("currency") final String currency,
                    @JsonProperty("reference") final String reference,
                    @JsonProperty("status") final TransferStatus status,
                    @JsonProperty("timestamp") final LocalDateTime timestamp) {

        this.id = id;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.currency = currency;
        this.reference = reference;
        this.status = status;
        this.timestamp = timestamp;
    }
}
