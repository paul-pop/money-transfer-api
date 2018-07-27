package com.revolut.api.transfers;

import com.revolut.api.transfers.handler.*;
import com.revolut.api.transfers.module.RepositoryModule;
import ratpack.guice.Guice;
import ratpack.hikari.HikariModule;
import ratpack.server.RatpackServer;

import javax.validation.Validation;

/**
 * Main entry point of the app, this is where we:
 * - Start the {@link RatpackServer} (embedded Netty server)
 * - Set up the H2 connection via HikariCP
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
                .module(RepositoryModule.class)
                .module(HikariModule.class, config -> {
                    config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
                    config.addDataSourceProperty("URL", "jdbc:h2:mem:transfers;INIT=RUNSCRIPT FROM 'classpath:/tables.sql'");
                })))
            .handlers(chain -> chain
                .all(CORSHandler.class)
                .path("accounts", new AccountBaseHandler())
                .path("accounts/:id", new AccountHandler())
                .path("transfers", new TransferBaseHandler())
                .path("transfers/:id", new TransferHandler())));
    }
}
