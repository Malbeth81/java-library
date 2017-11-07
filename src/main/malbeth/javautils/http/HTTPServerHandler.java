package malbeth.javautils.http;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public abstract class HTTPServerHandler implements Runnable {
    private boolean active = false;
    private boolean closed = false;
    private int connectionCount = 0;
    private List<HTTPServerConnectionHandler> httpConnections = Collections.synchronizedList(new LinkedList<HTTPServerConnectionHandler>());
    private List<HTTPServerConnectionHandler> httpsConnections = Collections.synchronizedList(new LinkedList<HTTPServerConnectionHandler>());
    private int maxHTTPConnections = 1000;
    private String serverAddress = null;
    private int serverPort = 80;
    private int serverSecurePort = 443;
    private ServerSocket serverSocket = null;
    private ServerSocket serverSecureSocket = null;
    private List<HTTPServerConnectionHandler> webSocketConnections = Collections.synchronizedList(new LinkedList<HTTPServerConnectionHandler>());

    public HTTPServerHandler(String address, int port, int securePort, int maxConnections) {
        serverAddress = address;
        serverPort = port;
        serverSecurePort = securePort;
        maxHTTPConnections = maxConnections;
    }

    protected final boolean addConnectionHandler(HTTPServerConnectionHandler connection, boolean secure) {
        boolean result = false;

        // Add connection handler to list, must skip when server is closing to avoid deadlock!
        if (!closed && connection != null) {
            connection.setId(++connectionCount);

            // Close oldest connection
            synchronized (httpConnections) {
                synchronized (httpsConnections) {
                    if (httpConnections.size() + httpsConnections.size() >= maxHTTPConnections) {
                        if (httpsConnections.size() > 0 && (httpConnections.size() == 0 || httpsConnections.get(0).getId() < httpConnections.get(0).getId()))
                            httpsConnections.remove(0).close();
                        else if (httpConnections.size() > 0)
                            httpConnections.remove(0).close();
                    }

                    // Add new connection
                    if (secure)
                        httpsConnections.add(connection);
                    else
                        httpConnections.add(connection);
                }
            }
        }
        return result;
    }

    public final void close() {
        if (!closed) {
            // Close server
            closed = true;

            // Close socket
            if (serverSocket != null) {
                synchronized (serverSocket) {
                    try {
                        serverSocket.close();
                    } catch (Exception e) {
                        System.err.println("An error occured:");
                        e.printStackTrace();
                    }
                    serverSocket = null;
                }
            }

            // Close secure socket
            if (serverSecureSocket != null) {
                synchronized (serverSecureSocket) {
                    try {
                        serverSecureSocket.close();
                    } catch (Exception e) {
                        System.err.println("An error occured:");
                        e.printStackTrace();
                    }
                    serverSecureSocket = null;
                }
            }

            // Signal all connections to close
            synchronized (httpConnections) {
                for (ListIterator<HTTPServerConnectionHandler> it = httpConnections.listIterator(); it.hasNext(); )
                    it.next().close();
            }
            synchronized (httpsConnections) {
                for (ListIterator<HTTPServerConnectionHandler> it = httpsConnections.listIterator(); it.hasNext(); )
                    it.next().close();
            }
            synchronized (webSocketConnections) {
                for (ListIterator<HTTPServerConnectionHandler> it = webSocketConnections.listIterator(); it.hasNext(); )
                    it.next().close();
            }
        }
    }

    public final void dropConnection(String address) {
        if (address != null) {
            synchronized (httpConnections) {
                for (ListIterator<HTTPServerConnectionHandler> it = httpConnections.listIterator(); it.hasNext(); ) {
                    HTTPServerConnectionHandler connection = it.next();
                    if (connection.getRemoteAddress().equals(address))
                        connection.close();
                }
            }
            synchronized (httpsConnections) {
                for (ListIterator<HTTPServerConnectionHandler> it = httpsConnections.listIterator(); it.hasNext(); ) {
                    HTTPServerConnectionHandler connection = it.next();
                    if (connection.getRemoteAddress().equals(address))
                        connection.close();
                }
            }
            synchronized (webSocketConnections) {
                for (ListIterator<HTTPServerConnectionHandler> it = webSocketConnections.listIterator(); it.hasNext(); ) {
                    HTTPServerConnectionHandler connection = it.next();
                    if (connection.getRemoteAddress().equals(address))
                        connection.close();
                }
            }
        }
    }

    public final String getAddress() {
        // Returns the IP address the server is bound to
        return serverAddress;
    }

    public final int getPort() {
        // Return the port the server is bound to
        return serverPort;
    }

    public final int getSecurePort() {
        // Return the secure port the server is bound to
        return serverSecurePort;
    }

    protected abstract HTTPServerConnectionHandler initializeConnectionHandler(Socket socket, boolean secure);

    public final boolean isActive() {
        // Return whether the server thread is still active
        return active;
    }

    public final boolean isClosed() {
        // Return whether the server is closing
        return closed;
    }

    protected final boolean removeConnectionHandler(HTTPServerConnectionHandler connection) {
        boolean result = false;

        // Remove connection handler from list, must skip when server is closing to avoid deadlock!
        if (!closed && connection != null) {
            synchronized (httpConnections) {
                result = result || httpConnections.remove(connection);
            }
            synchronized (httpsConnections) {
                result = result || httpsConnections.remove(connection);
            }
            synchronized (webSocketConnections) {
                result = result || webSocketConnections.remove(connection);
            }
        }
        return result;
    }

    @Override
    public final void run() {
        // Initialize
        active = true;

        // Open server
        try {
            if (serverPort > 0) {
                serverSocket = new ServerSocket(serverPort, 0, (serverAddress != null && serverAddress.length() > 0 ? InetAddress.getByName(serverAddress.trim()) : null));
                serverSocket.setSoTimeout(200);
            }
            if (serverSecurePort > 0) {
                serverSecureSocket = new ServerSocket(serverSecurePort, 0, (serverAddress != null && serverAddress.length() > 0 ? InetAddress.getByName(serverAddress.trim()) : null));
                serverSecureSocket.setSoTimeout(200);
            }
            while (!closed && (serverSocket != null || serverSecureSocket != null)) {
                if (serverSocket != null) {
                    synchronized (serverSocket) {
                        if (serverSocket.isBound() && !serverSocket.isClosed()) {
                            try {
                                // Accept connection request
                                Socket socket = serverSocket.accept();

                                // Create new connection handler
                                HTTPServerConnectionHandler connection = initializeConnectionHandler(socket, false);
                                if (!closed && connection != null) {
                                    addConnectionHandler(connection, false);

                                    // Start new connection handler
                                    new Thread(connection).start();
                                }
                            } catch (java.net.SocketTimeoutException e) {
                                // Looking for a new connection timed out, not a problem
                            } catch (Exception e) {
                                System.err.println("An error occured:");
                                e.printStackTrace();
                                break;
                            }
                        } else
                            serverSocket = null;
                    }
                }
                if (serverSecureSocket != null) {
                    synchronized (serverSecureSocket) {
                        if (serverSecureSocket.isBound() && !serverSecureSocket.isClosed()) {
                            try {
                                // Accept connection request
                                Socket socket = serverSecureSocket.accept();

                                // Create new connection handler
                                HTTPServerConnectionHandler connection = initializeConnectionHandler(socket, true);
                                if (!closed && connection != null) {
                                    addConnectionHandler(connection, true);

                                    // Start new connection handler
                                    new Thread(connection).start();
                                }
                            } catch (java.net.SocketTimeoutException e) {
                                // Looking for a new connection timed out, not a problem
                            } catch (Exception e) {
                                System.err.println("An error occured:");
                                e.printStackTrace();
                                break;
                            }
                        } else
                            serverSecureSocket = null;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("An error occured:");
            e.printStackTrace();
        }

        // Stop server
        shutdown();
    }

    public final void shutdown() {
        if (active) {
            // Close server
            if (!closed)
                close();

            try {
                // Wait for all connections to have closed
                boolean allClosed = false;
                long timestamp = System.currentTimeMillis();
                while (!allClosed && System.currentTimeMillis() - timestamp < 10000) // 10 seconds
                {
                    allClosed = true;
                    synchronized (httpConnections) {
                        for (ListIterator<HTTPServerConnectionHandler> it = httpConnections.listIterator(); it.hasNext(); )
                            if (it.next().isActive())
                                allClosed = false;
                    }
                    synchronized (httpsConnections) {
                        for (ListIterator<HTTPServerConnectionHandler> it = httpsConnections.listIterator(); it.hasNext(); )
                            if (it.next().isActive())
                                allClosed = false;
                    }
                    synchronized (webSocketConnections) {
                        for (ListIterator<HTTPServerConnectionHandler> it = webSocketConnections.listIterator(); it.hasNext(); )
                            if (it.next().isActive())
                                allClosed = false;
                    }
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        System.err.println("An error occured:");
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                System.err.println("An error occured:");
                e.printStackTrace();
            }

            active = false;
        }
    }

    protected final boolean upgradeConnection(HTTPServerConnectionHandler connection, String upgradeTo) {
        if (!closed && connection != null && upgradeTo != null) {
            if (connection.onUpgrade(upgradeTo)) {
                boolean removed = false;

                if (connection.isSecure()) {
                    synchronized (httpsConnections) {
                        removed = httpsConnections.remove(connection);
                    }
                } else {
                    synchronized (httpConnections) {
                        removed = httpConnections.remove(connection);
                    }
                }

                if (upgradeTo.equalsIgnoreCase("websocket") && removed) {
                    // Upgrade this connection to a web socket connection
                    synchronized (webSocketConnections) {
                        return webSocketConnections.add(connection);
                    }
                }
            }
        }
        return false;
    }

    public final void writeFrameToAllWebSockets(WebSocketFrame frame) {
        // Send to all web socket connections
        synchronized (webSocketConnections) {
            try {
                for (ListIterator<HTTPServerConnectionHandler> it = webSocketConnections.listIterator(); it.hasNext(); ) {
                    HTTPServerConnectionHandler connection = it.next();
                    connection.writeWebSocketFrame(frame);
                }
            } catch (Exception e) {
                System.err.println("An error occured:");
                e.printStackTrace();
            }
        }
    }

}
