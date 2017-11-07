package malbeth.javautils.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ResultSetCache {
    private Map<String, CacheEntry> cache = new HashMap<String, CacheEntry>();
    private CacheWorker worker = null;

    private class CacheEntry {
        public long timestamp;
        public long keepUntilTimestamp;
        public CachedResultSet resultSet;

        public CacheEntry(CachedResultSet resultSet, int timeToKeep) {
            this.resultSet = resultSet;
            this.timestamp = System.currentTimeMillis();
            this.keepUntilTimestamp = this.timestamp + timeToKeep * 1000;
        }
    }

    private class CacheWorker implements Runnable {
        public boolean active = true;

        public void run() {
            while (active) {
                synchronized (cache) {
                    try {
                        // Remove expired entries
                        for (Iterator<Map.Entry<String, CacheEntry>> it = cache.entrySet().iterator(); it.hasNext(); ) {
                            CacheEntry entry = it.next().getValue();
                            if (System.currentTimeMillis() >= entry.keepUntilTimestamp)
                                it.remove();
                        }
                    } catch (Exception e) {
                        System.err.println("An error occured:");
                        e.printStackTrace();
                    }
                }

                // Pause thread
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    System.err.println("An error occured:");
                    e.printStackTrace();
                }
            }
        }
    }

    public ResultSetCache() {
        worker = new CacheWorker();
        new Thread(worker).start();
    }

    public void clear() {
        // Clear cache
        synchronized (cache) {
            cache.clear();
        }
    }

    public void delete(String sqlStatement) {
        // Remove from cache
        if (sqlStatement != null) {
            synchronized (cache) {
                cache.remove(sqlStatement);
            }
        }
    }

    public Long getTimestamp(String sqlStatement) {
        // Recover timestamp from cache
        if (sqlStatement != null) {
            synchronized (cache) {
                if (cache.containsKey(sqlStatement)) {
                    CacheEntry entry = cache.get(sqlStatement);
                    if (System.currentTimeMillis() < entry.keepUntilTimestamp)
                        return entry.timestamp;
                }
            }
        }

        return null;
    }

    public CachedResultSet retrieve(String sqlStatement) {
        // Recover result set from cache
        if (sqlStatement != null) {
            synchronized (cache) {
                if (cache.containsKey(sqlStatement)) {
                    CacheEntry entry = cache.get(sqlStatement);
                    if (System.currentTimeMillis() < entry.keepUntilTimestamp)
                        return entry.resultSet;
                }
            }
        }

        return null;
    }

    public CachedResultSet store(String sqlStatement, ResultSet resultSet, int timeToKeep) throws SQLException {
        // Add to cache
        if (sqlStatement != null && resultSet != null && timeToKeep > 0) {
            CachedResultSet cachedResultSet = CachedResultSet.createFrom(resultSet);

            if (cachedResultSet != null) {
                synchronized (cache) {
                    cache.put(sqlStatement, new CacheEntry(cachedResultSet, timeToKeep));
                }
            }

            return cachedResultSet;
        }

        return null;
    }

}
