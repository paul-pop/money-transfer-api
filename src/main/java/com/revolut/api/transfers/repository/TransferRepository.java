package com.revolut.api.transfers.repository;

import com.revolut.api.transfers.model.Transfer;
import com.revolut.api.transfers.model.TransferState;
import com.revolut.api.transfers.model.TransferStatus;
import ratpack.exec.Blocking;
import ratpack.exec.Promise;

import java.util.List;
import java.util.Optional;

/**
 * Holds methods for querying/mutating the transfer repository
 */
public class TransferRepository {

    private final List<Transfer> transfers;

    public TransferRepository(final List<Transfer> transfers) {
        this.transfers = transfers;
    }

    /**
     * Retrieve a list of all {@link Transfer}s
     *
     * @return promise with the list of all transfers
     */
    public Promise<List<Transfer>> getAll() {
        return Blocking.get(() -> transfers);
    }

    /**
     * Retrieve an {@link Transfer} by id
     *
     * @param id the identifier to query by
     * @return promise with the transfer
     */
    public Promise<Transfer> getById(final Long id) {
        Optional<Transfer> first = transfers.stream()
            .filter(transfer -> id.equals(transfer.getId()))
            .findFirst();

        return first.isPresent() ? Blocking.get(first::get) : Blocking.get(() -> null);
    }

    /**
     * Create a new {@link Transfer}
     *
     * @param transfer the data to create with
     * @return promise with the transfer
     */
    public Promise<Transfer> create(final Transfer transfer) {
        return Blocking
            .op(() -> transfer.setStatus(new TransferStatus(TransferState.PROCESSING, null)))
            .next(() -> transfers.add(transfer))
            .map(() -> transfer);
    }
}
