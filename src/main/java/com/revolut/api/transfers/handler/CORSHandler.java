package com.revolut.api.transfers.handler;

import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.MutableHeaders;

/**
 * Handler used for CORS requests, coming from browsers.
 */
public class CORSHandler implements Handler {

    @Override
    public void handle(Context ctx) {
        MutableHeaders headers = ctx.getResponse().getHeaders();
        headers.set("Access-Control-Allow-Origin", "*");
        headers.set("Access-Control-Allow-Headers", "content-type,accept");
        ctx.next();
    }
}
