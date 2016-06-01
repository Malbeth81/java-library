package com.heresysoft.arsenal.socket;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public abstract class ServerHandler implements Runnable {
    private boolean active = false;
    private final ServerBinding binding;
    private final Map<Long, ServerConnectionHandler> clients = new HashMap<>();
    private boolean closed = false;
    private long connectionCount = 0;

    public ServerHandler(ServerBinding binding) {
        this.binding = binding;
    }

    protected final long addConnection(ServerConnectionHandler connection) {
        // Add connection handler to the list, must skip when server is closed or it can create a deadlock!
        if (connection != null) {
            long id;
            synchronized (this) {
                if (closed)
                    return 0;

                id = (connectionCount < Long.MAX_VALUE ? connectionCount + 1 : 1);
                connectionCount = id;
            }

            synchronized (clients) {
                clients.put(id, connection);
            }

            return id;
        }

        return 0;
    }

    public final void close() {
        synchronized (this) {
            if (closed)
                return;

            // Close server
            closed = true;
        }

        closing();

        // Signal all connections to close
        synchronized (clients) {
            for (ServerConnectionHandler serverConnectionHandler : clients.values())
                serverConnectionHandler.close(false);
        }
    }

    protected abstract void closing();

    protected abstract boolean execute();

    public final ServerBinding getBinding() {
        // Returns the binding for this server
        return binding;
    }

    public final ServerConnectionHandler getClient(long id) {
        // Returns the connection with the specified id
        synchronized (clients) {
            return clients.get(id);
        }
    }

    protected abstract ServerConnectionHandler initializeConnectionHandler(Socket socket);

    public final boolean isActive() {
        // Return whether the server is still running
        return active;
    }

    public final boolean isClosed() {
        // Return whether the server is closed
        return closed;
    }

    protected abstract boolean open();

    protected final void removeConnection(long id) {
        if (id > 0) {
            // Remove connection handler from list, must skip when server is closed or it can create a deadlock!
            synchronized (this) {
                if (closed)
                    return;
            }

            synchronized (clients) {
                clients.remove(id);
            }
        }
    }

    @Override
    public final void run() {
        // Initialize
        synchronized (this) {
            active = true;
        }

        // Open server
        if (open()) {
            while (true) {
                synchronized (this) {
                    if (closed)
                        break;
                }

                // Execute server processing
                if (!execute())
                    break;
            }
        }

        // Stop server
        terminate();
    }

    public final void terminate() {
        synchronized (this) {
            if (!active)
                return;

            // Close server
            if (!closed)
                close();

            active = false;
        }

        terminated();
    }

    protected abstract void terminated();

    public final void waitForConnections(int timeout) {
        try {
            // Wait for all connections to close
            boolean allClosed = false;
            long timestamp = System.currentTimeMillis();
            while (!allClosed && System.currentTimeMillis() - timestamp < timeout) {
                allClosed = true;
                synchronized (clients) {
                    for (ServerConnectionHandler serverConnectionHandler : clients.values())
                        if (serverConnectionHandler.isActive())
                            allClosed = false;
                }
                try {
                    Thread.sleep(10);
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
    }

}
