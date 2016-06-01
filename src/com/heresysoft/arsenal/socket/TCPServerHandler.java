package com.heresysoft.arsenal.socket;

import java.net.ServerSocket;

public abstract class TCPServerHandler extends ServerHandler {
    private final Object lock_socket = new Object();
    private ServerSocket socket = null;

    public TCPServerHandler(ServerBinding binding) {
        super(binding);
    }

    @Override
    protected void closing() {
        // Close TCP socket
        synchronized (lock_socket) {
            try {
                socket.close();
            } catch (Exception ignored) {
            }
            socket = null;
        }
    }

    @Override
    protected boolean execute() {
        synchronized (lock_socket) {
            try {
                // Accept TCP connection request and create connection handler
                new Thread(initializeConnectionHandler(socket.accept())).start();

                return true;
            } catch (java.net.SocketTimeoutException e) {
                // Accepting a TCP connection timed out, not a problem
                return true;
            } catch (Exception ignored) {
            }
        }

        return false;
    }

    @Override
    protected boolean open() {
        synchronized (lock_socket) {
            try {
                // Start TCP server socket
                socket = new ServerSocket(getBinding().getPort(), 0, getBinding().getInetAddress());
                socket.setSoTimeout(200);

                return true;
            } catch (Exception ignored) {
            }
        }

        return false;
    }

}
