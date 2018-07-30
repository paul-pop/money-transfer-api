package com.revolut.api.transfers.service;

import com.revolut.api.transfers.model.Account;
import com.revolut.api.transfers.model.Transfer;
import com.revolut.api.transfers.model.TransferState;
import com.revolut.api.transfers.model.TransferStatus;
import com.revolut.api.transfers.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.exec.Blocking;
import ratpack.exec.Promise;

import java.math.BigDecimal;

/**
 * Service used to validate the transfer of balances between two {@link Account}s and withdraw/deposit the amount
 */
public class TransferService {

    private static final Logger logger = LoggerFactory.getLogger(TransferService.class);

    private final AccountRepository accountRepository;

    public TransferService(final AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Perform a transfer and returns a promise. It will only perform the transfer if:
     * - The amount to transfer is greater than 0 (can be 0.01)
     * - Both accounts exist in the repository and can be retrieved
     * - The source account has sufficient funds to perform the transfer
     *
     * @param transfer transfer to be performed
     * @return promise of that transfer
     */
    public Promise<Transfer> transfer(Transfer transfer) {
        return Blocking
            .get(() -> {
                BigDecimal transferAmount = transfer.getAmount();
                if (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Transfer amount must be greater than 0");
                }

                Account sourceAccount = Blocking.on(accountRepository.getById(transfer.getSourceAccountId()));
                Account destinationAccount = Blocking.on(accountRepository.getById(transfer.getDestinationAccountId()));
                if (sourceAccount == null || destinationAccount == null) {
                    throw new IllegalArgumentException("Account does not exist");
                }
                if (sourceAccount.equals(destinationAccount)) {
                    throw new IllegalArgumentException("The transfer must be between two different accounts");
                }
                if (sourceAccount.getBalance().compareTo(transferAmount) < 0) {
                    throw new IllegalArgumentException("Not enough funds to transfer");
                }

                sourceAccount.withdraw(transferAmount);
                destinationAccount.deposit(transferAmount);

                return completed(transfer);
            })
            .mapError(IllegalArgumentException.class, ex -> {
                logger.warn("Error occurred during transfer", ex);
                return failed(transfer, ex.getMessage());
            })
            .mapError(Exception.class, ex -> {
                logger.error("Server exception occurred during transfer", ex);
                return failed(transfer, "Something terribly wrong happened on the server");
            });
    }

    /**
     * Set the status of the transfer to {@code TransferState.FAILED} with the given reason
     *
     * @param transfer transfer to change
     * @param reason   reason for the failed transfer
     * @return changed transfer
     */
    private Transfer failed(Transfer transfer, String reason) {
        transfer.setStatus(TransferStatus.builder()
            .state(TransferState.FAILED)
            .reason(reason)
            .build());
        return transfer;
    }

    /**
     * Set the status of the transfer to {@code TransferState.COMPLETED}
     *
     * @param transfer transfer to change
     * @return changed transfer
     */
    private Transfer completed(Transfer transfer) {
        transfer.setStatus(TransferStatus.builder()
            .state(TransferState.COMPLETED)
            .reason("All good 👍")
            .build());
        return transfer;
    }
}
