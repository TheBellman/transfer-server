package net.parttimepolymath.model;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * an implementation of our GenericDao, mainly to act as a base class for more sophisticated DAO. Note that I have
 * copied this over from another project I've recently built, as I found it a useful mini-framework for getting things into and out
 * of the JPA layer.
 * 
 * @author robert
 * @param <T> the type of entity managed by the DAO
 * @param <PK> the type of the primary key of the entity.
 */
public abstract class GenericDaoImpl<T, PK extends Serializable> implements GenericDao<T, PK>, AutoCloseable {

    /**
     * the apparent class of T.
     */
    private final Class<T> entityClass;

    /**
     * an injected entity manager instance.
     */
    private final EntityManager entityManager;

    /**
     * primary constructor. Note that this figures out the class of the actual entity through reflection on T.
     * 
     * @param em the entity manager to inject to this instance.
     */
    @SuppressWarnings("unchecked")
    public GenericDaoImpl(final EntityManager em) {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.entityClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
        entityManager = em;
    }

    /**
     * access the entity manager.
     * 
     * @return the contained entity manager.
     */
    protected final EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public final T create(final T t) {
        entityManager.persist(t);
        return t;
    }

    @Override
    public final T read(final PK id) {
        return entityManager.find(entityClass, id);
    }

    @Override
    public final T update(final T t) {
        return entityManager.merge(t);
    }

    @Override
    public final void delete(final T t) {
        T newt = entityManager.merge(t);
        entityManager.remove(newt);
    }

    @Override
    public abstract boolean matches(final T lhs, final T rhs);

    @Override
    public abstract void copyAttributes(final T lhs, final T rhs);

    @Override
    public abstract T getEntityByPrimaryKey(Map<String, Object> parameters);

    @Override
    public final List<T> findWithNamedQuery(final String namedQueryName) {
        return entityManager.createNamedQuery(namedQueryName, entityClass).getResultList();
    }

    @Override
    public final List<T> findWithNamedQuery(final String namedQueryName, final Map<String, Object> parameters) {
        return findWithNamedQuery(namedQueryName, parameters, 0);
    }

    @Override
    public final List<T> findWithNamedQuery(final String queryName, final int resultLimit) {
        return entityManager.createNamedQuery(queryName, entityClass).setMaxResults(resultLimit).getResultList();
    }

    @Override
    public final List<T> findWithNamedQuery(final String namedQueryName, final Map<String, Object> parameters, final int resultLimit) {
        TypedQuery<T> query = entityManager.createNamedQuery(namedQueryName, entityClass);
        if (resultLimit > 0) {
            query.setMaxResults(resultLimit);
        }
        for (Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }

    @Override
    public final List<T> findWithCriteria(final CriteriaQuery<T> criterion, final Map<String, Object> parameters) {
        return findWithCriteria(criterion, parameters, 0);
    }

    @Override
    public final List<T> findWithCriteria(final CriteriaQuery<T> criterion, final Map<String, Object> parameters, final int resultLimit) {
        TypedQuery<T> query = entityManager.createQuery(criterion);
        if (resultLimit > 0) {
            query.setMaxResults(resultLimit);
        }
        for (Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }

    @Override
    public final List<T> findAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        CriteriaQuery<T> all = cq.select(root);
        TypedQuery<T> allQuery = entityManager.createQuery(all);

        return allQuery.getResultList();
    }

    @Override
    public final T findFirstWithCriteria(final CriteriaQuery<T> criterion, final Map<String, Object> parameters) {
        List<T> results = findWithCriteria(criterion, parameters, 1);
        if (results.isEmpty()) {
            throw new NoResultException();
        }
        return results.get(0);
    }

    @Override
    public final T findOrMake(final CriteriaQuery<T> cq, final Map<String, Object> parameters, final T prototype) {
        T result = null;
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        try {
            result = findFirstWithCriteria(cq, parameters);
            return result;
        } catch (NoResultException nre) {
            result = prototype;
            create(result);
            entityManager.flush();
            entityManager.refresh(result);
        } finally {
            tx.commit();
        }
        return result;
    }

    @Override
    public final CriteriaQuery<T> simpleWhere(final String rootColumn, final Class<? extends Serializable> parameterType,
            final String parameterKey) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);

        cq.where(cb.equal(root.get(rootColumn), cb.parameter(parameterType, parameterKey)));
        return cq;
    }

    @Override
    public abstract Map<String, Object> makeKeyParameters(final T entity);

    @Override
    public final void storeOrUpdate(final T entity) {
        EntityTransaction tx = entityManager.getTransaction();
        tx.begin();
        try {
            T instance = getEntityByPrimaryKey(makeKeyParameters(entity));
            if (!matches(entity, instance)) {
                copyAttributes(entity, instance);
                update(instance);
            }
        } catch (NoResultException nre) {
            create(entity);
            entityManager.flush();
            entityManager.refresh(entity);
        } finally {
            tx.commit();
        }
    }

    @Override
    public final void close() throws IOException {
        entityManager.close();
    }

}
