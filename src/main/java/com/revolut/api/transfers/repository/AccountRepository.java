package com.revolut.api.transfers.repository;

import com.revolut.api.transfers.model.Account;
import ratpack.exec.Blocking;
import ratpack.exec.Promise;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Holds methods for querying/mutating the account repository
 */
public class AccountRepository {

    private final Map<Long, Account> accounts;

    public AccountRepository(final Map<Long, Account> accounts) {
        this.accounts = accounts;
    }

    /**
     * Retrieve a list of all {@link Account}s
     *
     * @return promise with the list of all accounts
     */
    public Promise<List<Account>> getAll() {
        return Blocking.get(() -> new ArrayList<>(accounts.values()));
    }

    /**
     * Retrieve an {@link Account} by id
     *
     * @param id the identifier to query by
     * @return promise with the account
     */
    public Promise<Account> getById(final long id) {
        return Blocking.get(() -> accounts.get(id));
    }

    /**
     * Create a new {@link Account}
     *
     * @param account the data to create with
     * @return promise with the account
     */
    public Promise<Account> create(final Account account) {
        return Blocking
            .op(() -> accounts.put(account.getId(), account))
            .map(() -> account);
    }
}
