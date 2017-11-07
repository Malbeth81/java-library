package malbeth.javautils.http;

import malbeth.javautils.ByteUtils;

import java.net.InetAddress;

public class HTTPRequest {
    public byte[] content = null;
    public HTTPRequestHeaders headers = new HTTPRequestHeaders();
    public HTTPMethod method = HTTPMethod.GET;
    public String remoteAddr = "";
    public String remoteHost = "";
    public String url = "/";
    public HTTPVersion version = HTTPVersion.HTTP10;

    public HTTPRequest() {
    }

    public final byte[] getHeaderBytes() {
        try {
            return toString().getBytes("US-ASCII");
        } catch (Exception e) {
        }
        return new byte[]{};
    }

    public static final HTTPRequest parse(InetAddress address, byte[] header) {
        if (header != null && header.length > 0) {
            int requestSize = ByteUtils.byteArrayIndexOf(header, new byte[]{0x0D, 0x0A});
            if (requestSize >= 0) {
                try {
                    if (address == null)
                        address = InetAddress.getLocalHost();
                    HTTPRequest result = new HTTPRequest();
                    String[] request = new String(header, 0, requestSize, "US-ASCII").trim().split(" ");
                    if (request.length >= 3) {
                        result.method = HTTPMethod.parse(request[0]);
                        result.url = request[1];
                        result.version = HTTPVersion.parse(request[2]);
                    }
                    result.headers = HTTPRequestHeaders.parse(new String(header, requestSize + 2, header.length - requestSize - 2, "US-ASCII"));
                    result.remoteAddr = address.getHostAddress();
                    result.remoteHost = address.getHostName();
                    return result;
                } catch (Exception e) {
                    System.err.println("An error occured:");
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public final String toString() {
        if (method != null && url != null && version != null) {
            return method + " " + url + " " + version + "\r\n" + (headers != null ? headers : "") + "\r\n";
        }
        return "";
    }

}
