package net.parttimepolymath.model;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;

import net.jcip.annotations.ThreadSafe;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * implementation of our database facade. There should only be one of these in play, although it is thread safe.
 * 
 * @author robert
 */
@ThreadSafe
public final class JPADataStore implements DataStore {
    /**
     * local copy of the Factory to be used by the JPA layer.
     */
    private final EntityManagerFactory emf;
    /**
     * Class logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JPADataStore.class);

    /**
     * primary constructor.
     */
    public JPADataStore(final EntityManagerFactory factory) {
        emf = factory;
    }

    @Override
    public List<Client> getClients() {
        try (ClientDAO dao = new ClientDAO(emf)) {
            return dao.findAll();
        } catch (IOException e) {
            LOGGER.error("serious failure {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Client getClient(final String clientId) {
        if (StringUtils.isBlank(clientId)) {
            return null;
        }

        try (ClientDAO dao = new ClientDAO(emf)) {
            return dao.getClient(clientId);
        } catch (NoResultException nre) {
            return null;
        } catch (IOException e) {
            LOGGER.error("serious failure {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<Account> getAccounts(final String clientId) {
        if (StringUtils.isBlank(clientId)) {
            return Collections.emptyList();
        }

        try (AccountDAO dao = new AccountDAO(emf)) {
            return dao.getAccounts(clientId);
        } catch (IOException e) {
            LOGGER.error("serious failure {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Account getAccount(final String accountId) {
        if (StringUtils.isBlank(accountId)) {
            return null;
        }

        try (AccountDAO dao = new AccountDAO(emf)) {
            return dao.getAccount(accountId);
        } catch (NoResultException nre) {
            return null;
        } catch (IOException e) {
            LOGGER.error("serious failure {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<Transaction> getTransactions(final String accountId) {
        if (StringUtils.isBlank(accountId)) {
            return Collections.emptyList();
        }
        try (TransactionDAO dao = new TransactionDAO(emf)) {
            return dao.getTransactions(accountId);
        } catch (IOException e) {
            LOGGER.error("serious failure {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    // note that this could be improved by adding or moving the balance update into the database itself.
    // additionally we could improve concurrency by using the before balances in the update.
    @Override
    public void addTransactions(Transaction fromTransaction, Transaction toTransaction) throws Exception {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        try {
            Account toAccount = entityManager.find(Account.class, toTransaction.getId().getAccountId());
            Account fromAccount = entityManager.find(Account.class, fromTransaction.getId().getAccountId());

            toTransaction.setAccount(toAccount);
            fromTransaction.setAccount(fromAccount);

            entityManager.persist(fromTransaction);
            entityManager.persist(toTransaction);

            BigDecimal toBalance = toAccount.getBalance().add(toTransaction.getAmount());
            BigDecimal fromBalance = fromAccount.getBalance().add(fromTransaction.getAmount());

            toAccount.setBalance(toBalance);
            fromAccount.setBalance(fromBalance);

            toAccount.addTransaction(toTransaction);
            fromAccount.addTransaction(fromTransaction);

            entityManager.merge(toAccount);
            entityManager.merge(fromAccount);

            entityManager.flush();

            entityManager.refresh(toTransaction);
            entityManager.refresh(fromTransaction);
            entityManager.refresh(toAccount);
            entityManager.refresh(fromAccount);

        } catch (Exception ex) {
            tx.rollback();
        } finally {
            tx.commit();
        }
    }

}
