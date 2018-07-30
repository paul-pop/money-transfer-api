package com.revolut.api.transfers.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.api.transfers.MoneyTransferAPI;
import com.revolut.api.transfers.model.Account;
import com.revolut.api.transfers.model.Transfer;
import com.revolut.api.transfers.model.TransferState;
import com.revolut.api.transfers.model.TransferStatus;
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

import static com.revolut.api.transfers.TestConstants.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JUnit4.class)
public class TransferHandlersTest {

    private static final String PATH = "/transfers";
    private static final String ACCOUNTS_PATH = "/accounts";

    private final MainClassApplicationUnderTest underTest = new MainClassApplicationUnderTest(MoneyTransferAPI.class);
    private final TestHttpClient testHttpClient = underTest.getHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Account sourceAccount;
    private Account destinationAccount;

    @Before
    public void setUp() {
        // Create accounts and persist them
        sourceAccount = new Account(SOURCE_ACCOUNT_NAME, AMOUNT_OF_10, CURRENCY);
        destinationAccount = new Account(DESTINATION_ACCOUNT_NAME, AMOUNT_OF_100, CURRENCY);

        testHttpClient.request(ACCOUNTS_PATH, request -> request
            .body(body -> body
                .type(MediaType.APPLICATION_JSON)
                .text(objectMapper.writeValueAsString(sourceAccount)))
            .post());

        testHttpClient.request(ACCOUNTS_PATH, request -> request
            .body(body -> body
                .type(MediaType.APPLICATION_JSON)
                .text(objectMapper.writeValueAsString(destinationAccount)))
            .post());
    }

    @After
    public void tearDown() {
        underTest.close();
    }

    @Test
    public void whenNoTransfersCreated_returnsEmptyListOfTransfers() {
        ReceivedResponse response = testHttpClient.get(PATH);

        assertThat(response.getStatusCode(), equalTo(OK.code()));
        assertThat(response.getBody().getText(), equalTo(EMPTY_JSON_LIST));
    }

