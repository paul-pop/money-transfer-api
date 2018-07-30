package com.revolut.api.transfers.module;

import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.revolut.api.transfers.repository.AccountRepository;
import com.revolut.api.transfers.repository.TransferRepository;

/**
 * Guice module used to register the {@link AccountRepository} and {@link TransferRepository} as singleton beans
 */
public class RepositoryModule extends AbstractModule {

    @Override
    protected void configure() {
        // No configuration needed
    }

    @Provides
    @Singleton
    public AccountRepository accountRepository() {
        return new AccountRepository(Maps.newConcurrentMap());
    }

    @Provides
    @Singleton
    public TransferRepository transferRepository() {
        return new TransferRepository(Maps.newConcurrentMap());
    }
}
