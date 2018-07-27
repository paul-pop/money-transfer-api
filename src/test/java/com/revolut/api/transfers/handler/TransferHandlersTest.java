package com.revolut.api.transfers.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.api.transfers.MoneyTransferAPI;
import com.revolut.api.transfers.model.Account;
import com.revolut.api.transfers.model.Transfer;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ratpack.test.MainClassApplicationUnderTest;
import ratpack.test.http.TestHttpClient;

import java.math.BigDecimal;
import java.util.Currency;

@RunWith(JUnit4.class)
public class TransferHandlersTest {

    private static final String PATH = "/transfers";

    private final MainClassApplicationUnderTest underTest = new MainClassApplicationUnderTest(MoneyTransferAPI.class);
    private final TestHttpClient testHttpClient = underTest.getHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Account sourceAccount;
    private Account destinationAccount;
    private Transfer transfer;

    @Before
    public void setUp() {
        sourceAccount = new Account("Current account", BigDecimal.valueOf(10), Currency.getInstance("GBP"));
        destinationAccount = new Account("Savings account", BigDecimal.valueOf(100), Currency.getInstance("GBP"));

        transfer = new Transfer(
            sourceAccount.getId(),
            destinationAccount.getId(),
            BigDecimal.valueOf(9.99),
            Currency.getInstance("GBP"),
            "<3");
    }

    @After
    public void tearDown() {
        underTest.close();
    }

}
