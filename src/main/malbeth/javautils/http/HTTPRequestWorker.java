package malbeth.javautils.http;

import malbeth.javautils.ByteUtils;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

public class HTTPRequestWorker implements Runnable {
    private boolean abort = false;
    private int bufferSize = 1024;
    private boolean completed = false;
    private byte[] dataRemainder = null;
    private HTTPRequest request = null;
    private HTTPRequestResult result = null;
    private Socket socket = null;
    private int timeout = 1000;

    public static class HTTPRequestResult {
        public HTTPResponse response = null;
        public long timestampStart = 0;
        public long timestampRequest = 0;
        public long timestampEnd = 0;

        public HTTPRequestResult clone() {
            HTTPRequestResult result = new HTTPRequestResult();
            result.response = response;
            result.timestampStart = timestampStart;
            result.timestampRequest = timestampRequest;
            result.timestampEnd = timestampEnd;
            return result;
        }
    }

    private HTTPRequestWorker(Socket socket, HTTPRequest request) {
        this.socket = socket;
        this.request = request;
    }

    private HTTPRequestWorker(Socket socket, HTTPRequest request, int timeout) {
        this.socket = socket;
        this.request = request;
        this.timeout = timeout;
    }

    public final void abort() {
        abort = true;
    }

    public final boolean aborted() {
        return abort;
    }

    public final boolean completed() {
        return completed;
    }

    public static final HTTPRequestResult execute(InetAddress address, int port, HTTPRequest request) throws InterruptedException {
        try {
            Socket socket = new Socket(address, port);
            socket.setSoTimeout(100);
            HTTPRequestWorker worker = new HTTPRequestWorker(socket, request);
            worker.run();
            socket.close();
            return worker.getResult();
        } catch (Exception e) {
        }
        return null;
    }

    public static final HTTPRequestResult execute(InetAddress address, int port, HTTPRequest request, int timeout) throws InterruptedException {
        try {
            Socket socket = new Socket(address, port);
            socket.setSoTimeout(100);
            HTTPRequestWorker worker = new HTTPRequestWorker(socket, request, timeout);
            worker.run();
            socket.close();
            return worker.getResult();
        } catch (Exception e) {
        }
        return null;
    }


    public static final HTTPRequestResult execute(String address, int port, HTTPRequest request) throws InterruptedException {
        try {
            Socket socket = new Socket(address, port);
            socket.setSoTimeout(100);
            HTTPRequestWorker worker = new HTTPRequestWorker(socket, request);
            worker.run();
            socket.close();
            return worker.getResult();
        } catch (Exception e) {
        }
        return null;
    }

    public static final HTTPRequestResult execute(String address, int port, HTTPRequest request, int timeout) throws InterruptedException {
        try {
            Socket socket = new Socket(address, port);
            socket.setSoTimeout(100);
            HTTPRequestWorker worker = new HTTPRequestWorker(socket, request, timeout);
            worker.run();
            socket.close();
            return worker.getResult();
        } catch (Exception e) {
        }
        return null;
    }

    public static final HTTPRequestResult execute(Socket socket, HTTPRequest request) throws InterruptedException {
        HTTPRequestWorker worker = new HTTPRequestWorker(socket, request);
        worker.run();
        return worker.getResult();
    }

    public static final HTTPRequestResult execute(Socket socket, HTTPRequest request, int timeout) throws InterruptedException {
        HTTPRequestWorker worker = new HTTPRequestWorker(socket, request, timeout);
        worker.run();
        return worker.getResult();
    }

    public static final HTTPRequestWorker executeAsync(Socket socket, HTTPRequest request) {
        HTTPRequestWorker worker = new HTTPRequestWorker(socket, request);
        Thread thread = new Thread(worker);
        thread.start();
        return worker;
    }

    public static final HTTPRequestWorker executeAsync(Socket socket, HTTPRequest request, int timeout) {
        HTTPRequestWorker worker = new HTTPRequestWorker(socket, request, timeout);
        Thread thread = new Thread(worker);
        thread.start();
        return worker;
    }

    public final Socket getSocket() {
        return socket;
    }

    public final HTTPRequest getRequest() {
        return request;
    }

    public final HTTPRequestResult getResult() {
        return result.clone();
    }

    public final int getTimeout() {
        return timeout;
    }

    private final void handleData(byte[] data) {
        if (data != null && data.length > 0 && result != null) {
            if (dataRemainder != null && dataRemainder.length > 0) {
                data = ByteUtils.appendToArray(dataRemainder, data);
                dataRemainder = null;
            }
            if (result.response != null && result.response.content == null && result.response.headers.ContentLength != null && data.length >= result.response.headers.ContentLength) {
                // Recover the response content
                int contentSize = result.response.headers.ContentLength;
                result.response.content = (data.length > contentSize ? ByteUtils.copyArray(data, 0, contentSize) : data);
                completed = true;
            } else if (result.response == null) {
                // Read the response header
                int headerSize = ByteUtils.byteArrayIndexOf(data, new byte[]{0x0D, 0x0A, 0x0D, 0x0A});
                if (headerSize >= 0) {
                    result.response = HTTPResponse.parse(data.length > headerSize ? ByteUtils.copyArray(data, 0, headerSize) : data);

                    if (result.response != null && (result.response.headers.ContentLength == null || result.response.headers.ContentLength <= 0))
                        completed = true;
                    else if (data.length > headerSize + 4)
                        handleData(ByteUtils.copyArray(data, headerSize + 4)); // +4 to skip delimiter (0x0D,0x0A,0x0D,0x0A)
                } else
                    dataRemainder = data;
            } else
                dataRemainder = data;
        }
    }

    @Override
    public final void run() {
        if (request != null) {
            try {
                abort = false;
                completed = false;
                dataRemainder = null;
                result = new HTTPRequestResult();
                result.timestampStart = System.nanoTime();

                // Initialize streams
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                PrintStream outputStream = new PrintStream(socket.getOutputStream());
                while (inputStream.skipBytes(inputStream.available()) > 0) ;

                // Send request
                outputStream.write(request.getHeaderBytes());
                if (request.content != null && request.content.length > 0)
                    outputStream.write(request.content);
                outputStream.flush();

                result.timestampRequest = System.nanoTime();
                while (!completed && !abort && System.nanoTime() - result.timestampRequest < timeout * 1000000L) {
                    try {
                        // Read incoming data
                        byte buffer[] = new byte[bufferSize];
                        int dataSize = inputStream.read(buffer);
                        if (dataSize == -1)
                            break;
                        else if (dataSize > 0)
                            handleData((dataSize < bufferSize ? ByteUtils.copyArray(buffer, 0, dataSize) : buffer));
                    } catch (java.net.SocketTimeoutException e) {
                        Thread.yield();
                    } catch (Exception e) {
                        System.err.println("An error occured:");
                        e.printStackTrace();

                        // The connection was lost
                        break;
                    }
                }
                result.timestampEnd = System.nanoTime();
            } catch (Exception e) {
                System.err.println("An error occured:");
                e.printStackTrace();
            }
        }
    }

    public final void setSocket(Socket socket) {
        this.socket = socket;
    }

    public final void setRequest(HTTPRequest request) {
        this.request = request;
    }

    public final void setTimeout(int timeout) {
        this.timeout = timeout;
    }

}
