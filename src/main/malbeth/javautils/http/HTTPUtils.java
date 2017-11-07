package malbeth.javautils.http;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class HTTPUtils {
    private static SimpleDateFormat httpDateTimeFormat = null;

    private HTTPUtils() {

    }

    public static final String formatHTTPTimeString(long date) {
        // Return specified time in HTTP format
        try {
            return getFormatter().format(new Date(date));
        } catch (Exception e) {
        }
        return null;
    }

    private static final SimpleDateFormat getFormatter() {
        // Instantiate formatter if undefined
        if (httpDateTimeFormat == null) {
            // HTTP time string example : Fri, 31 Dec 1999 23:59:59 GMT
            httpDateTimeFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", java.util.Locale.ENGLISH);
            httpDateTimeFormat.setCalendar(Calendar.getInstance(TimeZone.getTimeZone("GMT"), java.util.Locale.ENGLISH));
        }
        return httpDateTimeFormat;
    }

    public static final Float parseFloat(String string) {
        if (string != null) {
            try {
                // Return a float object representing the specified string value
                return Float.valueOf(string.trim());
            } catch (Exception e) {
            }
        }

        return null;
    }

    public static final Integer parseInteger(String string) {
        if (string != null) {
            try {
                // Return an integer object representing the specified string value
                return Integer.valueOf(string.trim());
            } catch (Exception e) {
            }
        }

        return null;
    }

    public static final Long parseLong(String string) {
        if (string != null) {
            try {
                // Return a long object representing the specified string value
                return Long.valueOf(string.trim());
            } catch (Exception e) {
            }
        }

        return null;
    }

    public static final Date parseHTTPTimeString(String string) {
        if (string != null) {
            try {
                // Return a date object representing the specified time string
                return getFormatter().parse(string.trim());
            } catch (Exception e) {
            }
        }

        return null;
    }

    public static final String trim(String string) {
        if (string != null)
            return string.trim();

        return null;
    }

    public static final Long valueOfHTTPTimeString(String string) {
        if (string != null) {
            Date result = parseHTTPTimeString(string);

            // Return the numeric value of the specified time string
            if (result != null)
                return result.getTime();
        }

        return null;
    }

}
