package malbeth.javautils.http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HTTPResponseHeaders {
    private Map<String, HTTPCookie> Cookies;

    public String Allow;
    public String CacheControl;
    public String Connection;
    public String ContentEncoding;
    public String ContentDisposition;
    public String ContentLanguage;
    public Integer ContentLength;
    public String ContentMD5;
    public String ContentRange;
    public String ContentType;
    public Long Date;
    public Long Expires;
    public Long LastModified;
    public String Location;
    public String SecWebSocketAccept;
    public String SecWebSocketExtensions;
    public String SecWebSocketProtocol;
    public String Server;
    public String Upgrade;

    public HTTPResponseHeaders() {
        Cookies = new HashMap<String, HTTPCookie>();
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

    public final HTTPCookie getCookie(String name) {
        return Cookies.get(name);
    }

    public static final HTTPResponseHeaders parse(String header) {
        if (header != null && header.length() > 0) {
            HTTPResponseHeaders result = new HTTPResponseHeaders();
            String[] lines = header.split("\r\n");
            for (int i = 0; i < lines.length; i++) {
                int separator = lines[i].indexOf(':');
                if (separator > 0) {
                    String name = lines[i].substring(0, separator).trim();
                    String value = lines[i].substring(separator + 1).trim();
                    if (name.equalsIgnoreCase("Allow"))
                        result.Allow = value;
                    else if (name.equalsIgnoreCase("Cache-Control"))
                        result.CacheControl = value;
                    else if (name.equalsIgnoreCase("Connection"))
                        result.Connection = value;
                    else if (name.equalsIgnoreCase("Content-Encoding"))
                        result.ContentEncoding = value;
                    else if (name.equalsIgnoreCase("Content-Disposition"))
                        result.ContentDisposition = value;
                    else if (name.equalsIgnoreCase("Content-Language"))
                        result.ContentLanguage = value;
                    else if (name.equalsIgnoreCase("Content-Length"))
                        result.ContentLength = HTTPUtils.parseInteger(value);
                    else if (name.equalsIgnoreCase("Content-MD5"))
                        result.ContentMD5 = value;
                    else if (name.equalsIgnoreCase("Content-Range"))
                        result.ContentRange = value;
                    else if (name.equalsIgnoreCase("Content-Type"))
                        result.ContentType = value;
                    else if (name.equalsIgnoreCase("Date"))
                        result.Date = HTTPUtils.valueOfHTTPTimeString(value);
                    else if (name.equalsIgnoreCase("Expires"))
                        result.Expires = HTTPUtils.valueOfHTTPTimeString(value);
                    else if (name.equalsIgnoreCase("Last-Modified"))
                        result.LastModified = HTTPUtils.valueOfHTTPTimeString(value);
                    else if (name.equalsIgnoreCase("Location"))
                        result.Location = value;
                    else if (name.equalsIgnoreCase("Server"))
                        result.Server = value;
                    else if (name.equalsIgnoreCase("Sec-WebSocket-Accept"))
                        result.SecWebSocketAccept = value;
                    else if (name.equalsIgnoreCase("Sec-WebSocket-Extensions"))
                        result.SecWebSocketExtensions = value;
                    else if (name.equalsIgnoreCase("Sec-WebSocket-Protocol"))
                        result.SecWebSocketProtocol = value;
                    else if (name.equalsIgnoreCase("Set-Cookie")) {
                        HTTPCookie cookie = HTTPCookie.parse(value);
                        if (cookie != null)
                            result.Cookies.put(cookie.name, cookie);
                    } else if (name.equalsIgnoreCase("Upgrade"))
                        result.Upgrade = value;
                }
            }
            return result;
        }
        return null;
    }

    public final void setCookie(HTTPCookie cookie) {
        Cookies.put(cookie.name, cookie);
    }

    public final String toString() {
        String result = "";

        if (Allow != null && Allow.length() > 0)
            result += "Allow:" + Allow + "\r\n";

        if (CacheControl != null && CacheControl.length() > 0)
            result += "Cache-Control:" + CacheControl + "\r\n";

        if (Connection != null && Connection.length() > 0)
            result += "Connection:" + Connection + "\r\n";

        if (ContentEncoding != null && ContentEncoding.length() > 0)
            result += "Content-Encoding:" + ContentEncoding + "\r\n";

        if (ContentDisposition != null && ContentDisposition.length() > 0)
            result += "Content-Disposition:" + ContentDisposition + "\r\n";

        if (ContentLanguage != null && ContentLanguage.length() > 0)
            result += "Content-Language:" + ContentLanguage + "\r\n";

        if (ContentMD5 != null && ContentMD5.length() > 0)
            result += "Content-MD5:" + ContentMD5 + "\r\n";

        if (ContentLength != null)
            result += "Content-Length:" + ContentLength + "\r\n";

        if (ContentRange != null && ContentRange.length() > 0)
            result += "Content-Range:" + ContentRange + "\r\n";

        if (ContentType != null && ContentType.length() > 0)
            result += "Content-Type:" + ContentType + "\r\n";

        if (Date != null)
            result += "Date:" + HTTPUtils.formatHTTPTimeString(Date) + "\r\n";

        if (Expires != null)
            result += "Expires:" + HTTPUtils.formatHTTPTimeString(Expires) + "\r\n";

        if (LastModified != null)
            result += "Last-Modified:" + HTTPUtils.formatHTTPTimeString(LastModified) + "\r\n";

        if (Location != null)
            result += "Location:" + Location + "\r\n";

        if (Server != null && Server.length() > 0)
            result += "Server:" + Server + "\r\n";

        if (SecWebSocketAccept != null && SecWebSocketAccept.length() > 0)
            result += "Sec-WebSocket-Accept:" + SecWebSocketAccept + "\r\n";

        if (SecWebSocketExtensions != null && SecWebSocketExtensions.length() > 0)
            result += "Sec-WebSocket-Extensions:" + SecWebSocketExtensions + "\r\n";

        if (SecWebSocketProtocol != null && SecWebSocketProtocol.length() > 0)
            result += "Sec-WebSocket-Protocol:" + SecWebSocketProtocol + "\r\n";

        if (Cookies != null) {
            for (Iterator<Map.Entry<String, HTTPCookie>> it = Cookies.entrySet().iterator(); it.hasNext(); ) {
                String cookie = it.next().getValue().toString();
                if (cookie != null)
                    result += "Set-Cookie:" + cookie + "\r\n";
            }
        }

        if (Upgrade != null && Upgrade.length() > 0)
            result += "Upgrade:" + Upgrade + "\r\n";

        return result;
    }
}
