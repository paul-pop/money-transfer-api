package com.revolut.api.transfers;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Class that holds some constants for our tests so we don't use magic strings
 */
public final class TestConstants {

    private TestConstants() {
        // no constructor allowed
    }

    public static final String PATH_SEPARATOR = "/";
    public static final String EMPTY_JSON_LIST = "[]";

    public static final String INVALID_ACCOUNT_ID = "invalid";
    public static final long NON_EXISTENT_ACCOUNT_ID = 100L;
    public static final String SOURCE_ACCOUNT_NAME = "Source account";
    public static final String DESTINATION_ACCOUNT_NAME = "Destination account";

    public static final Currency CURRENCY = Currency.getInstance("GBP");
    public static final BigDecimal NEGATIVE_AMOUNT = BigDecimal.valueOf(-1);
    public static final BigDecimal AMOUNT_OF_0 = BigDecimal.ZERO;
    public static final BigDecimal AMOUNT_OF_10 = BigDecimal.TEN;
    public static final BigDecimal AMOUNT_OF_100 = BigDecimal.valueOf(100);
    public static final String REFERENCE = "<3";
}
