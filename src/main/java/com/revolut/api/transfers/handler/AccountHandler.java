package com.revolut.api.transfers.handler;

import com.revolut.api.transfers.repository.AccountRepository;
import io.netty.handler.codec.http.HttpResponseStatus;
import ratpack.handling.Context;
import ratpack.handling.InjectionHandler;
import ratpack.jackson.Jackson;

/**
 * This handler is for the /accounts/:id route where we have the following endpoints:
 *
 * <ul>
 * <li>GET /accounts/id - retrieves a specific account by id</li>
 * </ul>
 */
public class AccountHandler extends InjectionHandler {

    public void handle(Context ctx, AccountRepository repository) throws Exception {
        try {
            Long id = Long.parseLong(ctx.getPathTokens().get("id"));
            ctx.byMethod(method -> method
                .get(() ->
                    repository.getById(id)
                        .onNull(() -> ctx.clientError(HttpResponseStatus.NOT_FOUND.code()))
                        .map(Jackson::json)
                        .then(ctx::render)));
        } catch (NumberFormatException ex) {
            ctx.clientError(HttpResponseStatus.NOT_FOUND.code());
        }

    }
}
