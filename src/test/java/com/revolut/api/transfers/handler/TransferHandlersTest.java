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
        sourceAccount = Account.builder()
            .id(1L)
            .name("Current Account")
            .balance(BigDecimal.valueOf(10))
            .currency(Currency.getInstance("GBP"))
            .build();

        destinationAccount = Account.builder()
            .id(2L)
            .name("Savings Account")
            .balance(BigDecimal.valueOf(100))
            .currency(Currency.getInstance("GBP"))
            .build();

        transfer = Transfer.builder()
            .sourceAccountId(sourceAccount.getId())
            .destinationAccountId(destinationAccount.getId())
            .amount(BigDecimal.valueOf(9.99))
            .currency(Currency.getInstance("GBP"))
            .reference("<3")
            .build();
    }

    @After
    public void tearDown() {
        underTest.close();
    }
}
