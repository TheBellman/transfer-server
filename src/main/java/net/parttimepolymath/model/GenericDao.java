package net.parttimepolymath.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaQuery;

/**
 * This is an connector between the JPA entity model and a higher level model.
 * 
 * @author robert
 * @param <T> the type of the underlying entity object.
 * @param <PK> the type of the underlying entity object's primary key.
 */
public interface GenericDao<T, PK extends Serializable> {
    /**
     * create a new instance of T and return the persisted T.
     * 
     * @param t the T to store.
     * @return the T that was stored.
     */
    T create(T t);

    /**
     * fetch an instance of T using it's primary key.
     * 
     * @param id the primary key to search with.
     * @return the T that was read.
     */
    T read(PK id);

    /**
     * write a new version of T and return the persisted T.
     * 
     * @param t the T to store.
     * @return the T that was stored.
     */
    T update(T t);

    /**
     * remove the instance of T.
     * 
     * @param t the T to remove.
     */
    void delete(T t);

    /**
     * get the complete set of T.
     * 
     * @return a non-null but possibly empty list of T.
     */
    List<T> findAll();

    /**
     * get the set of T matching a named query.
     * 
     * @param namedQueryName the query to execute.
     * @return a non-null but possibly empty list of T.
     */
    List<T> findWithNamedQuery(String namedQueryName);

    /**
     * get the set of T matching a named query using a set of variable parameters.
     * 
     * @param namedQueryName the query to execute.
     * @param parameters the set of key/value pairs used to qualify the query
     * @return a non-null but possibly empty list of T.
     */
    List<T> findWithNamedQuery(String namedQueryName, Map<String, Object> parameters);

    /**
     * get a limited set of T matching a named query.
     * 
     * @param queryName the query to execute.
     * @param resultLimit the maximum number of T to retrieve.
     * @return a non-null but possibly empty list of T.
     */
    List<T> findWithNamedQuery(String queryName, int resultLimit);

    /**
     * get a limited set of T matching a named query using a set of variable parameters.
     * 
     * @param namedQueryName the query to execute.
     * @param parameters the set of key/value pairs used to qualify the query
     * @param resultLimit the maximum number of T to retrieve.
     * @return a non-null but possibly empty list of T.
     */
    List<T> findWithNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit);

    /**
     * get a set of T matching a criterion query using a set of variable parameters.
     * 
     * @param criterion the query criterion to execute.
     * @param parameters the set of key/value pairs used to qualify the query
     * @return a non-null but possibly empty list of T.
     */
    List<T> findWithCriteria(CriteriaQuery<T> criterion, Map<String, Object> parameters);

    /**
     * get a limited set of T matching a criterion query using a set of variable parameters.
     * 
     * @param criterion the query criterion to execute.
     * @param parameters the set of key/value pairs used to qualify the query
     * @param resultLimit the maximum number of T to retrieve.
     * @return a non-null but possibly empty list of T.
     */
    List<T> findWithCriteria(CriteriaQuery<T> criterion, Map<String, Object> parameters, int resultLimit);

    /**
     * find the first T matching the criterion with the set of variable parameters. The ordering is not guaranteed.
     * 
     * @param criterion the query criterion to execute.
     * @param parameters the set of key/value pairs used to qualify the query
     * @return a non-null T
     */
    T findFirstWithCriteria(CriteriaQuery<T> criterion, Map<String, Object> parameters);

    /**
     * find a T, or create it if it is not present.
     * 
     * @param criterion the query criterion to execute.
     * @param parameters the set of key/value pairs used to qualify the query
     * @param prototype the T to add if the selection is not satisfied.
     * @return a non-null T
     */
    T findOrMake(CriteriaQuery<T> criterion, Map<String, Object> parameters, T prototype);

    /**
     * construct a simple select..where.. criterion query for finding T.
     * 
     * @param rootColumn the qualifying attribute on the underlying entity.
     * @param parameterType the type of the where parameter.
     * @param parameterKey the name of the parameter value.
     * @return a non-null CriteriaQuery.
     */
    CriteriaQuery<T> simpleWhere(String rootColumn, Class<? extends Serializable> parameterType, String parameterKey);

    /**
     * given a set of key/value pairs describing the columns and values to match on. Note that this potentially throws NoResultException.
     * 
     * @param parameters a non-null set of key/value pairs.
     * @return the entity if available.
     */
    T getEntityByPrimaryKey(Map<String, Object> parameters);

    /**
     * given a set of parameters describing the primary keys for an entity, and a possibly new version of the entity, either store
     * the entity or update the existing version of the entity.
     * 
     * @param entity the entity to store or update.
     */
    void storeOrUpdate(final T entity);

    /**
     * given a non-null entity, return the set of key/value pairs that describes it's primary key.
     * 
     * @param entity the entity to examine.
     * @return the set of key/value pairs that describe the entity's key.
     */
    Map<String, Object> makeKeyParameters(final T entity);

    /**
     * helper method to test if two entity instances are equal. This is based on all attributes except the internal
     * database ID.
     * 
     * @param lhs the left hand side of the test.
     * @param rhs the right hand side of the test.
     * @return true if the two instances have identical attributes other than the internal database ID.
     */
    boolean matches(final T lhs, final T rhs);

    /**
     * helper method to copy the non-key attributes from the left-hand object to the right-hand object.
     * 
     * @param lhs the left-hand instance, assumed non-null
     * @param rhs the right-hand instance, assumed non-null.
     */
    void copyAttributes(final T lhs, final T rhs);

}
