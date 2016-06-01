package com.heresysoft.arsenal.http;

import com.heresysoft.arsenal.utils.ByteUtils;
import com.heresysoft.arsenal.utils.Hash;
import com.heresysoft.arsenal.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public abstract class HTTPServerConnectionHandler implements Runnable {
    public static enum WebServerProtocol {
        HTTP, WebSocket
    }

    ;

    private boolean active = false;
    private int bufferSize = 1024;
    private boolean closed = false;
    private int connectionId = 0;
    private WebSocketFrame frame = null;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;
    private byte[] dataRemainder = null;
    private WebServerProtocol protocol = WebServerProtocol.HTTP;
    private HTTPRequest request = null;
    private HTTPResponse response = null;
    private boolean secure = false;
    private HTTPServerHandler server;
    private boolean showDebug = false;
    private Socket socket = null;
    private int socketTimeout = 50;

    public HTTPServerConnectionHandler(HTTPServerHandler server, Socket socket, boolean secure, boolean showDebug) {
        // Initialize variables
        this.secure = secure;
        this.server = server;
        this.socket = socket;
        this.showDebug = showDebug;

        if (socket != null) {
            try {
                // Configure socket
                socket.setKeepAlive(true);
                socket.setSoTimeout(socketTimeout);

                // Initialize socket input and output
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (Exception e) {
                System.err.println("Thread " + this.connectionId + " - An error occured:");
                e.printStackTrace();
            }
        }
    }

    public final void close() {
        // Close socket
        closed = true;

        // Close input streams
        try {
            inputStream.close();
        } catch (Exception e) {
        }
    }

    protected final byte[] generateHTTPContentFromFile(String filename) {
        try {
            // HTML document request
            File file = new File(filename);
            if (file.exists()) {
                // Read file
                byte[] result = new byte[(int) file.length()];
                FileInputStream stream = new FileInputStream(file);
                stream.read(result);
                stream.close();

                // Determine file content type
                String extension = StringUtils.getExtension(filename);
                if (extension.equalsIgnoreCase("css"))
                    response.headers.ContentType = "text/css";
                else if (extension.equalsIgnoreCase("gif"))
                    response.headers.ContentType = "image/gif";
                else if (extension.equalsIgnoreCase("jpg"))
                    response.headers.ContentType = "image/jpeg";
                else if (extension.equalsIgnoreCase("js"))
                    response.headers.ContentType = "text/javascript";
                else if (extension.equalsIgnoreCase("png"))
                    response.headers.ContentType = "image/png";

                return result;
            }
        } catch (Exception e) {
            System.err.println("Thread " + this.connectionId + " - An error occured:");
            e.printStackTrace();
        }
        return null;
    }

    public final HTTPRequest getCurrentRequest() {
        return request;
    }

    public final HTTPResponse getCurrentResponse() {
        return response;
    }

    public final int getId() {
        return connectionId;
    }

    public final WebServerProtocol getProtocol() {
        return protocol;
    }

    public final String getRemoteAddress() {
        return socket.getInetAddress().getHostAddress();
    }

    protected final void handleData(byte[] data) {
        if (data != null && data.length > 0) {
            if (dataRemainder != null && dataRemainder.length > 0) {
                data = ByteUtils.appendToArray(dataRemainder, data);
                dataRemainder = null;
            }
            if (protocol == WebServerProtocol.WebSocket) {
                // Web socket data
                frame = WebSocketFrame.parse(data);
                if (frame != null) {
                    int frameSize = frame.size();

                    // Process the frame
                    handleFrame();
                    frame = null;

                    // Handle the rest of the data
                    if (data.length > frameSize)
                        handleData(ByteUtils.copyArray(data, frameSize));
                } else
                    dataRemainder = data;
            } else {
                // HTTP request
                if (request == null) {
                    // Read the request header
                    int requestSize = ByteUtils.byteArrayIndexOf(data, new byte[]{0x0D, 0x0A, 0x0D, 0x0A});
                    if (requestSize >= 0) {
                        if (showDebug)
                            System.out.println("Thread " + this.connectionId + " - Received " + requestSize + " bytes");

                        request = HTTPRequest.parse((socket != null ? socket.getInetAddress() : null), data.length > requestSize ? ByteUtils.copyArray(data, 0, requestSize) : data);

                        // Process the request (if it is complete)
                        if (request != null && (request.headers.ContentLength == null || request.headers.ContentLength <= 0)) {
                            handleRequest();
                            request = null;
                        }

                        // Handle the rest of the data
                        if (data.length > requestSize + 4)
                            handleData(ByteUtils.copyArray(data, requestSize + 4)); // +4 to skip delimiter (0x0D,0x0A,0x0D,0x0A)
                    } else
                        dataRemainder = data;
                } else if (request.content == null && request.headers.ContentLength != null && data.length >= request.headers.ContentLength) {
                    // Recover the request content
                    request.content = (data.length > request.headers.ContentLength ? ByteUtils.copyArray(data, 0, request.headers.ContentLength) : data);

                    // Process the request
                    handleRequest();
                    request = null;

                    // Handle the rest of the data
                    if (data.length > request.headers.ContentLength)
                        handleData(ByteUtils.copyArray(data, request.headers.ContentLength));
                } else
                    dataRemainder = data;
            }
        }
    }

    private final void handleFrame() {
        if (frame != null) {
            try {
                if (showDebug) {
                    try {
                        if (frame.getType() == WebSocketFrameType.textFrame)
                            System.out.println("Thread " + this.connectionId + " - Received frame :\r\n" + new String(frame.getContent(), "UTF-8"));
                        else if (frame.getType() == WebSocketFrameType.binaryFrame)
                            System.out.println("Thread " + this.connectionId + " - Received frame :\r\n" + ByteUtils.byteArrayToHexString(frame.getContent()));
                        else if (frame.getType() == WebSocketFrameType.ping)
                            System.out.println("Thread " + this.connectionId + " - Received frame : ping\r\n");
                        else if (frame.getType() == WebSocketFrameType.pong)
                            System.out.println("Thread " + this.connectionId + " - Received frame : pong\r\n");
                    } catch (Exception e) {
                    }
                }

                long responseStart = System.currentTimeMillis();

                processFrame(frame);

                // Notification
                requestProcessed((int) (System.currentTimeMillis() - responseStart));

                // Connection closure
                if (frame.getType() == WebSocketFrameType.connectionClose)
                    close();
            } catch (Exception e) {
                System.err.println("Thread " + this.connectionId + " - An error occured:");
                e.printStackTrace();

                onError();

                // Close connection
                close();
            }
        }
    }

    private final void handleRequest() {
        if (request != null) {
            try {
                if (showDebug)
                    System.out.println("Thread " + this.connectionId + " - Received request :\r\n" + request);

                long responseStart = System.currentTimeMillis();
                WebServerProtocol switchToProtocol = null;

                // Prepare the response
                response = new HTTPResponse();

                // Set response version
                response.version = HTTPVersion.HTTP11;

                // Set response status
                if (request == null)
                    response.status = HTTPStatus.HTTP400;
                else if (!request.method.equals(HTTPMethod.GET) && !request.method.equals(HTTPMethod.POST))
                    response.status = HTTPStatus.HTTP501;
                else if (StringUtils.listContainsIgnoreCase(request.headers.Connection, "Upgrade")) {
                    // Upgrade connection
                    if (request.headers.Upgrade.equalsIgnoreCase("websocket") && request.headers.SecWebSocketKey != null && server.upgradeConnection(this, request.headers.Upgrade)) {
                        response.status = HTTPStatus.HTTP101;
                        response.headers.Connection = "Upgrade";
                        response.headers.SecWebSocketAccept = ByteUtils.byteArrayToBase64(Hash.getHash("SHA-1", request.headers.SecWebSocketKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"));
                        response.headers.Upgrade = "websocket";
                        switchToProtocol = WebServerProtocol.WebSocket;
                    } else
                        response.status = HTTPStatus.HTTP501;
                } else {
                    // Get response content
                    processRequest(request, response);

                    // Update status
                    if (response.status == HTTPStatus.HTTP200 && response.content == null)
                        response.status = HTTPStatus.HTTP404;

                    // Set default date
                    if (response.headers.Date == null)
                        response.headers.Date = System.currentTimeMillis();

                    // Set default content length
                    if (response.headers.ContentLength == null)
                        response.headers.ContentLength = (response.content != null ? response.content.length : 0);

                    // Set default content type
                    if (response.headers.ContentType == null) {
                        String charset = StringUtils.getCharacterEncoding(response.content);
                        if (charset.length() > 0)
                            response.headers.ContentType = "text/html; charset=" + charset.toLowerCase();
                        else
                            response.headers.ContentType = "text/html";
                    }
                }

                // Write response to the client
                if (outputStream != null) {
                    outputStream.write(response.getHeaderBytes());
                    if (response.content != null)
                        outputStream.write(response.content);
                    outputStream.flush();

                    if (showDebug)
                        System.out.println("Thread " + this.connectionId + " - Response sent :\r\n" + response);
                }

                // Notification
                requestProcessed((int) (System.currentTimeMillis() - responseStart));

                // Switch protocol
                if (switchToProtocol != null && response.status == HTTPStatus.HTTP101) {
                    protocol = switchToProtocol;

                    if (showDebug)
                        System.out.println("Thread " + this.connectionId + " - Switched protocol.\r\n");
                }

                response = null;
            } catch (Exception e) {
                System.err.println("Thread " + this.connectionId + " - An error occured:");
                e.printStackTrace();

                onError();

                // Close connection
                close();
            }
        }
    }

    protected abstract void onError();

    protected abstract void onStart();

    protected abstract void onStop();

    protected abstract boolean onUpgrade(String upgradeTo);

    public final boolean isActive() {
        return active;
    }

    public final boolean isClosed() {
        return closed;
    }

    public final boolean isSecure() {
        return secure;
    }

    protected abstract void processFrame(WebSocketFrame frame);

    protected abstract void processRequest(HTTPRequest request, HTTPResponse response);

    protected abstract void requestProcessed(int responseTime);

    @Override
    public final void run() {
        active = true;

        if (showDebug)
            System.out.println("Thread " + this.connectionId + " - Started" + (socket != null ? " for client " + socket.getInetAddress().toString() + ":" + socket.getPort() : ""));

        // Notification
        onStart();

        byte buffer[] = new byte[bufferSize];
        int dataSize = 0;
        while (!closed && inputStream != null) {
            try {
                // Read incoming data
                dataSize = inputStream.read(buffer);
                if (dataSize == -1)
                    break;
                else if (dataSize > 0)
                    handleData(dataSize < bufferSize ? ByteUtils.copyArray(buffer, 0, dataSize) : buffer);
            } catch (java.net.SocketTimeoutException e) {
                Thread.yield();
            } catch (Exception e) {
                // The connection was lost
                if (!(e instanceof java.net.SocketException)) {
                    System.err.println("Thread " + this.connectionId + " - An error occured:");
                    e.printStackTrace();
                }

                onError();

                break;
            }
        }

        // Stop socket handler
        shutdown();
    }

    protected final void setId(int id) {
        this.connectionId = id;
    }

    protected final void shutdown() {
        if (active) {
            if (showDebug)
                System.out.println("Thread " + this.connectionId + " - Closing");

            // Close connection
            if (!closed)
                close();

            // Notification
            onStop();

            // Close output streams
            try {
                outputStream.close();
            } catch (Exception e) {
            }

            // Close socket
            try {
                socket.close();
            } catch (Exception e) {
            }

            if (showDebug)
                System.out.println("Thread " + this.connectionId + " - Stopped");

            active = false;

            server.removeConnectionHandler(this);
        }
    }

    public final boolean writeWebSocketFrame(WebSocketFrame frame) {
        if (frame != null && outputStream != null) {
            byte[] data = frame.getBytes();
            if (data != null) {
                // Write data to the connected client
                try {
                    outputStream.write(data);
                    outputStream.flush();

                    if (showDebug)
                        System.out.println("Thread " + this.connectionId + " - Frame sent :\r\n" + ByteUtils.dataToString(frame.getContent()));

                    return true;
                } catch (Exception e) {
                    System.err.println("Thread " + this.connectionId + " - An error occured:");
                    e.printStackTrace();

                    onError();

                    // Close connection
                    close();
                }
            }
        }
        return false;
    }
}