    @Test
    public void givenNoId_returnsAllTransfers() {
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
    public void givenTransferWithNegativeAmount_returns400() {
        ReceivedResponse response = createTransfer(
            new Transfer(sourceAccount.getId(), destinationAccount.getId(), NEGATIVE_AMOUNT, CURRENCY, REFERENCE));

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST.code()));
    }

    @Test
    public void givenTransferWithNoCurrency_returns400() {
        ReceivedResponse response = createTransfer(
            new Transfer(sourceAccount.getId(), destinationAccount.getId(), AMOUNT_OF_10, null, REFERENCE));

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST.code()));
    }

    @Test
    public void givenTransferWithEmptyReference_returns400() {
        ReceivedResponse response = createTransfer(
            new Transfer(sourceAccount.getId(), destinationAccount.getId(), AMOUNT_OF_10, CURRENCY, ""));

        assertThat(response.getStatusCode(), equalTo(BAD_REQUEST.code()));
    }

    @Test
    public void givenTransferWith0Amount_returnsFailedTransfer() throws IOException {
        ReceivedResponse createdTransferResponse = createTransfer(
            new Transfer(sourceAccount.getId(), destinationAccount.getId(), AMOUNT_OF_0, CURRENCY, REFERENCE));

        assertThat(createdTransferResponse.getStatusCode(), equalTo(CREATED.code()));

        Transfer createdTransfer = objectMapper.readValue(createdTransferResponse.getBody().getText(), Transfer.class);
        ReceivedResponse response = testHttpClient.get(PATH + PATH_SEPARATOR + createdTransfer.getId());
        Transfer parsedResponse = objectMapper.readValue(response.getBody().getText(), Transfer.class);

        assertThat(response.getStatusCode(), equalTo(OK.code()));
        assertTransfers(parsedResponse, createdTransfer);
        assertThat(parsedResponse.getStatus(), equalTo(TransferStatus.builder()
            .state(TransferState.FAILED)
            .reason("Transfer amount must be greater than 0")
            .build()
        ));
    }

    @Test
    public void givenTransferWithNonExistingSourceAccount_returnsFailedTransfer() throws IOException {
        ReceivedResponse createdTransferResponse = createTransfer(
            new Transfer(NON_EXISTENT_ACCOUNT_ID, destinationAccount.getId(), AMOUNT_OF_10, CURRENCY, REFERENCE));

        assertThat(createdTransferResponse.getStatusCode(), equalTo(CREATED.code()));

        Transfer createdTransfer = objectMapper.readValue(createdTransferResponse.getBody().getText(), Transfer.class);
        ReceivedResponse response = testHttpClient.get(PATH + PATH_SEPARATOR + createdTransfer.getId());
        Transfer parsedResponse = objectMapper.readValue(response.getBody().getText(), Transfer.class);

        assertThat(response.getStatusCode(), equalTo(OK.code()));
        assertTransfers(parsedResponse, createdTransfer);
        assertThat(parsedResponse.getStatus(), equalTo(TransferStatus.builder()
            .state(TransferState.FAILED)
            .reason("Account does not exist")
            .build()
        ));
    }

    @Test
    public void givenTransferWithNonExistingDestinationAccount_returnsFailedTransfer() throws IOException {
        ReceivedResponse createdTransferResponse = createTransfer(
            new Transfer(sourceAccount.getId(), NON_EXISTENT_ACCOUNT_ID, AMOUNT_OF_10, CURRENCY, REFERENCE));

        assertThat(createdTransferResponse.getStatusCode(), equalTo(CREATED.code()));

        Transfer createdTransfer = objectMapper.readValue(createdTransferResponse.getBody().getText(), Transfer.class);
        ReceivedResponse response = testHttpClient.get(PATH + PATH_SEPARATOR + createdTransfer.getId());
        Transfer parsedResponse = objectMapper.readValue(response.getBody().getText(), Transfer.class);

        assertThat(response.getStatusCode(), equalTo(OK.code()));
        assertTransfers(parsedResponse, createdTransfer);
        assertThat(parsedResponse.getStatus(), equalTo(TransferStatus.builder()
            .state(TransferState.FAILED)
            .reason("Account does not exist")
            .build()
        ));
    }

    @Test
    public void givenTransferInTheSameAccount_returnsFailedTransfer() throws IOException {
        ReceivedResponse createdTransferResponse = createTransfer(
            new Transfer(sourceAccount.getId(), sourceAccount.getId(), AMOUNT_OF_10, CURRENCY, REFERENCE));

        assertThat(createdTransferResponse.getStatusCode(), equalTo(CREATED.code()));

        Transfer createdTransfer = objectMapper.readValue(createdTransferResponse.getBody().getText(), Transfer.class);
        ReceivedResponse response = testHttpClient.get(PATH + PATH_SEPARATOR + createdTransfer.getId());
        Transfer parsedResponse = objectMapper.readValue(response.getBody().getText(), Transfer.class);

        assertThat(response.getStatusCode(), equalTo(OK.code()));
        assertTransfers(parsedResponse, createdTransfer);
        assertThat(parsedResponse.getStatus(), equalTo(TransferStatus.builder()
            .state(TransferState.FAILED)
            .reason("The transfer must be between two different accounts")
            .build()
        ));
    }

    @Test
    public void givenSourceAccountHasInsufficientFunds_returnsFailedTransfer() throws IOException {
        ReceivedResponse createdTransferResponse = createTransfer(
            new Transfer(sourceAccount.getId(), destinationAccount.getId(), BigDecimal.valueOf(10.01), CURRENCY, REFERENCE));

        assertThat(createdTransferResponse.getStatusCode(), equalTo(CREATED.code()));

        Transfer createdTransfer = objectMapper.readValue(createdTransferResponse.getBody().getText(), Transfer.class);
        ReceivedResponse response = testHttpClient.get(PATH + PATH_SEPARATOR + createdTransfer.getId());
        Transfer parsedResponse = objectMapper.readValue(response.getBody().getText(), Transfer.class);

        assertThat(response.getStatusCode(), equalTo(OK.code()));
        assertTransfers(parsedResponse, createdTransfer);
        assertThat(parsedResponse.getStatus(), equalTo(TransferStatus.builder()
            .state(TransferState.FAILED)
            .reason("Not enough funds to transfer")
            .build()
        ));
    }

    @Test
    public void givenSourceAccountHasExactAmountOfFunds_returnsCompletedTransfer() throws IOException {
        ReceivedResponse createdTransferResponse = createTransfer(
            new Transfer(sourceAccount.getId(), destinationAccount.getId(), AMOUNT_OF_10, CURRENCY, REFERENCE));

        assertThat(createdTransferResponse.getStatusCode(), equalTo(CREATED.code()));

        Transfer createdTransfer = objectMapper.readValue(createdTransferResponse.getBody().getText(), Transfer.class);
        ReceivedResponse response = testHttpClient.get(PATH + PATH_SEPARATOR + createdTransfer.getId());
        Transfer parsedResponse = objectMapper.readValue(response.getBody().getText(), Transfer.class);

        assertThat(response.getStatusCode(), equalTo(OK.code()));
        assertTransfers(parsedResponse, createdTransfer);
        assertThat(parsedResponse.getStatus(), equalTo(TransferStatus.builder()
            .state(TransferState.COMPLETED)
            .reason("All good 👍")
            .build()
        ));

        assertAccountBalances(sourceAccount.getId(), destinationAccount.getId(), AMOUNT_OF_0, BigDecimal.valueOf(110));
    }

    @Test
    public void givenSourceAccountHasFundsLeftToSpare_returnsCompletedTransfer() throws IOException {
        ReceivedResponse createdTransferResponse = createTransfer(
            new Transfer(sourceAccount.getId(), destinationAccount.getId(), BigDecimal.valueOf(9.99), CURRENCY, REFERENCE));

        assertThat(createdTransferResponse.getStatusCode(), equalTo(CREATED.code()));

        Transfer createdTransfer = objectMapper.readValue(createdTransferResponse.getBody().getText(), Transfer.class);
        ReceivedResponse response = testHttpClient.get(PATH + PATH_SEPARATOR + createdTransfer.getId());
        Transfer parsedResponse = objectMapper.readValue(response.getBody().getText(), Transfer.class);

        assertThat(response.getStatusCode(), equalTo(OK.code()));
        assertTransfers(parsedResponse, createdTransfer);
        assertThat(parsedResponse.getStatus(), equalTo(TransferStatus.builder()
            .state(TransferState.COMPLETED)
            .reason("All good 👍")
            .build()
        ));

        assertAccountBalances(sourceAccount.getId(), destinationAccount.getId(), BigDecimal.valueOf(0.01), BigDecimal.valueOf(109.99));
    }

    /**
     * Utility method used to create a given {@link Transfer}
     */
    private ReceivedResponse createTransfer(Transfer transfer) {
        return testHttpClient.request(PATH, request -> request
            .body(body -> body
                .type(MediaType.APPLICATION_JSON)
                .text(objectMapper.writeValueAsString(transfer)))
            .post());
    }

    /**
     * Utility method that asserts the balances have been updated correctly
     *
     * @param sourceAccountId           account id of the source
     * @param destinationAccountId      account id of the destination
     * @param sourceAccountBalance      account balance after withdrawal
     * @param destinationAccountBalance account balance after deposit
     * @throws IOException
     */
    private void assertAccountBalances(long sourceAccountId, long destinationAccountId,
                                       BigDecimal sourceAccountBalance, BigDecimal destinationAccountBalance) throws IOException {
        ReceivedResponse sourceAccountResponse = testHttpClient.get(ACCOUNTS_PATH + PATH_SEPARATOR + sourceAccountId);
        ReceivedResponse destinationAccountResponse = testHttpClient.get(ACCOUNTS_PATH + PATH_SEPARATOR + destinationAccountId);
        Account parsedSourceAccountResponse = objectMapper.readValue(sourceAccountResponse.getBody().getText(), Account.class);
        Account parsedDestinationAccountResponse = objectMapper.readValue(destinationAccountResponse.getBody().getText(), Account.class);

        assertThat(parsedSourceAccountResponse.getId(), equalTo(sourceAccountId));
        assertThat(parsedSourceAccountResponse.getBalance(), equalTo(sourceAccountBalance));
        assertThat(parsedDestinationAccountResponse.getId(), equalTo(destinationAccountId));
        assertThat(parsedDestinationAccountResponse.getBalance(), equalTo(destinationAccountBalance));
    }

    /**
     * Utility method that asserts the transfers have been updated correctly
     *
     * @param actual   response transfer
     * @param expected request transfer
     */
    private void assertTransfers(Transfer actual, Transfer expected) {
        assertThat(actual.getId(), equalTo(expected.getId()));
        assertThat(actual.getDestinationAccountId(), equalTo(expected.getDestinationAccountId()));
        assertThat(actual.getSourceAccountId(), equalTo(expected.getSourceAccountId()));
        assertThat(actual.getAmount(), equalTo(expected.getAmount()));
        assertThat(actual.getCurrency(), equalTo(expected.getCurrency()));
        assertThat(actual.getReference(), equalTo(expected.getReference()));
        assertThat(actual.getTimestamp(), notNullValue());
    }

}
