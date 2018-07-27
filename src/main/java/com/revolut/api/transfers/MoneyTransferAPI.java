package com.revolut.api.transfers;

import com.revolut.api.transfers.handler.*;
import com.revolut.api.transfers.module.RepositoryModule;
import ratpack.guice.Guice;
import ratpack.server.RatpackServer;

import javax.validation.Validation;

/**
 * Main entry point of the app, this is where we:
 * - Start the {@link RatpackServer} (embedded Netty server)
 * - Register handlers
 * - Register validators
 */
public class MoneyTransferAPI {

    public static void main(String[] args) throws Exception {
        // Default all time to GMT
        System.setProperty("user.timezone", "GMT");

        // Configure Ratpack server
        RatpackServer.start(server -> server
            .registry(Guice.registry(bindings -> bindings
                .bindInstance(new CORSHandler())
                .bindInstance(Validation.buildDefaultValidatorFactory().getValidator())
                .module(RepositoryModule.class)))
            .handlers(chain -> chain
                .all(CORSHandler.class)
                .path("accounts", new AccountBaseHandler())
                .path("accounts/:id", new AccountHandler())
                .path("transfers", new TransferBaseHandler())
                .path("transfers/:id", new TransferHandler())));
    }
}
