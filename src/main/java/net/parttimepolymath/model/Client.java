package net.parttimepolymath.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import net.jcip.annotations.NotThreadSafe;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheType;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * The persistent class for the client database table. Note that some operations against this are not
 * thread safe - the state of this class will remain consistent when being modified by multiple threads,
 * but adding and removing an account required external transaction control to avoid side effects on the Account object.
 */
@NotThreadSafe
@Entity
@Cacheable(true)
@Cache(type = CacheType.SOFT, size = 256, expiry = 60000)
@Table(name = "client")
@NamedQuery(name = "Client.findAll", query = "SELECT c FROM Client c")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Client implements Serializable {
    /**
     * serial id.
     */
    private static final long serialVersionUID = 8138900255061212780L;
    /**
     * prime used for hash calculation.
     */
    private static final int PRIME1 = 19;
    /**
     * prime used for hash calculation.
     */
    private static final int PRIME2 = 17;

    /**
     * lock used for synchronisation.
     */
    @Transient
    private final Lock lock = new ReentrantLock();

    /**
     * the unique client id.
     */
    @Id
    @Column(name = "client_id", unique = true, nullable = false, length = 36)
    @NotNull
    @Size(max = 36)
    private String clientId;

    /**
     * the client name.
     */
    @Column(name = "name", nullable = false, length = 64)
    @NotNull
    @Size(max = 64)
    private String name;

    /**
     * the client's accounts. This may be an empty set.
     */
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<Account> accounts;

    /**
     * default constructor.
     */
    public Client() {
    }

    /**
     * accessor - get the client id.
     * 
     * @return the cilent id.
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * mutator - set the client id.
     * 
     * @param value the client id to set
     */
    public void setClientId(final String value) {
        clientId = value;
    }

    /**
     * accessor - get the client name.
     * 
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * mutator - set the client name.
     * 
     * @param value the name to set.
     */
    public void setName(final String value) {
        name = value;
    }

    /**
     * accessor - get the set of accounts. Note that this uses a simple-minded mechanism for constraining thread safety
     * around lazy instantiation of the set of accounts that is not suitable for production use.
     * 
     * @return the contained set of accounts. guaranteed not to be null.
     */
    public List<Account> getAccounts() {
        if (accounts == null) {
            try {
                lock.lock();
                if (accounts == null) {
                    accounts = new ArrayList<>();
                }
            } finally {
                lock.unlock();
            }
        }
        return accounts;
    }

    /**
     * mutator - set the set of accounts.
     * 
     * @param value the accounts to set.
     */
    public void setAccounts(final List<Account> value) {
        accounts = value;
    }

    /**
     * mutator - attach an account to the existing set. Note that the supplied value is updated.
     * 
     * @param value the account to set.
     * @return the account that was added.
     */
    public Account addAccount(final Account value) {
        getAccounts().add(value);
        value.setClient(this);

        return value;
    }

    /**
     * mutator - detach an account from the client. Note that the supplied value is updated.
     * 
     * @param value the account to detach.
     * @return the account that was attached
     */
    public Account removeAccount(final Account value) {
        getAccounts().remove(value);
        value.setClient(null);

        return value;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(PRIME1, PRIME2).append(accounts).append(clientId).append(name).toHashCode();
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

        Client rhs = (Client) obj;
        return new EqualsBuilder().append(accounts, rhs.accounts).append(clientId, rhs.clientId).append(name, rhs.name).isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("clientId", clientId).append("name", name).toString();
    }
}
