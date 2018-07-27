package com.revolut.api.transfers.handler;

import com.revolut.api.transfers.repository.TransferRepository;
import io.netty.handler.codec.http.HttpResponseStatus;
import ratpack.handling.Context;
import ratpack.handling.InjectionHandler;
import ratpack.jackson.Jackson;

/**
 * This handler is for the /transfers/:id route where we have the following endpoints:
 *
 * <ul>
 * <li>GET /transfers/id - retrieves a specific transfer by id</li>
 * </ul>
 */
public class TransferHandler extends InjectionHandler {

    public void handle(Context ctx, TransferRepository repository) throws Exception {
        try {
            long id = Long.parseLong(ctx.getPathTokens().get("id"));

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
