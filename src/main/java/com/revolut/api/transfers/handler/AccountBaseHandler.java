package com.revolut.api.transfers.handler;

import com.revolut.api.transfers.model.Account;
import com.revolut.api.transfers.repository.AccountRepository;
import ratpack.exec.Promise;
import ratpack.handling.Context;
import ratpack.handling.InjectionHandler;
import ratpack.jackson.Jackson;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * This handler is for the /accounts route where we have the following endpoints:
 *
 * <ul>
 * <li>GET /accounts - retrieves a list of all accounts</li>
 * <li>POST /accounts - creates a new account</li>
 * </ul>
 */
public class AccountBaseHandler extends InjectionHandler {

    public void handle(Context ctx, AccountRepository repository) throws Exception {
        Validator validator = ctx.get(Validator.class);

        ctx.byMethod(method -> method
            .get(() ->
                // Retrieve all accounts from the repository and serialize into JSON
                repository.getAll()
                    .map(Jackson::json)
                    .then(ctx::render))
            .post(() -> {
                // Deserialize the request and validate it
                Promise<Account> account = ctx
                    .parse(Jackson.fromJson(Account.class))
                    .route(obj -> !validator.validate(obj).isEmpty(),
                        obj -> ctx.clientError(400));

                // Save to the repository and return the JSON response
                account
                    .flatMap(repository::create)
                    .map(Jackson::json)
                    .then(ctx::render);
            }));
    }
}
