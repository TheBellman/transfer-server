package net.parttimepolymath.model;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.hsqldb.cmdline.SqlFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * helper class for constructing a DataStore. This is broken out so that we can reuse the same launch and populate
 * of hsqldb in both test and live run.
 * 
 * @author robert
 */
public final class DataStoreFactory {
    /**
     * Class logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataStoreFactory.class);

    /**
     * driver class we are using.
     */
    public static final String DRIVER_CLASS = "org.hsqldb.jdbcDriver";
    /**
     * name of our database.
     */
    public static final String DB_URL = "jdbc:hsqldb:mem:TRANSFER";

    /**
     * construct a DataStore.
     * 
     * @param createScript - the db creation script.
     * @return the data store
     */
    public static DataStore makeDataStore(final String createScript) {

        Connection jdbcConnection = null;
        try {
            LOGGER.info("Creating test database for in-memory database context (url=" + DB_URL + ")");
            Class.forName(DRIVER_CLASS);
            jdbcConnection = DriverManager.getConnection(DB_URL);

            SqlFile sqlFile = new SqlFile(new StringReader(createScript), "createTestDBReader", System.out, null, false, null);
            sqlFile.setConnection(jdbcConnection);
            sqlFile.execute();
            LOGGER.info("script executed ok");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("createTestDatabase failed", ex);
        } finally {
            if (jdbcConnection != null) {
                try {
                    jdbcConnection.close();
                } catch (Exception ex) {
                    LOGGER.warn("failed to close connection: " + ex.getMessage());
                }
            }
        }

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("transferServer", emfProperties());
        return new JPADataStore(emf);
    }

    /**
     * assuming a HSQLDB instance is running, shut it down.
     */
    public static void shutdownDatabase() {
        Connection jdbcConnection = null;
        try {
            Class.forName(DRIVER_CLASS);
            jdbcConnection = DriverManager.getConnection(DB_URL);
            try (PreparedStatement ps = jdbcConnection.prepareStatement("shutdown;")) {
                ps.execute();
            }
            jdbcConnection.commit();

        } catch (Exception ex) {
            throw new RuntimeException("shutdownDatabase failed", ex);
        } finally {
            if (jdbcConnection != null) {
                try {
                    jdbcConnection.close();
                } catch (Exception ex) {
                    System.out.println("failed to close connection: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * build a set of properties to pass to the JPA layer.
     * 
     * @return a set of key/value pairs.
     */
    private static Map<String, String> emfProperties() {
        Map<String, String> props = new HashMap<String, String>();
        props.put(PersistenceUnitProperties.JDBC_URL, DB_URL);
        props.put(PersistenceUnitProperties.JDBC_DRIVER, DRIVER_CLASS);
        props.put(PersistenceUnitProperties.JDBC_USER, "SA");
        props.put(PersistenceUnitProperties.JDBC_PASSWORD, "");

        props.put(PersistenceUnitProperties.CACHE_STATEMENTS, "true");
        props.put(PersistenceUnitProperties.BATCH_WRITING, "JDBC");

        props.put(PersistenceUnitProperties.CONNECTION_POOL_INITIAL, "4");
        props.put(PersistenceUnitProperties.CONNECTION_POOL_MIN, "4");
        props.put(PersistenceUnitProperties.CONNECTION_POOL_MAX, "16");

        props.put("eclipselink.jdbc.connections.initial", "4");
        props.put("eclipselink.jdbc.connections.min", "4");
        props.put("eclipselink.jdbc.connections.max", "16");
        return props;
    }
}
