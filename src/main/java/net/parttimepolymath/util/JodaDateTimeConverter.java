package net.parttimepolymath.util;

/**
 * helper class to convert the database date columns to joda date/time.
 */
import java.sql.Timestamp;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;
import org.joda.time.DateTime;

public class JodaDateTimeConverter implements Converter {
    /**
     * generated uid. Why do so many of these JPA classes implement serializable?
     */
    private static final long serialVersionUID = -394198864985808896L;

    @Override
    public Object convertDataValueToObjectValue(Object dataValue, Session session) {
        return dataValue == null ? null : new DateTime(dataValue);
    }

    @Override
    public Object convertObjectValueToDataValue(Object objectValue, Session session) {
        return objectValue == null ? null : new Timestamp(((DateTime) objectValue).getMillis());
    }

    @Override
    public void initialize(DatabaseMapping mapping, Session session) {
    }

    @Override
    public boolean isMutable() {
        return false;
    }

}
