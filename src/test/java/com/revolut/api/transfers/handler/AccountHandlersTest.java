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
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JUnit4.class)
public class AccountHandlersTest {

    private static final String PATH = "/accounts";

    private final MainClassApplicationUnderTest underTest = new MainClassApplicationUnderTest(MoneyTransferAPI.class);
    private final TestHttpClient testHttpClient = underTest.getHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Account account;

    @Before
    public void setUp() {
        account = new Account("Current account", BigDecimal.valueOf(0), Currency.getInstance("GBP"));
    }

    @After
    public void tearDown() {
        underTest.close();
    }

    @Test
    public void whenNoAccountsCreated_returnsEmptyListOfAccounts() {
        ReceivedResponse response = testHttpClient.get(PATH);

        assertThat(response.getStatusCode(), equalTo(OK.code()));
        assertThat(response.getBody().getText(), equalTo("[]"));
    }

    @Test
    public void givenNoId_returnsAllAccounts() {
        ReceivedResponse response = testHttpClient.get(PATH + "/");

        assertThat(response.getStatusCode(), equalTo(OK.code()));
        assertThat(response.getBody().getText(), equalTo("[]"));
    }

    @Test
    public void givenInvalidId_returns404() {
        ReceivedResponse response = testHttpClient.get(PATH + "/invalid");

        assertThat(response.getStatusCode(), equalTo(NOT_FOUND.code()));
    }

    @Test
    public void givenNonExistingId_returns404() {
        ReceivedResponse response = testHttpClient.get(PATH + "/34823473294732894");

        assertThat(response.getStatusCode(), equalTo(NOT_FOUND.code()));
    }

    @Test
    public void givenAccountWithEmptyName_returns400() {
        ReceivedResponse response = testHttpClient.request(PATH, request -> request
            .body(body -> body
                .type(MediaType.APPLICATION_JSON)
                .text(objectMapper.writeValueAsString(
                    new Account("", BigDecimal.valueOf(1), Currency.getInstance("GBP"))
                )))
            .post());

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST.code()));
    }

    @Test
    public void givenAccountWithNegativeBalance_returns400() {
        ReceivedResponse response = testHttpClient.request(PATH, request -> request
            .body(body -> body
                .type(MediaType.APPLICATION_JSON)
                .text(objectMapper.writeValueAsString(
                    new Account("Current account", BigDecimal.valueOf(-1), Currency.getInstance("GBP"))
                )))
            .post());

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST.code()));
    }

    @Test
    public void givenAccountWithNoCurrency_returns400() {
        ReceivedResponse response = testHttpClient.request(PATH, request -> request
            .body(body -> body
                .type(MediaType.APPLICATION_JSON)
                .text(objectMapper.writeValueAsString(
                    new Account("Current account", BigDecimal.valueOf(1), null)
                )))
            .post());

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST.code()));
    }

    @Test
    public void givenSingleAccount_returnsListWithOneAccount() throws IOException {
        ReceivedResponse createdAccountResponse = testHttpClient.request(PATH, request -> request
            .body(body -> body
                .type(MediaType.APPLICATION_JSON)
                .text(objectMapper.writeValueAsString(account)))
            .post());

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
        ReceivedResponse createdAccountResponse1 = testHttpClient.request(PATH, request -> request
            .body(body -> body
                .type(MediaType.APPLICATION_JSON)
                .text(objectMapper.writeValueAsString(account)))
            .post());
        ReceivedResponse createdAccountResponse2 = testHttpClient.request(PATH, request -> request
            .body(body -> body
                .type(MediaType.APPLICATION_JSON)
                .text(objectMapper.writeValueAsString(account)))
            .post());

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
        ReceivedResponse createdAccountResponse = testHttpClient.request(PATH, request -> request
            .body(body -> body
                .type(MediaType.APPLICATION_JSON)
                .text(objectMapper.writeValueAsString(account)))
            .post());

        assertThat(createdAccountResponse.getStatusCode(), equalTo(CREATED.code()));

        Account createdAccount = objectMapper.readValue(createdAccountResponse.getBody().getText(), Account.class);
        ReceivedResponse response = testHttpClient.get(PATH + "/" + createdAccount.getId());

        assertThat(response.getStatusCode(), equalTo(OK.code()));
        assertThat(response.getBody().getText(), equalTo(createdAccountResponse.getBody().getText()));
    }

}
