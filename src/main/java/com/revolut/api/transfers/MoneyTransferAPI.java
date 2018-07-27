package com.revolut.api.transfers;

import com.revolut.api.transfers.handler.*;
import com.revolut.api.transfers.module.RepositoryModule;
import ratpack.guice.Guice;
import ratpack.hikari.HikariModule;
import ratpack.server.RatpackServer;

/**
 * Main entry point of the app, this is where we start the {@link RatpackServer} (embedded Netty server), set up the H2
 * connection and add the path handlers.
 */
public class MoneyTransferAPI {

    public static void main(String[] args) throws Exception {
        // Default all time to GMT
        System.setProperty("user.timezone", "GMT");

        // Configure Ratpack server
        RatpackServer.start(server -> server
            .registry(Guice.registry(bindings -> bindings
                .bindInstance(new CORSHandler())
                .module(RepositoryModule.class)
                .module(HikariModule.class, config -> {
                    config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
                    config.addDataSourceProperty("URL", "jdbc:h2:mem:transfers;INIT=RUNSCRIPT FROM 'classpath:/tables.sql'");
                })))
            .handlers(chain -> chain
                .all(CORSHandler.class)
                .path("accounts", new AccountBaseHandler())
                .path("accounts/:id", new AccountIdHandler())
                .path("transfers", new TransferBaseHandler())
                .path("transfers/:id", new TransferIdHandler())));
    }
}
