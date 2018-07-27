package com.revolut.api.transfers.repository;

import com.revolut.api.transfers.model.Account;
import ratpack.exec.Blocking;
import ratpack.exec.Promise;

import java.util.List;
import java.util.Optional;

/**
 * Holds methods for querying/mutating the account repository
 */
public class AccountRepository {

    private final List<Account> accounts;

    public AccountRepository(final List<Account> accounts) {
        this.accounts = accounts;
    }

    /**
     * Retrieve a list of all {@link Account}s
     *
     * @return promise with the list of all accounts
     */
    public Promise<List<Account>> getAll() {
        return Blocking.get(() -> accounts);
    }

    /**
     * Retrieve an {@link Account} by id
     *
     * @param id the identifier to query by
     * @return promise with the account
     */
    public Promise<Account> getById(final Long id) {
        Optional<Account> first = accounts.stream()
            .filter(account -> id.equals(account.getId()))
            .findFirst();

        return first.isPresent() ? Blocking.get(first::get) : Blocking.get(() -> null);
    }

    /**
     * Create a new {@link Account}
     *
     * @param account the data to create with
     * @return promise with the account
     */
    public Promise<Account> create(final Account account) {
        return Blocking
            .op(() -> accounts.add(account))
            .map(() -> account);
    }
}
