package net.parttimepolymath.model;

import java.util.List;

/**
 * this represents a facility for getting the model entities into and out of a persistent store.
 * 
 * @author robert
 */
public interface DataStore {
    /**
     * get all clients.
     * 
     * @return a possibly empty set of clients.
     */
    List<Client> getClients();

    /**
     * get a specific client.
     * 
     * @param clientId the client identifier.
     * @return the client if it can be found, null otherwise.
     */
    Client getClient(String clientId);

    /**
     * get all accounts for a client.
     * 
     * @param clientId the client identifier.
     * @return a possibly empty but not null list of accounts.
     */
    List<Account> getAccounts(String clientId);

    /**
     * get a particular account.
     * 
     * @param accountId the account identifier.
     * @return the account if it can be found, null otherwise.
     */
    Account getAccount(String accountId);

    /**
     * get all transactions for an account.
     * 
     * @param accountId the account identifier.
     * @return a possibly empty but not null set of transactions.
     */
    List<Transaction> getTransactions(String accountId);

    /**
     * add two new transactions to the system and cause balances to be updated.
     * 
     * @param fromTransaction the transaction taking from the source account. assumed non-null.
     * @param toTransaction the transaction adding to the source account. assumed non-null.
     * @throws Exception if there is a failure performing the update.
     */
    void addTransactions(Transaction fromTransaction, Transaction toTransaction) throws Exception;
}
