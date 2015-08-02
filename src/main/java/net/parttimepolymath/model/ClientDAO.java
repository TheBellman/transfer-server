package net.parttimepolymath.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

/**
 * access class for working with client table.
 * 
 * @author robert
 */
public final class ClientDAO extends GenericDaoImpl<Client, String> {

    /**
     * default constructor.
     * 
     * @param emf the injected EntityMnanagerFactory, assumed non-null.
     */
    public ClientDAO(final EntityManagerFactory emf) {
        super(emf.createEntityManager());
    }

    @Override
    public boolean matches(final Client lhs, final Client rhs) {
        if (lhs == null || rhs == null) {
            return false;
        }
        if (lhs == rhs) {
            return true;
        }
        return lhs.equals(rhs);
    }

    @Override
    public void copyAttributes(final Client lhs, final Client rhs) {
        lhs.setAccounts(rhs.getAccounts());
        lhs.setClientId(rhs.getClientId());
        lhs.setName(rhs.getName());
    }

    @Override
    public Client getEntityByPrimaryKey(final Map<String, Object> parameters) {
        return findFirstWithCriteria(simpleWhere("clientId", String.class, "client_id"), parameters);
    }

    @Override
    public Map<String, Object> makeKeyParameters(final Client entity) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("client_id", entity.getClientId());
        return parameters;
    }

    public Client getClient(final String clientId) {
        Client template = new Client();
        template.setClientId(clientId);
        return getEntityByPrimaryKey(makeKeyParameters(template));
    }

}
