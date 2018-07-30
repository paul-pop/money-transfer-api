package com.revolut.api.transfers.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.api.transfers.MoneyTransferAPI;
import com.revolut.api.transfers.model.Account;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ratpack.http.MediaType;
import ratpack.http.client.ReceivedResponse;
import ratpack.test.MainClassApplicationUnderTest;
import ratpack.test.http.TestHttpClient;

import java.io.IOException;
import java.util.List;

import static com.revolut.api.transfers.TestConstants.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JUnit4.class)
public class AccountHandlersTest {

    private static final String PATH = "/accounts";

    private final MainClassApplicationUnderTest underTest = new MainClassApplicationUnderTest(MoneyTransferAPI.class);
    private final TestHttpClient testHttpClient = underTest.getHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Account account1;
    private Account account2;

    @Before
    public void setUp() {
        account1 = new Account(SOURCE_ACCOUNT_NAME, AMOUNT_OF_0, CURRENCY);
        account2 = new Account(DESTINATION_ACCOUNT_NAME, AMOUNT_OF_10, CURRENCY);
    }

    @After
    public void tearDown() {
        underTest.close();
    }

    @Test
    public void whenNoAccountsCreated_returnsEmptyListOfAccounts() {
        ReceivedResponse response = testHttpClient.get(PATH);

        assertThat(response.getStatusCode(), equalTo(OK.code()));
        assertThat(response.getBody().getText(), equalTo(EMPTY_JSON_LIST));
    }

    @Test
    public void givenNoId_returnsAllAccounts() {
        ReceivedResponse response = testHttpClient.get(PATH + PATH_SEPARATOR);

        assertThat(response.getStatusCode(), equalTo(OK.code()));
        assertThat(response.getBody().getText(), equalTo(EMPTY_JSON_LIST));
    }

    @Test
    public void givenInvalidId_returns404() {
        ReceivedResponse response = testHttpClient.get(PATH + PATH_SEPARATOR + INVALID_ACCOUNT_ID);

        assertThat(response.getStatusCode(), equalTo(NOT_FOUND.code()));
    }

    @Test
    public void givenNonExistingId_returns404() {
        ReceivedResponse response = testHttpClient.get(PATH + PATH_SEPARATOR + NON_EXISTENT_ACCOUNT_ID);

        assertThat(response.getStatusCode(), equalTo(NOT_FOUND.code()));
    }

    @Test
    public void givenAccountWithEmptyName_returns400() {
        ReceivedResponse response = createAccount(new Account("", AMOUNT_OF_10, CURRENCY));

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST.code()));
    }

    @Test
    public void givenAccountWithNegativeBalance_returns400() {
        ReceivedResponse response = createAccount(new Account(SOURCE_ACCOUNT_NAME, NEGATIVE_AMOUNT, CURRENCY));

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST.code()));
    }

    @Test
    public void givenAccountWithNoCurrency_returns400() {
        ReceivedResponse response = createAccount(new Account(SOURCE_ACCOUNT_NAME, AMOUNT_OF_10, null));

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST.code()));
    }

    @Test
    public void givenSingleAccount_returnsListWithOneAccount() throws IOException {
        ReceivedResponse createdAccountResponse = createAccount(account1);

        assertThat(createdAccountResponse.getStatusCode(), equalTo(CREATED.code()));

        Account createdAccount = objectMapper.readValue(createdAccountResponse.getBody().getText(), Account.class);
        ReceivedResponse response = testHttpClient.get(PATH);
        List<Account> accounts = objectMapper.readValue(response.getBody().getText(), new TypeReference<List<Account>>() {
        });

        assertThat(response.getStatusCode(), equalTo(OK.code()));
        assertThat(accounts.size(), equalTo(1));
        assertThat(accounts.get(0), equalTo(createdAccount));
    }

    @Test
    public void givenMultipleAccounts_returnsListWithMultipleAccount() throws IOException {
        ReceivedResponse createdAccountResponse1 = createAccount(account1);
        ReceivedResponse createdAccountResponse2 = createAccount(account2);

        assertThat(createdAccountResponse1.getStatusCode(), equalTo(CREATED.code()));
        assertThat(createdAccountResponse2.getStatusCode(), equalTo(CREATED.code()));

        Account createdAccount1 = objectMapper.readValue(createdAccountResponse1.getBody().getText(), Account.class);
        Account createdAccount2 = objectMapper.readValue(createdAccountResponse2.getBody().getText(), Account.class);
        ReceivedResponse response = testHttpClient.get(PATH);
        List<Account> accounts = objectMapper.readValue(response.getBody().getText(), new TypeReference<List<Account>>() {
        });

        assertThat(response.getStatusCode(), equalTo(OK.code()));
        assertThat(accounts.size(), equalTo(2));
        assertThat(accounts.get(0), equalTo(createdAccount1));
        assertThat(accounts.get(1), equalTo(createdAccount2));
    }

    @Test
    public void givenSingleAccount_returnsSingleAccount() throws IOException {
        ReceivedResponse createdAccountResponse = createAccount(account1);

        assertThat(createdAccountResponse.getStatusCode(), equalTo(CREATED.code()));

        Account createdAccount = objectMapper.readValue(createdAccountResponse.getBody().getText(), Account.class);
        ReceivedResponse response = testHttpClient.get(PATH + PATH_SEPARATOR + createdAccount.getId());

        assertThat(response.getStatusCode(), equalTo(OK.code()));
        assertThat(response.getBody().getText(), equalTo(createdAccountResponse.getBody().getText()));
    }

    /**
     * Utility method used to create a given {@link Account}
     */
    private ReceivedResponse createAccount(Account account) {
        return testHttpClient.request(PATH, request -> request
            .body(body -> body
                .type(MediaType.APPLICATION_JSON)
                .text(objectMapper.writeValueAsString(account)))
            .post());
    }

}
