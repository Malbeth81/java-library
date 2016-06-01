package com.heresysoft.arsenal.socket;

import com.heresysoft.arsenal.utils.ByteUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public abstract class ConnectionHandler implements Runnable {
    private boolean active = false;
    private byte buffer[] = new byte[128];
    private boolean closed = false;
    private final InputStream inputStream;
    private final Object lock_buffer = new Object();
    private final OutputStream outputStream;
    private final Socket socket;

    public ConnectionHandler(Socket socket) throws IOException {
        if (socket == null)
            throw new NullPointerException("Argument \"socket\" cannot be null");

        // Configure socket
        socket.setKeepAlive(true);
        socket.setSoTimeout(200);

        // Initialize variables
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        this.socket = socket;
    }

    public final void close(boolean reconnect) {
        synchronized (this) {
            if (closed)
                return;

            // Close socket
            closed = true;
        }

        // Close input streams
        synchronized (inputStream) {
            try {
                inputStream.close();
            } catch (Exception ignored) {
            }
        }

        closing(reconnect);
    }

    protected abstract void closing(boolean reconnect);

    protected abstract void dataSent(byte[] data);

    public final int getBufferSize() {
        synchronized (lock_buffer) {
            // Current buffer size
            return buffer.length;
        }
    }

    protected abstract void handleData(byte[] data);

    public final boolean isActive() {
        // Return whether this client thread is still active
        return active;
    }

    public final boolean isClosed() {
        // Return whether this client thread is closed
        return closed;
    }

    @Override
    public final void run() {
        // Initialize
        synchronized (this) {
            active = true;
        }

        int dataSize;
        while (true) {
            try {
                synchronized (this) {
                    if (closed)
                        return;
                }
                // Receive incoming data
                synchronized (inputStream) {
                    synchronized (lock_buffer) {
                        dataSize = inputStream.read(buffer);
                    }
                }
                if (dataSize == -1)
                    break;
                else if (dataSize > 0)
                    handleData(ByteUtils.copyArray(buffer, 0, dataSize));
            } catch (java.net.SocketTimeoutException e) {
                Thread.yield();
            } catch (Exception e) {
                break;
            }
        }

        // Stop client handler
        terminate();
    }

    public final boolean sendData(byte[] data) {
        if (data != null) {
            // Send the data to the connected client
            synchronized (outputStream) {
                try {
                    outputStream.write(data);
                    outputStream.flush();

                    dataSent(data);

                    return true;
                } catch (Exception ignored) {
                }
            }
        }

        return false;
    }

    public final void setBufferSize(int size) {
        // Set buffer size
        synchronized (lock_buffer) {
            buffer = new byte[size];
        }
    }

    public final void terminate() {
        // Close client
        synchronized (this) {
            if (!active)
                return;

            if (!closed)
                close(false);
        }

        // Close output streams
        synchronized (outputStream) {
            try {
                outputStream.close();
            } catch (Exception ignored) {
            }
        }

        // Close socket
        synchronized (socket) {
            try {
                socket.close();
            } catch (Exception ignored) {
            }
        }

        // Terminate
        synchronized (this) {
            active = false;
        }

        terminated();
    }

    protected abstract void terminated();

}
