package malbeth.javautils.database;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.ListIterator;

public class ConnectionPool {
    private int connectionCount = 0;
    private DataSource dataSource = null;
    private LinkedList<Connection> availableConnections = new LinkedList<Connection>();
    private LinkedList<Connection> loanedConnections = new LinkedList<Connection>();
    private int maxConnections = 0;

    public ConnectionPool(DataSource dataSource, int maxConnections) {
        this.dataSource = dataSource;
        this.maxConnections = Math.max(maxConnections, 1);
    }

    public Connection acquireConnection() {
        return acquireConnection(0);
    }

    public Connection acquireConnection(int timeout) {
        Connection connection = null;
        long timestamp = System.currentTimeMillis();

        if (this.dataSource != null) {
            // Retrieve connection from the list of available connections
            while (connection == null) {
                // Get connection from list of available connections
                synchronized (availableConnections) {
                    try {
                        connection = availableConnections.removeFirst();
                    } catch (Exception e) {
                    }
                }

                if (!isConnectionValid(connection)) {
                    connection = null;
                    connectionCount--;
                }

                if (connection == null) {
                    if (connectionCount < maxConnections) {
                        // Create new connection
                        try {
                            connection = this.dataSource.getConnection();
                            connectionCount++;
                        } catch (Exception e) {
                            System.err.println("An error occured:");
                            e.printStackTrace();
                        }
                    } else if (timeout == 0 || System.currentTimeMillis() < timestamp + timeout) {
                        // Wait for a connection to be returned
                        try {
                            Thread.sleep(10);
                        } catch (Exception e) {
                        }
                    } else {
                        // Abort
                        break;
                    }
                }
            }

            // Add connection to the list of loaned connections
            if (connection != null) {
                synchronized (loanedConnections) {
                    loanedConnections.add(connection);
                }
            }
        }
        return connection;
    }

    public void close() {
        // Close available connections
        synchronized (availableConnections) {
            for (ListIterator<Connection> it = availableConnections.listIterator(); it.hasNext(); ) {
                try {
                    it.next().close();
                } catch (Exception e) {
                    System.err.println("An error occured:");
                    e.printStackTrace();
                }
            }
        }

        // Close loaned connections
        synchronized (loanedConnections) {
            for (ListIterator<Connection> it = loanedConnections.listIterator(); it.hasNext(); ) {
                try {
                    it.next().close();
                } catch (Exception e) {
                    System.err.println("An error occured:");
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean isConnectionValid(Connection connection) {
        boolean result = false;

        if (connection != null) {
            Statement statement = null;
            try {
                // Execute request
                if (!connection.isClosed()) {
                    statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT 1;");
                    if (resultSet != null) {
                        try {
                            result = resultSet.next();
                        } catch (Exception e) {
                            System.err.println("An error occured:");
                            e.printStackTrace();
                        } finally {
                            resultSet.close();
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("An error occured:");
                e.printStackTrace();
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (Exception e) {
                        System.err.println("An error occured:");
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    public void returnConnection(Connection connection) {
        if (this.dataSource != null && connection != null) {
            boolean wasLoaned = false;

            // Remove the connection from the list of loaned connections
            synchronized (loanedConnections) {
                if (loanedConnections.contains(connection)) {
                    loanedConnections.remove(connection);
                    wasLoaned = true;
                }
            }

            // Add the connection to the list of available connections
            if (wasLoaned) {
                synchronized (availableConnections) {
                    availableConnections.add(connection);
                }
            }
        }
    }

}
