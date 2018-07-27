package com.revolut.api.transfers.repository;

import com.revolut.api.transfers.model.Transfer;
import jooq.tables.records.TransferRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import ratpack.exec.Blocking;
import ratpack.exec.Promise;

import javax.sql.DataSource;
import java.util.List;

import static jooq.tables.Transfer.TRANSFER;
import static org.jooq.SQLDialect.H2;

/**
 * Holds methods for querying/mutating the transfer repository
 */
public class TransferRepository {

    private final DSLContext ctx;

    public TransferRepository(final DataSource ds) {
        this.ctx = DSL.using(ds, H2);
    }

    public Promise<List<Transfer>> getAll() {
        SelectJoinStep<Record> selected = ctx.select().from(TRANSFER);
        return Blocking.get(() -> selected.fetchInto(Transfer.class));
    }

    public Promise<Transfer> getById(final Long id) {
        Record record = ctx.select().from(TRANSFER).where(TRANSFER.ID.equal(id)).fetchOne();
        return record == null ? Blocking.get(() -> null) : Blocking.get(() -> record.into(Transfer.class));
    }

    public Promise<Transfer> create(final Transfer transfer) {
        TransferRecord transferRecord = ctx.newRecord(TRANSFER, transfer);
        return Blocking.op(transferRecord::store)
            .next(Blocking.op(transferRecord::refresh))
            .map(() -> transferRecord.into(Transfer.class));
    }
}
