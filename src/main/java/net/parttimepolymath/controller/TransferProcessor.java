package net.parttimepolymath.controller;

import java.math.BigDecimal;
import java.util.UUID;

import net.jcip.annotations.ThreadSafe;
import net.parttimepolymath.api.TransferRequest;
import net.parttimepolymath.api.TransferResult;
import net.parttimepolymath.model.Account;
import net.parttimepolymath.model.DataStore;
import net.parttimepolymath.model.Transaction;
import net.parttimepolymath.model.TransactionPK;

import org.apache.commons.lang3.Validate;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * processsor class which actually does the work of attempting a transfer.
 * 
 * @author robert
 */
@ThreadSafe
public class TransferProcessor {

    /**
     * result to return for malformed requests.
     */
    public static final TransferResult BAD_RESULT = new TransferResult(400, "Request is not well-formed", "");

    /**
     * the request being processed by this instance.
     */
    private final TransferRequest request;
    /**
     * the injected datastore.
     */
    private final DataStore dataStore;

    /**
     * primary constructor.
     * 
     * @param rqst the request to process.
     * @param store the datastore to read and write to.
     */
    public TransferProcessor(final TransferRequest rqst, final DataStore store) {
        request = rqst;
        dataStore = store;
    }

    /**
     * perform the processing.
     * 
     * @return the result of the processing, guaranteed non-null.
     */
    public TransferResult execute() {
        if (invalidRequest()) {
            return BAD_RESULT;
        }

        Account fromAccount = dataStore.getAccount(request.getFromAccount());
        if (fromAccount == null) {
            return new TransferResult(404, "From Account not found", "");
        }
        if (!fromAccount.isOpen()) {
            return new TransferResult(404, "From Account not open", "");
        }

        Account toAccount = dataStore.getAccount(request.getToAccount());
        if (toAccount == null) {
            return new TransferResult(404, "To Account not found", "");
        }
        if (!toAccount.isOpen()) {
            return new TransferResult(404, "To Account not open", "");
        }

        BigDecimal amount = convert(request.getAmount());

        // if the amount in the account is less than what we are requesting, fail out.
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            return new TransferResult(520, "Insufficient funds", "");
        }

        // create two new transactions and store them. We allow the persistence layer to take care of adjusting the balance
        Transaction fromTransaction = new Transaction();
        fromTransaction.setAmount(amount.negate());
        fromTransaction.setDate(DateTime.now(DateTimeZone.UTC));
        fromTransaction.setReference(toAccount.getAccountId());
        TransactionPK fromKey = new TransactionPK();
        fromKey.setAccountId(fromAccount.getAccountId());
        fromKey.setTxId(UUID.randomUUID().toString());
        fromTransaction.setId(fromKey);

        Transaction toTransaction = new Transaction();
        toTransaction.setAmount(amount);
        toTransaction.setDate(fromTransaction.getDate());
        toTransaction.setReference(fromTransaction.getId().getTxId());
        TransactionPK toKey = new TransactionPK();
        toKey.setAccountId(toAccount.getAccountId());
        toKey.setTxId(UUID.randomUUID().toString());
        toTransaction.setId(toKey);

        // persist the two transactions, allowing the data layer to take care of adjusting the balance.
        // note that after this call the local references to fromAccount and toAccount will be out of date.
        // if needed they can be fetched from the fromTransaction and toTransaction
        try {
            dataStore.addTransactions(fromTransaction, toTransaction);
            return new TransferResult(200, "OK", fromTransaction.getId().getTxId());
        } catch (Exception ex) {
            return new TransferResult(503, "Internal Error", "");
        }
    }

    /**
     * convert the amount to a BigDecimal. This is only a partial implementation - we should be examining the source
     * accounts currency to verify how many decimal places the value should have. Here we are hard-wiring it to 2.
     * 
     * @param amount the amount to convert.
     * @return a new BigDecimal
     */
    private BigDecimal convert(final long amount) {
        // TODO: this should be based on the currency we are transferring.
        return BigDecimal.valueOf(amount, 2);
    }

    /**
     * is the supplied request well formed?
     * 
     * @param request the request to validate.
     * @return true if the request is not well formed.
     */
    private boolean invalidRequest() {
        try {
            Validate.notNull(request);
            Validate.notBlank(request.getFromAccount());
            Validate.notBlank(request.getToAccount());
            Validate.isTrue(!request.getToAccount().equals(request.getFromAccount()));
            return false;
        } catch (Exception ex) {
            return true;
        }
    }

}
