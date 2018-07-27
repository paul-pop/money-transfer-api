package com.revolut.api.transfers.handler;

import com.revolut.api.transfers.model.Transfer;
import com.revolut.api.transfers.repository.TransferRepository;
import io.netty.handler.codec.http.HttpResponseStatus;
import ratpack.exec.Promise;
import ratpack.handling.Context;
import ratpack.handling.InjectionHandler;
import ratpack.jackson.Jackson;

import javax.validation.Validator;

/**
 * This handler is for the /transfers route where we have the following endpoints:
 *
 * <ul>
 * <li>GET /transfers - retrieves a list of all transfers</li>
 * <li>POST /transfers - creates a new transfers between accounts</li>
 * </ul>
 */
public class TransferBaseHandler extends InjectionHandler {

    public void handle(Context ctx, TransferRepository repository) throws Exception {
        Validator validator = ctx.get(Validator.class);

        ctx.byMethod(method -> method
            .get(() ->
                repository.getAll()
                    .map(Jackson::json)
                    .then(ctx::render))
            .post(() -> {
                // Deserialize de request and validate it
                Promise<Transfer> transfer = ctx
                    .parse(Jackson.fromJson(Transfer.class))
                    .route(obj -> !validator.validate(obj).isEmpty(),
                        obj -> ctx.clientError(HttpResponseStatus.BAD_REQUEST.code()));

                // Save to the repository and return the response
                transfer
                    .flatMap(repository::create)
                    .map(Jackson::json)
                    .then(ctx::render);

                ctx.getResponse().status(HttpResponseStatus.CREATED.code());
            }));
    }
}
