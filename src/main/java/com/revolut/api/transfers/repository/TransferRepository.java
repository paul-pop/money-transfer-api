package com.revolut.api.transfers.repository;

import com.revolut.api.transfers.model.Transfer;
import ratpack.exec.Blocking;
import ratpack.exec.Promise;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Holds methods for querying/mutating the transfer repository
 */
public class TransferRepository {

    private final Map<Long, Transfer> transfers;

    public TransferRepository(final Map<Long, Transfer> transfers) {
        this.transfers = transfers;
    }

    /**
     * Retrieve a list of all {@link Transfer}s
     *
     * @return promise with the list of all transfers
     */
    public Promise<List<Transfer>> getAll() {
        return Blocking.get(() -> new ArrayList<>(transfers.values()));
    }

    /**
     * Retrieve an {@link Transfer} by id
     *
     * @param id the identifier to query by
     * @return promise with the transfer
     */
    public Promise<Transfer> getById(final long id) {
        return Blocking.get(() -> transfers.get(id));
    }

    /**
     * Create a new {@link Transfer}
     *
     * @param transfer the data to create with
     * @return promise with the transfer
     */
    public Promise<Transfer> create(final Transfer transfer) {
        return Blocking
            .op(() -> transfers.putIfAbsent(transfer.getId(), transfer))
            .map(() -> transfer);
    }
}
