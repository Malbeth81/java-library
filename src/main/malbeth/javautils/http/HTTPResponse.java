package malbeth.javautils.http;

import malbeth.javautils.ByteUtils;

public class HTTPResponse {
    public byte[] content = null;
    public HTTPResponseHeaders headers = new HTTPResponseHeaders();
    public HTTPStatus status = HTTPStatus.HTTP200;
    public HTTPVersion version = HTTPVersion.HTTP10;

    public HTTPResponse() {
    }

    public final byte[] getHeaderBytes() {
        try {
            return toString().getBytes("US-ASCII");
        } catch (Exception e) {
        }
        return new byte[]{};
    }

    public static final HTTPResponse parse(byte[] header) {
        if (header != null && header.length > 0) {
            int responseSize = ByteUtils.byteArrayIndexOf(header, new byte[]{0x0D, 0x0A});
            if (responseSize >= 0) {
                try {
                    HTTPResponse result = new HTTPResponse();
                    String[] response = new String(header, 0, responseSize, "US-ASCII").trim().split(" ");
                    if (response.length >= 3) {
                        result.version = HTTPVersion.parse(response[0]);
                        result.status = HTTPStatus.parse(response[1]);
                    }
                    result.headers = HTTPResponseHeaders.parse(new String(header, responseSize + 2, header.length - responseSize - 2, "US-ASCII"));
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
        if (status != null && version != null) {
            return version + " " + status + "\r\n" + (headers != null ? headers : "") + "\r\n";
        }
        return "";
    }

}
