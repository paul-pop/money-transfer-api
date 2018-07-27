package com.revolut.api.transfers.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"id", "status", "timestamp"}, allowGetters = true)
public class Transfer {

    @NotNull
    private final Long id;

    @NotNull
    private final Long sourceAccountId;

    @NotNull
    private final Long destinationAccountId;

    @NotNull
    @Min(0)
    private final BigDecimal amount;

    @NotBlank
    private final String currency;

    @NotBlank
    private final String reference;

    @JsonSerialize(using = ToStringSerializer.class)
    private final LocalDateTime timestamp;

    // This will change so can't be immutable
    private TransferStatus status;

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
        this.currency = "GBP";
        this.reference = reference;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
}
