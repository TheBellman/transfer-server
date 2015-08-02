package net.parttimepolymath.api;

import net.jcip.annotations.ThreadSafe;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * bean which represents a request to transfer an amount from one account to another.
 * 
 * @author robert
 */
@ThreadSafe
public final class TransferRequest {
    /**
     * prime used for hash calculation.
     */
    private static final int PRIME1 = 17;
    /**
     * prime used for hash calculation.
     */
    private static final int PRIME2 = 19;
    /**
     * the account we go from.
     */
    private final String fromAccount;
    /**
     * the account we go to.
     */
    private final String toAccount;
    /**
     * the amount expressed without decimal places.
     */
    private final long amount;

    /**
     * @param fromAccount the account id to transfer from.
     * @param toAccount the account id to transfer to.
     * @param amount the amount to transfer.
     */
    @JsonCreator
    public TransferRequest(@JsonProperty("fromAccount") String from, @JsonProperty("toAccount") String to, @JsonProperty("amount") long amt) {
        fromAccount = from;
        toAccount = to;
        amount = amt;
    }

    /**
     * @return the fromAccount
     */
    @JsonProperty("fromAccount")
    public String getFromAccount() {
        return fromAccount;
    }

    /**
     * @return the toAccount
     */
    @JsonProperty("toAccount")
    public String getToAccount() {
        return toAccount;
    }

    /**
     * @return the amount expressed without decimal places.
     */
    @JsonProperty("amount")
    public long getAmount() {
        return amount;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(PRIME1, PRIME2).append(fromAccount).append(toAccount).append(amount).toHashCode();
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

        TransferRequest rhs = (TransferRequest) obj;
        return new EqualsBuilder().append(fromAccount, rhs.fromAccount).append(toAccount, rhs.toAccount).append(amount, rhs.amount)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("fromAccount", fromAccount)
                .append("toAccount", toAccount).append("amount", amount).toString();
    }
}
