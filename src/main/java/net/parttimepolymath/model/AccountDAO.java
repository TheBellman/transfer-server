package net.parttimepolymath.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

/**
 * access class for working with account table.
 * 
 * @author robert
 */
public final class AccountDAO extends GenericDaoImpl<Account, String> {
    /**
     * default constructor.
     * 
     * @param emf the injected EntityMnanagerFactory, assumed non-null.
     */
    public AccountDAO(final EntityManagerFactory emf) {
        super(emf.createEntityManager());
    }

    @Override
    public boolean matches(final Account lhs, final Account rhs) {
        if (lhs == null || rhs == null) {
            return false;
        }
        if (lhs == rhs) {
            return true;
        }
        return lhs.equals(rhs);
    }

    @Override
    public void copyAttributes(final Account lhs, final Account rhs) {
        lhs.setAccountId(rhs.getAccountId());
        lhs.setBalance(rhs.getBalance());
        lhs.setClient(rhs.getClient());
        lhs.setCurrency(rhs.getCurrency());
        lhs.setOpen(rhs.isOpen());
        lhs.setTransactions(rhs.getTransactions());
    }

    @Override
    public Account getEntityByPrimaryKey(final Map<String, Object> parameters) {
        return findFirstWithCriteria(simpleWhere("accountId", String.class, "account_id"), parameters);

    }

    @Override
    public Map<String, Object> makeKeyParameters(final Account entity) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("account_id", entity.getAccountId());
        return parameters;
    }

    /**
     * get accounts matching a given client id.
     * 
     * @param clientId the client id to match.
     * @return the set of matching accounts, if available.
     */
    public List<Account> getAccounts(final String clientId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Account> cq = cb.createQuery(Account.class);
        Root<Account> account = cq.from(Account.class);

        Join<Account, Client> client = account.join(Account_.client);

        cq.where(cb.equal(client.get(Client_.clientId), cb.parameter(String.class, "client_id")));
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("client_id", clientId);
        return findWithCriteria(cq, parameters);
    }

    public Account getAccount(final String accountId) {
        Account template = new Account();
        template.setAccountId(accountId);
        return getEntityByPrimaryKey(makeKeyParameters(template));
    }

}
