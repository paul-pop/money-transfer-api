package com.revolut.api.transfers.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferStatus {

    private final TransferState state;
    private final String reason;

    @JsonCreator
    public TransferStatus(@JsonProperty("state") final TransferState state,
                          @JsonProperty("reason") final String reason) {
        this.state = state;
        this.reason = reason;
    }

}
