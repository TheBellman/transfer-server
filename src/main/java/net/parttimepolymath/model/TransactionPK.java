package net.parttimepolymath.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import net.jcip.annotations.NotThreadSafe;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * The primary key class for the transaction database table. As this is mutable, operations
 * when a single instance is used between threads will potentially result in odd inconsistencies.
 */
@Embeddable
@NotThreadSafe
public final class TransactionPK implements Serializable {
    /**
     * serial ID.
     */
    private static final long serialVersionUID = 2403867408106499663L;
    /**
     * prime used for hash calculation.
     */
    private static final int PRIME1 = 43;
    /**
     * prime used for hash calculation.
     */
    private static final int PRIME2 = 71;

    /**
     * unique transaction id.
     */
    @Column(name = "tx_id", unique = true)
    @NotNull
    @Size(max = 36)
    private String txId;

    /**
     * account id.
     */
    @Column(name = "account_id", insertable = false, updatable = false, unique = true)
    @NotNull
    @Size(max = 36)
    private String accountId;

    /**
     * default constructor.
     */
    public TransactionPK() {
    }

    /**
     * accessor - retrieve transaction id.
     * 
     * @return the transaction id
     */
    public String getTxId() {
        return txId;
    }

    /**
     * mutator - set the transaction id.
     * 
     * @param value the id to set.
     */
    public void setTxId(final String value) {
        txId = value;
    }

    /**
     * accessor - retrieve the account id.
     * 
     * @return the account id.
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * mutator - set the account id.
     * 
     * @param value the account id to set.
     */
    public void setAccountId(final String value) {
        accountId = value;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(PRIME1, PRIME2).append(accountId).append(txId).toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        TransactionPK rhs = (TransactionPK) obj;
        return new EqualsBuilder().append(accountId, rhs.accountId).append(txId, rhs.txId).isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("accountId", accountId).append("txId", txId).toString();
    }
}
