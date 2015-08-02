package net.parttimepolymath.api;

import net.jcip.annotations.ThreadSafe;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * class for returning the result of a transfer request. The API is framed in terms of returning a genuine 2xx response
 * wherever possible, but with the logical result not necessarily being a successful. This allows us to carry back a richer response.
 * 
 * @author robert
 */
@ThreadSafe
public final class TransferResult {
    /**
     * prime used for hash calculation.
     */
    private static final int PRIME1 = 23;
    /**
     * prime used for hash calculation.
     */
    private static final int PRIME2 = 31;
    /**
     * the result of the request. These will correspond to HTTP codes.
     */
    private final int resultCode;
    /**
     * a message associated with the result, intended for human consumption.
     */
    private final String resultMessage;
    /**
     * a possibly null or blank transaction ID. This will only be provided on a successful request, and is the
     * id of the transaction generated for the source account.
     */
    private final String transactionId;

    /**
     * @param result the result code to store.
     * @param message the message to store.
     * @param txId the transactionID to store.
     */
    @JsonCreator
    public TransferResult(@JsonProperty("resultCode") final int result, @JsonProperty("resultMessage") final String message,
            @JsonProperty("transactionId") final String txId) {
        resultCode = result;
        resultMessage = message;
        transactionId = txId;
    }

    /**
     * @return the resultCode
     */
    @JsonProperty("resultCode")
    public int getResultCode() {
        return resultCode;
    }

    /**
     * @return the resultMessage
     */
    @JsonProperty("resultMessage")
    public String getResultMessage() {
        return resultMessage;
    }

    /**
     * @return the transactionId
     */
    @JsonProperty("transactionId")
    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(PRIME1, PRIME2).append(resultCode).append(resultMessage).append(transactionId).toHashCode();
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

        TransferResult rhs = (TransferResult) obj;
        return new EqualsBuilder().append(resultCode, rhs.resultCode).append(resultMessage, rhs.resultMessage)
                .append(transactionId, rhs.transactionId).isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("resultCode", resultCode)
                .append("resultMessage", resultMessage).append("transactionId", transactionId).toString();
    }
}
