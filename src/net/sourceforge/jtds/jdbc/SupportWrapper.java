package net.sourceforge.jtds.jdbc;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Calendar;

public class SupportWrapper {
    private SupportWrapper() {
    }

    public static Object convert(Object callerReference, Object x, int jdbcType, String charSet) throws SQLException {
        return Support.convert(callerReference, x, jdbcType, charSet);
    }

    public static InputStream getClobInputStream(ClobImpl clob) throws SQLException {
        return clob.getBlobBuffer().getUnicodeStream();
    }

    public static int getBooleanType() {
        return JtdsStatement.BOOLEAN;
    }

    public static long timeToZone(java.util.Date value, Calendar target) {
        return Support.timeToZone(value, target);
    }
}
