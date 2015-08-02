package net.parttimepolymath.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * helper class for accessing the transaction table.
 * 
 * @author robert
 */
public final class TransactionDAO extends GenericDaoImpl<Transaction, String> {
    /**
     * default constructor.
     * 
     * @param emf the injected EntityMnanagerFactory, assumed non-null.
     */
    public TransactionDAO(final EntityManagerFactory emf) {
        super(emf.createEntityManager());
    }

    @Override
    public boolean matches(final Transaction lhs, final Transaction rhs) {
        if (lhs == null || rhs == null) {
            return false;
        }
        if (lhs == rhs) {
            return true;
        }
        return lhs.equals(rhs);
    }

    @Override
    public void copyAttributes(final Transaction lhs, final Transaction rhs) {
        lhs.setAccount(rhs.getAccount());
        lhs.setAmount(rhs.getAmount());
        lhs.setDate(rhs.getDate());
        lhs.setId(rhs.getId());
        lhs.setReference(rhs.getReference());
    }

    @Override
    public Transaction getEntityByPrimaryKey(final Map<String, Object> parameters) {
        return findFirstWithCriteria(simpleWhere("txId", String.class, "tx_id"), parameters);
    }

    @Override
    public Map<String, Object> makeKeyParameters(final Transaction entity) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("tx_id", entity.getId().getTxId());
        parameters.put("account_id", entity.getId().getAccountId());
        return parameters;
    }

    /**
     * fetch all transactions matching a specific accountid.
     * 
     * @param accountId
     * @return
     */
    public List<Transaction> getTransactions(final String accountId) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
        Root<Transaction> transaction = cq.from(Transaction.class);

        cq.where(cb.equal(transaction.get(Transaction_.id).get(TransactionPK_.accountId), cb.parameter(String.class, "account_id")));
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("account_id", accountId);
        return findWithCriteria(cq, parameters);

    }
}
