package com.revolut.api.transfers.repository;

import com.revolut.api.transfers.model.Account;
import jooq.tables.records.AccountRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import ratpack.exec.Blocking;
import ratpack.exec.Promise;

import javax.sql.DataSource;
import java.util.List;

import static jooq.tables.Account.ACCOUNT;
import static org.jooq.SQLDialect.H2;

/**
 * Holds methods for querying/mutating the account repository
 */
public class AccountRepository {

    private final DSLContext ctx;

    public AccountRepository(final DataSource ds) {
        this.ctx = DSL.using(ds, H2);
    }

    /**
     * Retrieve a list of all {@link Account}
     *
     * @return promise with the list of all accounts
     */
    public Promise<List<Account>> getAll() {
        SelectJoinStep<Record> selected = ctx.select().from(ACCOUNT);
        return Blocking.get(() -> selected.fetchInto(Account.class));
    }

    /**
     * Retrieve an {@link Account} by id
     *
     * @param id the identifier to query by
     * @return promise with the account
     */
    public Promise<Account> getById(final Long id) {
        Record record = ctx.select().from(ACCOUNT).where(ACCOUNT.ID.equal(id)).fetchOne();
        return record == null ? Blocking.get(() -> null) : Blocking.get(() -> record.into(Account.class));
    }

    /**
     * Create a new {@link Account}
     *
     * @param account the data to create with
     * @return promise with the account
     */
    public Promise<Account> create(final Account account) {
        AccountRecord accountRecord = ctx.newRecord(ACCOUNT, account);
        return Blocking.op(accountRecord::store)
            .next(Blocking.op(accountRecord::refresh))
            .map(() -> accountRecord.into(Account.class));
    }
}
