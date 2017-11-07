package malbeth.javautils.http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HTTPRequestHeaders {
    private Map<String, String> Cookies;

    public String Accept;
    public String AcceptCharset;
    public Long AcceptDatetime;
    public String AcceptEncoding;
    public String AcceptLanguage;
    public String Authorization;
    public String CacheControl;
    public String Connection;
    public Integer ContentLength;
    public String ContentMD5;
    public String ContentType;
    public Long Date;
    public String Host;
    public String Origin;
    public String Range;
    public String Referer;
    public String SecWebSocketExtensions;
    public String SecWebSocketKey;
    public String SecWebSocketProtocol;
    public String SecWebSocketVersion;
    public String Upgrade;
    public String UserAgent;

    public HTTPRequestHeaders() {
        Cookies = new HashMap<String, String>();
    }

    public final byte[] getBytes() {
        try {
            return toString().getBytes("UTF-8");
        } catch (Exception e) {
            System.err.println("An error occured:");
            e.printStackTrace();
        }
        return new byte[]{};
    }

    public final String getCookie(String name) {
        return Cookies.get(name);
    }

    public final boolean hasCookie(String name) {
        return Cookies.containsKey(name);
    }

    public static final HTTPRequestHeaders parse(String header) {
        if (header != null && header.length() > 0) {
            HTTPRequestHeaders result = new HTTPRequestHeaders();
            String[] lines = header.split("\r\n");
            for (int i = 0; i < lines.length; i++) {
                int separator = lines[i].indexOf(':');
                if (separator > 0) {
                    String name = lines[i].substring(0, separator).trim();
                    String value = lines[i].substring(separator + 1).trim();
                    if (name.equalsIgnoreCase("Accept"))
                        result.Accept = value;
                    else if (name.equalsIgnoreCase("Accept-Charset"))
                        result.AcceptCharset = value;
                    else if (name.equalsIgnoreCase("Accept-Datetime"))
                        result.AcceptDatetime = HTTPUtils.valueOfHTTPTimeString(value);
                    else if (name.equalsIgnoreCase("Accept-Encoding"))
                        result.AcceptEncoding = value;
                    else if (name.equalsIgnoreCase("Accept-Language"))
                        result.AcceptLanguage = value;
                    else if (name.equalsIgnoreCase("Authorization"))
                        result.Authorization = value;
                    else if (name.equalsIgnoreCase("Cache-Control"))
                        result.CacheControl = value;
                    else if (name.equalsIgnoreCase("Connection"))
                        result.Connection = value;
                    else if (name.equalsIgnoreCase("Content-Length"))
                        result.ContentLength = HTTPUtils.parseInteger(value);
                    else if (name.equalsIgnoreCase("Content-MD5"))
                        result.ContentMD5 = value;
                    else if (name.equalsIgnoreCase("Content-Type"))
                        result.ContentType = value;
                    else if (name.equalsIgnoreCase("Cookie")) {
                        String[] values = value.split(";");
                        for (int j = 0; j < values.length; j++) {
                            int separatora = values[j].indexOf('=');
                            if (separatora > 0)
                                result.Cookies.put(values[j].substring(0, separatora).trim(), values[j].substring(separatora + 1).trim());
                            else
                                result.Cookies.put(values[j].trim(), "");
                        }
                    } else if (name.equalsIgnoreCase("Date"))
                        result.Date = HTTPUtils.valueOfHTTPTimeString(value);
                    else if (name.equalsIgnoreCase("Host"))
                        result.Host = value;
                    else if (name.equalsIgnoreCase("Origin"))
                        result.Origin = value;
                    else if (name.equalsIgnoreCase("Range"))
                        result.Range = value;
                    else if (name.equalsIgnoreCase("Referer"))
                        result.Referer = value;
                    else if (name.equalsIgnoreCase("Sec-WebSocket-Extensions"))
                        result.SecWebSocketExtensions = value;
                    else if (name.equalsIgnoreCase("Sec-WebSocket-Key"))
                        result.SecWebSocketKey = value;
                    else if (name.equalsIgnoreCase("Sec-WebSocket-Protocol"))
                        result.SecWebSocketProtocol = value;
                    else if (name.equalsIgnoreCase("Sec-WebSocket-Version"))
                        result.SecWebSocketVersion = value;
                    else if (name.equalsIgnoreCase("Upgrade"))
                        result.Upgrade = value;
                    else if (name.equalsIgnoreCase("User-Agent"))
                        result.UserAgent = value;
                }
            }
            return result;
        }
        return null;
    }

    public final void setCookie(String name, String value) {
        Cookies.put(name, value);
    }

    public final String toString() {
        String result = "";

        if (Accept != null && Accept.length() > 0)
            result += "Accept:" + Accept + "\r\n";

        if (AcceptCharset != null && AcceptCharset.length() > 0)
            result += "Accept-Charset:" + AcceptCharset + "\r\n";

        if (AcceptDatetime != null)
            result += "Accept-Datetime:" + HTTPUtils.formatHTTPTimeString(AcceptDatetime) + "\r\n";

        if (AcceptEncoding != null && AcceptEncoding.length() > 0)
            result += "Accept-Encoding:" + AcceptEncoding + "\r\n";

        if (AcceptLanguage != null && AcceptLanguage.length() > 0)
            result += "Accept-Language:" + AcceptLanguage + "\r\n";

        if (Authorization != null && Authorization.length() > 0)
            result += "Authorization:" + Authorization + "\r\n";

        if (CacheControl != null && CacheControl.length() > 0)
            result += "Cache-Control:" + CacheControl + "\r\n";

        if (Connection != null && Connection.length() > 0)
            result += "Connection:" + Connection + "\r\n";

        if (ContentLength != null)
            result += "Content-Length:" + ContentLength + "\r\n";

        if (ContentMD5 != null)
            result += "Content-MD5:" + ContentMD5 + "\r\n";

        if (ContentType != null && ContentType.length() > 0)
            result += "Content-Type:" + ContentType + "\r\n";

        if (Cookies.size() > 0) {
            result += "Cookie:";
            for (Iterator<Map.Entry<String, String>> it = Cookies.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, String> cookie = it.next();
                result += cookie.getKey() + "=" + cookie.getValue() + (it.hasNext() ? "; " : "");
            }
            result += "\r\n";
        }

        if (Date != null)
            result += "Date:" + HTTPUtils.formatHTTPTimeString(Date) + "\r\n";

        if (Host != null && Host.length() > 0)
            result += "Host:" + Host + "\r\n";

        if (Origin != null && Origin.length() > 0)
            result += "Origin:" + Origin + "\r\n";

        if (Range != null && Range.length() > 0)
            result += "Range" + Range + "\r\n";

        if (Referer != null && Referer.length() > 0)
            result += "Referer:" + Referer + "\r\n";

        if (SecWebSocketExtensions != null && SecWebSocketExtensions.length() > 0)
            result += "Sec-WebSocket-Extensions:" + SecWebSocketExtensions + "\r\n";

        if (SecWebSocketKey != null && SecWebSocketKey.length() > 0)
            result += "Sec-WebSocket-Key:" + SecWebSocketKey + "\r\n";

        if (SecWebSocketProtocol != null && SecWebSocketProtocol.length() > 0)
            result += "Sec-WebSocket-Protocol:" + SecWebSocketProtocol + "\r\n";

        if (SecWebSocketVersion != null && SecWebSocketVersion.length() > 0)
            result += "Sec-WebSocket-Version:" + SecWebSocketVersion + "\r\n";

        if (Upgrade != null && Upgrade.length() > 0)
            result += "Upgrade:" + Upgrade + "\r\n";

        if (UserAgent != null && UserAgent.length() > 0)
            result += "User-Agent:" + UserAgent + "\r\n";

        return result;
    }
}
