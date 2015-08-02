package net.parttimepolymath.util;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * small helper to serialise joda dates as ISO format.
 * 
 * @author robert
 */
public class CustomDateSerializer extends JsonSerializer<DateTime> {
    private static DateTimeFormatter formatter = ISODateTimeFormat.dateTime();

    @Override
    public void serialize(final DateTime value, final JsonGenerator gen, final SerializerProvider arg2) throws IOException,
            JsonProcessingException {

        gen.writeString(formatter.print(value));
    }

}
