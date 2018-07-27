package com.revolut.api.transfers.model;

/**
 * Enum describing the status of the transfer:
 *
 * <ul>
 * <li>PROCESSING - This means the transfer started and it's in progress</li>
 * <li>COMPLETED - This means the transfer has completed successfully</li>
 * <li>FAILED - This means the transfer has failed to complete</li>
 * </ul>
 */
public enum TransferStatus {

    PROCESSING,
    COMPLETED,
    FAILED

}
