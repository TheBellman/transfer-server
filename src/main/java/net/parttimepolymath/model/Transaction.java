package net.parttimepolymath.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import net.jcip.annotations.NotThreadSafe;
import net.parttimepolymath.util.CustomDateSerializer;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * The persistent class for the transaction database table. Note that ideally we would adjust the model so that this
 * was not a mutable class, as we should never risk rewriting a historical transaction. Because it is mutable,
 * it should not be shared between threads.
 */
@NotThreadSafe
@Entity
@Table(name = "transaction")
@NamedQuery(name = "Transaction.findAll", query = "SELECT t FROM Transaction t")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Transaction implements Serializable {
    // TODO: ideally transaction should specify the currency we are transferring.

    /**
     * generated id.
     */
    private static final long serialVersionUID = -3176527764726648391L;
    /**
     * prime used for hash calculation.
     */
    private static final int PRIME1 = 19;
    /**
     * prime used for hash calculation.
     */
    private static final int PRIME2 = 13;

    /**
     * primary key - account id + transaction id.
     */
    @EmbeddedId
    private TransactionPK id;

    /**
     * the transaction amount.
     */
    @Column(name = "amount", nullable = false, precision = 10, scale = 3)
    @NotNull
    private BigDecimal amount;

    /**
     * the time the transaction occurred.
     */
    @Column(name = "date", nullable = false)
    @Converter(name = "dateTimeConverter", converterClass = net.parttimepolymath.util.JodaDateTimeConverter.class)
    @Convert("dateTimeConverter")
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @JsonSerialize(using = CustomDateSerializer.class)
    private DateTime date;

    /**
     * optional transaction reference - semantics of this depend on what the transaction type is.
     */
    @Column(name = "reference", length = 36)
    @Size(max = 36)
    private String reference;

    /**
     * the account this transaction is for.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    @NotNull
    private Account account;

    /**
     * default constructor.
     */
    public Transaction() {
    }

    /**
     * accessor - get the contained ID for the transaction.
     * 
     * @return the ID.
     */
    public TransactionPK getId() {
        return id;
    }

    /**
     * mutator - set the transaction id.
     * 
     * @param value the id to set.
     */
    public void setId(final TransactionPK value) {
        id = value;
    }

    /**
     * accessor - get transaction amount.
     * 
     * @return the amount.
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * mutator - set the transaction amount.
     * 
     * @param value the amount to set.
     */
    public void setAmount(final BigDecimal value) {
        amount = value;
    }

    /**
     * accessor - get the time of the transaction.
     * 
     * @return the time of the transaction.
     */
    public DateTime getDate() {
        return date.withZone(DateTimeZone.UTC);
    }

    /**
     * mutator set the transaction time.
     * 
     * @param value the transaction time to set.
     */
    public void setDate(final DateTime value) {
        date = value;
    }

    /**
     * accessor - get the transaction reference.
     * 
     * @return the reference, which may be null.
     */
    public String getReference() {
        return reference;
    }

    /**
     * mutator - set the reference.
     * 
     * @param value a reference to set.
     */
    public void setReference(final String value) {
        reference = value;
    }

    /**
     * accessor - get the account this transaction is for.
     * 
     * @return the account.
     */
    public Account getAccount() {
        return account;
    }

    /**
     * mutator - set the transaction's account.
     * 
     * @param value the account to set.
     */
    public void setAccount(final Account value) {
        account = value;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(PRIME1, PRIME2).append(account).append(amount).append(date).append(id).append(reference).toHashCode();
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

        Transaction rhs = (Transaction) obj;
        return new EqualsBuilder().append(account, rhs.account).append(amount, rhs.amount).append(date, rhs.date).append(id, rhs.id)
                .append(reference, rhs.reference).isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("account", account).append("amount", amount)
                .append("date", date).append("id", id).append("reference", reference).toString();
    }
}
