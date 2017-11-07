package malbeth.javautils.http;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.TreeMap;

public class HTTPURL {
    public String extension = "";
    public String fileName = "";
    public String parameters = "";
    public TreeMap<String, String> variables = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);

    public HTTPURL() {
    }

    public static final HTTPURL parse(String url) {
        // ex.: / or /main.html or /main.html?var=1
        if (url != null && url.length() > 0) {
            HTTPURL result = new HTTPURL();

            // Separate file and parameters
            int separator = url.indexOf('?');
            if (separator >= 1) {
                result.fileName = url.substring(1, separator);
                result.parameters = url.substring(separator + 1);
            } else {
                result.fileName = url.substring(1);
                result.parameters = "";
            }

            // Extract file extension
            separator = result.fileName.lastIndexOf('.');
            if (separator >= 0)
                result.extension = result.fileName.substring(separator + 1);

            // Extract parameters
            if (result.parameters.length() >= 0) {
                String[] params = result.parameters.split("&");
                for (int i = 0; i < params.length; i++) {
                    separator = params[i].indexOf('=');
                    if (separator >= 0) {
                        try {
                            result.variables.put(params[i].substring(0, separator).trim(), URLDecoder.decode(params[i].substring(separator + 1).trim(), "UTF-8"));
                        } catch (Exception e) {
                        }
                    }
                }
            }
            return result;
        }
        return null;
    }

    public final String toString() {
        String variableString = "";
        for (Iterator<String> it = variables.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            try {
                variableString += key + "=" + URLEncoder.encode(variables.get(key), "UTF-8") + (it.hasNext() ? "&" : "");
            } catch (Exception e) {
            }
        }
        return fileName + "?" + variableString;
    }
}
