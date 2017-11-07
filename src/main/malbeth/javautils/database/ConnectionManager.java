package malbeth.javautils.database;

import net.sourceforge.jtds.jdbcx.JtdsDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TimeZone;

public abstract class ConnectionManager {
    private ResultSetCache cache = null;
    private int cacheTimeToKeep = 0;              // in seconds
    private ConnectionPool connectionPool = null;

    public static class DataBaseManagerSettings {
        public String applicationName;
        public boolean lastUpdateCount;
        public int loginTimeout;
        public int maxConnections;
        public boolean socketKeepAlive;
        public int socketTimeout;
        public String tdsVersion;
    }

    public ConnectionManager() throws ClassNotFoundException {
        Class.forName("net.sourceforge.jtds.jdbc.Driver");
    }

    public abstract void caughtException(Exception e);

    public final void clearCache() {
        if (cache != null)
            cache.clear();
    }

    public final void close() {
        clearCache();
        if (connectionPool != null)
            connectionPool.close();
    }

    public final void closeStatement(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                if (resultSet.getStatement() != null)
                    resultSet.getStatement().close();
            } catch (Exception e) {
                caughtException(e);

                System.err.println("An error occured:");
                e.printStackTrace();
            }
        }
    }

    public final boolean connect(String database, String address, int port, String username, String password, DataBaseManagerSettings settings) {
        boolean result = false;

        if (address != null && address.length() > 0 && database != null && database.length() > 0) {
            if (connectionPool != null)
                connectionPool.close();

            try {
                JtdsDataSource dataSource = new JtdsDataSource();
                dataSource.setServerName(address.trim());
                if (port > 0)
                    dataSource.setPortNumber(port);
                dataSource.setDatabaseName(database.trim());
                dataSource.setUser(username);
                dataSource.setPassword(password);
                if (settings != null) {
                    dataSource.setAppName(settings.applicationName);
                    dataSource.setLastUpdateCount(settings.lastUpdateCount);
                    dataSource.setLoginTimeout(settings.loginTimeout);
                    dataSource.setSocketKeepAlive(settings.socketKeepAlive);
                    dataSource.setSocketTimeout(settings.socketTimeout);
                    dataSource.setTds(settings.tdsVersion);
                }
                connectionPool = new ConnectionPool(dataSource, (settings != null ? settings.maxConnections : 1));

                return true;
            } catch (Exception e) {
                caughtException(e);

                System.err.println("An error occured:");
                e.printStackTrace();
            }
        } else {
            System.err.println("An error occured: Missing SQL server address and database name");
        }

        return result;
    }

    public final void disableResultSetCache() {
        clearCache();
        cache = null;
    }

    public final void enableResultSetCache(int timeToKeep) {
        if (timeToKeep > 0) {
            cache = new ResultSetCache();
            cacheTimeToKeep = timeToKeep;
        }
    }

    public final Integer executeInsert(String sqlStatement) {
        Integer result = null;

        if (connectionPool != null && sqlStatement != null) {
            // Acquire connection
            Connection connection = connectionPool.acquireConnection();

            // Execute request
            try {
                Statement statement = connection.createStatement();
                if (statement != null) {
                    try {
                        if (statement.executeUpdate(sqlStatement, Statement.RETURN_GENERATED_KEYS) > 0) {
                            ResultSet resultSet = statement.getGeneratedKeys();
                            if (resultSet != null) {
                                try {
                                    if (resultSet.next())
                                        result = resultSet.getInt(1);
                                } catch (Exception e) {
                                    caughtException(e);

                                    System.err.println("An error occured:");
                                    e.printStackTrace();
                                } finally {
                                    resultSet.close();
                                }
                            }
                        }
                    } catch (Exception e) {
                        caughtException(e);

                        System.err.println("An error occured:");
                        e.printStackTrace();
                    } finally {
                        statement.close();
                    }
                }
            } catch (Exception e) {
                caughtException(e);

                System.err.println("An error occured:");
                e.printStackTrace();
            }

            // Returns connection
            connectionPool.returnConnection(connection);
        }
        return result;
    }

    public final ResultSet executeSelect(String sqlStatement) {
        return executeSelect(sqlStatement, false, false);
    }

    public final ResultSet executeSelect(String sqlStatement, boolean cacheable) {
        return executeSelect(sqlStatement, cacheable, false);
    }

    public final ResultSet executeSelect(String sqlStatement, boolean cacheable, boolean refreshCache) {
        ResultSet result = null;

        // Recover from cache
        if (cacheable && !refreshCache)
            result = recoverResultSet(sqlStatement);
        if (result == null) {
            if (connectionPool != null && sqlStatement != null) {
                // Acquire connection
                Connection connection = connectionPool.acquireConnection();

                // Execute request
                try {
                    Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                    if (statement != null) {
                        try {
                            result = statement.executeQuery(sqlStatement);
                        } catch (Exception e) {
                            caughtException(e);

                            System.err.println("An error occured:");
                            e.printStackTrace();
                        } finally {
              /* Don't close the statement since that also closes the result set 
              statement.close();*/
                        }
                    }
                } catch (Exception e) {
                    caughtException(e);

                    System.err.println("An error occured:");
                    e.printStackTrace();
                }

                // Returns connection
                connectionPool.returnConnection(connection);
            }

            // Store in cache
            if (cacheable && result != null) {
                try {
                    CachedResultSet cachedResult = storeResultSet(sqlStatement, result);
                    closeStatement(result);
                    result = cachedResult;
                } catch (Exception e) {
                    caughtException(e);

                    System.err.println("An error occured:");
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public final boolean executeUpdate(String sqlStatement) {
        boolean result = false;

        if (connectionPool != null && sqlStatement != null) {
            // Acquire connection
            Connection connection = connectionPool.acquireConnection();

            // Execute request
            try {
                Statement statement = connection.createStatement();
                if (statement != null) {
                    try {
                        result = (statement.executeUpdate(sqlStatement) > 0);
                    } catch (Exception e) {
                        caughtException(e);

                        System.err.println("An error occured:");
                        e.printStackTrace();
                    } finally {
                        statement.close();
                    }
                }
            } catch (Exception e) {
                caughtException(e);

                System.err.println("An error occured:");
                e.printStackTrace();
            }

            // Returns connection
            connectionPool.returnConnection(connection);
        }
        return result;
    }

    public static final Float getFloat(ResultSet resultSet, int columnindex) {
        if (resultSet != null) {
            try {
                float result = resultSet.getFloat(columnindex);
                if (!resultSet.wasNull())
                    return result;
            } catch (Exception e) {
                System.err.println("An error occured:");
                e.printStackTrace();
            }
        }

        return null;
    }

    public static final Integer getInteger(ResultSet resultSet, int columnindex) {
        if (resultSet != null) {
            try {
                int result = resultSet.getInt(columnindex);
                if (!resultSet.wasNull())
                    return result;
            } catch (Exception e) {
                System.err.println("An error occured:");
                e.printStackTrace();
            }
        }

        return null;
    }

    public final Long getResultSetTimestamp(String sqlStatement) {
        if (cache != null)
            return cache.getTimestamp(sqlStatement);

        return null;
    }

    public static final String queryFormatBoolean(Boolean value) {
        if (value != null)
            return (value ? "1" : "0");
        return "NULL";
    }

    public static final String queryFormatIntegers(Collection<Integer> values) {
        if (values != null && values.size() > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append("(");
            for (Iterator<Integer> it = values.iterator(); it.hasNext(); ) {
                builder.append(it.next());
                if (it.hasNext())
                    builder.append(",");
            }
            builder.append(")");
            return builder.toString();
        }
        return "(SELECT 1 WHERE 1=0)";
    }

    public static final String queryFormatString(String value) {
        if (value != null)
            return "N'" + value.replace("'", "''") + "'";
        return "NULL";
    }

    public static final String queryFormatStrings(Collection<String> values) {
        if (values != null && values.size() > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append("(");
            for (Iterator<String> it = values.iterator(); it.hasNext(); ) {
                builder.append("N'").append(it.next().replace("'", "''")).append("'");
                if (it.hasNext())
                    builder.append(",");
            }
            builder.append(")");
            return builder.toString();
        }
        return "(SELECT '' WHERE 1=0)";
    }

    public static final String queryFormatTimestamp(Long value) {
        if (value != null) {
            // Example: 2012-05-03 16:14:30.709
            GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(value);
            return "'" + String.format("%04d", calendar.get(GregorianCalendar.YEAR)) +
                    "-" + String.format("%02d", calendar.get(GregorianCalendar.MONTH) + 1) +
                    "-" + String.format("%02d", calendar.get(GregorianCalendar.DAY_OF_MONTH)) +
                    " " + String.format("%02d", calendar.get(GregorianCalendar.HOUR_OF_DAY)) +
                    ":" + String.format("%02d", calendar.get(GregorianCalendar.MINUTE)) +
                    ":" + String.format("%02d", calendar.get(GregorianCalendar.SECOND)) +
                    "." + String.format("%03d", calendar.get(GregorianCalendar.MILLISECOND)) +
                    "'";
        }
        return "NULL";
    }

    public static final String queryFormatValue(Object value) {
        if (value != null)
            return value.toString();
        return "NULL";
    }

    public static final Long queryParseTimestamp(String value) {
        if (value != null && value.length() > 0) {
            // Example: 2012-05-03, 2012-05-03 18:30, 2012-05-03 18:30:00.0 or 2012-05-03 18:30:00.0000000
            GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
            try {
                calendar.set(GregorianCalendar.YEAR, Integer.parseInt(value.substring(0, 4)));
                calendar.set(GregorianCalendar.MONTH, Integer.parseInt(value.substring(5, 7)) - 1);
                calendar.set(GregorianCalendar.DAY_OF_MONTH, Integer.parseInt(value.substring(8, 10)));
                if (value.length() >= 16)
                    calendar.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt(value.substring(11, 13)));
                if (value.length() >= 16)
                    calendar.set(GregorianCalendar.MINUTE, Integer.parseInt(value.substring(14, 16)));
                if (value.length() >= 19)
                    calendar.set(GregorianCalendar.SECOND, Integer.parseInt(value.substring(17, 19)));
                if (value.length() > 20)
                    calendar.set(GregorianCalendar.MILLISECOND, Integer.parseInt(value.substring(20, 20 + Math.min(value.length() - 20, 3))));
                return calendar.getTimeInMillis();
            } catch (Exception e) {
                System.err.println("An error occured:");
                e.printStackTrace();
            }
        }
        return null;
    }

    protected final CachedResultSet recoverResultSet(String sqlStatement) {
        if (cache != null)
            return cache.retrieve(sqlStatement);

        return null;
    }

    protected final CachedResultSet storeResultSet(String sqlStatement, ResultSet resultSet) throws SQLException {
        if (cache != null)
            return cache.store(sqlStatement, resultSet, cacheTimeToKeep);

        return null;
    }

}
