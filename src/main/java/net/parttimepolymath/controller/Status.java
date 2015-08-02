package net.parttimepolymath.controller;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * simple bean to hold service status.
 * 
 * @author robert
 */
public final class Status {
    /**
     * the status of the service.
     */
    @JsonProperty("status")
    private String status;

    /**
     * number of requests received since start up.
     */
    @JsonIgnore
    private final AtomicLong requestCount;

    /**
     * default constructor.
     */
    public Status() {
        this("down", 0);
    }

    /**
     * construct with a status.
     * 
     * @param aStatus the status to use.
     */
    public Status(String aStatus) {
        this(aStatus, 0);
    }

    /**
     * constructor for json deserialisation.
     * 
     * @param aStatus the status to store.
     * @param count the reported count.
     */
    @JsonCreator
    public Status(@JsonProperty("status") String aStatus, @JsonProperty("requestCount") long count) {
        status = aStatus;
        requestCount = new AtomicLong(count);
    }

    /**
     * update the transaction count.
     */
    public void updateCount() {
        requestCount.incrementAndGet();
    }

    @JsonProperty("requestCount")
    public long getCount() {
        return requestCount.get();
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * mutator, set a new status.
     * 
     * @param newStatus the new status to store.
     */
    public void setStatus(final String newStatus) {
        status = newStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("status", status).append("requestCount", getCount())
                .toString();
    }
}
