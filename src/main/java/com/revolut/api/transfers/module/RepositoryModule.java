package com.revolut.api.transfers.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.revolut.api.transfers.repository.AccountRepository;
import com.revolut.api.transfers.repository.TransferRepository;

import javax.sql.DataSource;

/**
 * Guice module used to register the {@link AccountRepository} and {@link TransferRepository} as singletons
 */
public class RepositoryModule extends AbstractModule {

    @Override
    protected void configure() {
        // No configuration needed
    }

    @Provides
    @Singleton
    public AccountRepository accountRepository(DataSource ds) {
        return new AccountRepository(ds);
    }

    @Provides
    @Singleton
    public TransferRepository transferRepository(DataSource ds) {
        return new TransferRepository(ds);
    }
}
