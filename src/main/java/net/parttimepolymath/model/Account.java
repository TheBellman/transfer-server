package net.parttimepolymath.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import net.jcip.annotations.NotThreadSafe;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * The persistent class for the account database table. Because this is mutable, it should not be shared between threads.
 */
@NotThreadSafe
@Entity
@Table(name = "account")
@NamedQuery(name = "Account.findAll", query = "SELECT a FROM Account a")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Account implements Serializable {

    /**
     * serial id.
     */
    private static final long serialVersionUID = -2987576869365830382L;

    /**
     * the unique account id.
     */
    @Id
    @NotNull
    @Size(max = 36)
    @Column(name = "account_id", unique = true, nullable = false, length = 36)
    private String accountId;

    /**
     * the account balance. Note that this class does not maintain
     */
    @Column(name = "balance", nullable = false, precision = 10, scale = 3)
    @NotNull
    private BigDecimal balance;

    /**
     * the ISO3 currency code the account transacts in.
     */
    @Column(name = "currency", nullable = false, length = 3)
    @Size(max = 3)
    @NotNull
    private String currency;

    /**
     * is the account open? 0 means no, non zero means yes.
     */
    @Column(name = "open", nullable = false)
    @NotNull
    private byte open;

    /**
     * the client that owns the account.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @NotNull
    private Client client;

    /**
     * a possibly empty set of transactions owned by this account.
     */
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    /**
     * lock used for synchronisation.
     */
    @Transient
    private final Lock lock = new ReentrantLock();

    /**
     * default constructor.
     */
    public Account() {
    }

    /**
     * accessor - get the account id.
     * 
     * @return the account id
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * mutator - set the account id.
     * 
     * @param value the id to set.
     */
    public void setAccountId(final String value) {
        accountId = value;
    }

    /**
     * accessor - retrieve the account balance.
     * 
     * @return the account balance.
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * mutator - set the account balance.
     * 
     * @param value the balance to store.
     */
    public void setBalance(final BigDecimal value) {
        balance = value;
    }

    /**
     * accessor - get the iso currency code.
     * 
     * @return the iso currency code.
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * mutator - set the iso currency code.
     * 
     * @param value the iso currency code.
     */
    public void setCurrency(final String value) {
        currency = value;
    }

    /**
     * is the account open?
     * 
     * @return true if it is.
     */
    public boolean isOpen() {
        return open != 0;
    }

    /**
     * mark the account as open or closed.
     * 
     * @param value true if the account is open.
     */
    public void setOpen(final boolean value) {
        if (value) {
            open = (byte) 1;
        } else {
            open = (byte) 0;
        }
    }

    /**
     * accessor - get the client.
     * 
     * @return the client who owns this account.
     */
    public Client getClient() {
        return client;
    }

    /**
     * mutator - set the client.
     * 
     * @param value the client to set.
     */
    public void setClient(final Client value) {
        client = value;
    }

    @Transient
    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
        // do something: put to a Map; log a warning, whatever
    }

    /**
     * get the set of associated transactions. note that this uses a mechanism for lazy instantiation
     * that is thread safe but not robust enough for production use.
     * 
     * @return a non-null set of transactions.
     */
    public List<Transaction> getTransactions() {
        if (transactions == null) {
            try {
                lock.lock();
                if (transactions == null) {
                    transactions = new ArrayList<>();
                }
            } finally {
                lock.unlock();
            }
        }
        return transactions;
    }

    /**
     * store the set of associated transactions.
     * 
     * @param value the transactions to set.
     */
    public void setTransactions(final List<Transaction> value) {
        transactions = value;
    }

    /**
     * attach a transaction to the account. Note this is not thread safe, as the supplied value is modified.
     * 
     * @param value the transaction to attach.
     * @return the attached transaction
     */
    public Transaction addTransaction(final Transaction value) {
        getTransactions().add(value);
        value.setAccount(this);

        return value;
    }

    /**
     * detach a transaction from the account. Note that this is not thread safe as the supplied value is modified.
     * 
     * @param value the transaction to detach.
     * @return the detached transaction
     */
    public Transaction removeTransaction(final Transaction value) {
        getTransactions().remove(value);
        value.setAccount(null);

        return value;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("accountId", accountId).append("balance", balance)
                .append("client", client.getClientId()).append("currency", currency).append("open", isOpen()).toString();
    }

}
