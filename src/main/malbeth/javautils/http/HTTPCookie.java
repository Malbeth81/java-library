package malbeth.javautils.http;

public class HTTPCookie {
    public static class HTTPCookieAttributes {
        public String Domain;
        public Long Expires;
        public boolean HttpOnly;
        public Long MaxAge;
        public String Path;
        public boolean Secure;
    }

    public HTTPCookieAttributes attributes;
    public String name;
    public String value;

    public HTTPCookie() {
        attributes = new HTTPCookieAttributes();
    }

    public static final HTTPCookie parse(String cookie) {
        HTTPCookie result = null;
        if (cookie != null && cookie.length() > 0) {
            String[] values = cookie.split(";");
            for (int i = 0; i < values.length; i++) {
                int separator = values[i].indexOf('=');
                if (separator > 0) {
                    String name = values[i].substring(0, separator).trim();
                    String value = values[i].substring(separator + 1).trim();
                    if (i == 0) {
                        result = new HTTPCookie();
                        result.name = name;
                        result.value = value;
                    } else if (result != null) {
                        if (name.equalsIgnoreCase("Domain"))
                            result.attributes.Domain = value;
                        else if (name.equalsIgnoreCase("Expires"))
                            result.attributes.Expires = HTTPUtils.valueOfHTTPTimeString(value);
                        else if (name.equalsIgnoreCase("MaxAge"))
                            result.attributes.MaxAge = Long.getLong(value);
                        else if (name.equalsIgnoreCase("Path"))
                            result.attributes.Path = value;
                    }
                } else if (result != null) {
                    if (values[i].equalsIgnoreCase("HttpOnly"))
                        result.attributes.HttpOnly = true;
                    else if (values[i].equalsIgnoreCase("Secure"))
                        result.attributes.Secure = true;
                }
            }
        }
        return result;
    }

    public final String toString() {
        if (name != null && value != null && name.length() > 0 && value.length() > 0) {
            String result = name + "=" + value + ";";

            if (attributes != null) {
                if (attributes.Domain != null && attributes.Domain.length() > 0)
                    result += " Domain=" + attributes.Domain + ";";
                if (attributes.Path != null && attributes.Path.length() > 0)
                    result += " Path=" + attributes.Path + ";";
                if (attributes.Expires != null)
                    result += " Expires=" + HTTPUtils.formatHTTPTimeString(attributes.Expires) + ";";
                if (attributes.MaxAge != null)
                    result += " Max-Age=" + attributes.MaxAge + ";";
                if (attributes.HttpOnly)
                    result += " HttpOnly;";
                if (attributes.Secure)
                    result += " Secure;";
            }

            return result;
        }
        return null;
    }
}
