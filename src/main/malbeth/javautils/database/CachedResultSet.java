package malbeth.javautils.database;

import net.sourceforge.jtds.jdbc.ClobImpl;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbc.SupportWrapper;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

public class CachedResultSet implements ResultSet {
    private Object[] currentRow;
    private int currentRowIndex;
    private ArrayList<Object[]> rows;
    private boolean wasNull;

    public CachedResultSet() throws ClassNotFoundException {
        Class.forName("net.sourceforge.jtds.jdbc.SupportWrapper");

        currentRow = null;
        currentRowIndex = -1;
        rows = new ArrayList<Object[]>();
    }

    public CachedResultSet(Collection<Object[]> data) throws ClassNotFoundException {
        Class.forName("net.sourceforge.jtds.jdbc.SupportWrapper");

        currentRow = null;
        currentRowIndex = -1;
        rows = new ArrayList<Object[]>(data);
    }

    @Override
    public boolean isWrapperFor(Class<?> arg0) {
        throw new AbstractMethodError();
    }

    @Override
    public <T> T unwrap(Class<T> arg0) {
        throw new AbstractMethodError();
    }

    @Override
    public boolean absolute(int row) {
        if (row >= 0 && row < rows.size()) {
            currentRow = rows.get(row);
            currentRowIndex = row;

            return true;
        }

        return false;
    }

    @Override
    public void afterLast() {
        currentRow = null;
        currentRowIndex = rows.size();
    }

    @Override
    public void beforeFirst() {
        currentRow = null;
        currentRowIndex = -1;
    }

    @Override
    public void cancelRowUpdates() {
    }

    @Override
    public void clearWarnings() {
    }

    @Override
    public void close() {
    }

    public static CachedResultSet createFrom(ResultSet resultSet) throws SQLException {
        if (resultSet != null) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            if (metaData != null) {
                int columnCount = metaData.getColumnCount();

                if (columnCount > 0) {
                    ArrayList<Object[]> rows = new ArrayList<Object[]>();
                    while (resultSet.next()) {
                        Object[] row = new Object[columnCount];
                        for (int i = 0; i < columnCount; i++)
                            row[i] = resultSet.getObject(i + 1);
                        rows.add(row);
                    }

                    try {
                        return new CachedResultSet(rows);
                    } catch (Exception e) {

                    }
                }
            }
        }

        return null;
    }

    @Override
    public void deleteRow() throws SQLException {
        throw new SQLException(Messages.get("error.generic.notimp", "ResultSet.deleteRow()"), "HYC00");
    }

    @Override
    public int findColumn(String columnLabel) {
        return 0;
    }

    @Override
    public boolean first() {
        if (rows.size() > 0) {
            currentRow = rows.get(0);
            currentRowIndex = 0;

            return true;
        }

        return false;
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        throw new SQLException(Messages.get("error.generic.notimp", "ResultSet.getArray()"), "HYC00");
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        return getArray(findColumn(columnLabel));
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        Clob clob = getClob(columnIndex);

        if (clob != null)
            return clob.getAsciiStream();

        return null;
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        return getAsciiStream(findColumn(columnLabel));
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        if (currentRow != null && columnIndex > 0 && columnIndex <= currentRow.length) {
            Object obj = currentRow[columnIndex - 1];
            wasNull = (obj == null);
            return (BigDecimal) SupportWrapper.convert(this, obj, java.sql.Types.DECIMAL, null);
        }

        return null;
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return getBigDecimal(findColumn(columnLabel));
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        if (currentRow != null && columnIndex > 0 && columnIndex <= currentRow.length) {
            Object obj = currentRow[columnIndex - 1];
            wasNull = (obj == null);
            BigDecimal result = (BigDecimal) SupportWrapper.convert(this, obj, java.sql.Types.DECIMAL, null);

            if (result != null)
                return result.setScale(scale, BigDecimal.ROUND_HALF_UP);
        }

        return null;
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException

    {
        return getBigDecimal(findColumn(columnLabel), scale);
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        Blob blob = getBlob(columnIndex);

        if (blob != null)
            return blob.getBinaryStream();

        return null;
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        return getBinaryStream(findColumn(columnLabel));
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        if (currentRow != null && columnIndex > 0 && columnIndex <= currentRow.length) {
            Object obj = currentRow[columnIndex - 1];
            wasNull = (obj == null);
            return (Blob) SupportWrapper.convert(this, obj, java.sql.Types.BLOB, null);
        }

        return null;
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        return getBlob(findColumn(columnLabel));
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        if (currentRow != null && columnIndex > 0 && columnIndex <= currentRow.length) {
            Object obj = currentRow[columnIndex - 1];
            wasNull = (obj == null);
            return ((Boolean) SupportWrapper.convert(this, obj, SupportWrapper.getBooleanType(), null)).booleanValue();
        }

        return false;
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        return getBoolean(findColumn(columnLabel));
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        if (currentRow != null && columnIndex > 0 && columnIndex <= currentRow.length) {
            Object obj = currentRow[columnIndex - 1];
            wasNull = (obj == null);
            return ((Integer) SupportWrapper.convert(this, obj, java.sql.Types.TINYINT, null)).byteValue();
        }

        return 0;
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        return getByte(findColumn(columnLabel));
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        return getBytes(columnIndex, "UTF-8");
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        return getBytes(findColumn(columnLabel));
    }

    public byte[] getBytes(int columnIndex, String charset) throws SQLException {
        if (currentRow != null && columnIndex > 0 && columnIndex <= currentRow.length) {
            Object obj = currentRow[columnIndex - 1];
            wasNull = (obj == null);
            return (byte[]) SupportWrapper.convert(this, obj, java.sql.Types.BINARY, charset);
        }

        return null;
    }

    public byte[] getBytes(String columnLabel, String charset) throws SQLException {
        return getBytes(findColumn(columnLabel), charset);
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        Clob clob = getClob(columnIndex);

        if (clob != null)
            return clob.getCharacterStream();

        return null;
    }

    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        return getCharacterStream(findColumn(columnLabel));
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        if (currentRow != null && columnIndex > 0 && columnIndex <= currentRow.length) {
            Object obj = currentRow[columnIndex - 1];
            wasNull = (obj == null);
            return (Clob) SupportWrapper.convert(this, obj, java.sql.Types.CLOB, null);
        }

        return null;
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        return getClob(findColumn(columnLabel));
    }

    @Override
    public int getConcurrency() {
        return ResultSet.CONCUR_UPDATABLE;
    }

    @Override
    public String getCursorName() {
        return null;
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        if (currentRow != null && columnIndex > 0 && columnIndex <= currentRow.length) {
            Object obj = currentRow[columnIndex - 1];
            wasNull = (obj == null);
            return (java.sql.Date) SupportWrapper.convert(this, obj, java.sql.Types.DATE, null);
        }

        return null;
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        return getDate(findColumn(columnLabel));
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        java.sql.Date date = getDate(columnIndex);

        if (date != null && cal != null)
            date = new java.sql.Date(SupportWrapper.timeToZone(date, cal));

        return date;
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return getDate(findColumn(columnLabel), cal);
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        if (currentRow != null && columnIndex > 0 && columnIndex <= currentRow.length) {
            Object obj = currentRow[columnIndex - 1];
            wasNull = (obj == null);
            return ((Double) SupportWrapper.convert(this, obj, java.sql.Types.DOUBLE, null)).doubleValue();
        }

        return 0;
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        return getDouble(findColumn(columnLabel));
    }

    @Override
    public int getFetchDirection() {
        return 0;
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        if (currentRow != null && columnIndex > 0 && columnIndex <= currentRow.length) {
            Object obj = currentRow[columnIndex - 1];
            wasNull = (obj == null);
            return ((Float) SupportWrapper.convert(this, obj, java.sql.Types.REAL, null)).floatValue();
        }

        return 0;
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        return getFloat(findColumn(columnLabel));
    }

    @Override
    public int getHoldability() {
        throw new AbstractMethodError();
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        if (currentRow != null && columnIndex > 0 && columnIndex <= currentRow.length) {
            Object obj = currentRow[columnIndex - 1];
            wasNull = (obj == null);
            return ((Integer) SupportWrapper.convert(this, obj, java.sql.Types.INTEGER, null)).intValue();
        }

        return 0;
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        return getInt(findColumn(columnLabel));
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        if (currentRow != null && columnIndex > 0 && columnIndex <= currentRow.length) {
            Object obj = currentRow[columnIndex - 1];
            wasNull = (obj == null);
            return ((Long) SupportWrapper.convert(this, obj, java.sql.Types.BIGINT, null)).longValue();
        }

        return 0;
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        return getLong(findColumn(columnLabel));
    }

    @Override
    public ResultSetMetaData getMetaData() {
        return null;
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) {
        throw new AbstractMethodError();
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) {
        return getNCharacterStream(findColumn(columnLabel));
    }

    @Override
    public NClob getNClob(int columnIndex) {
        throw new AbstractMethodError();
    }

    @Override
    public NClob getNClob(String columnLabel) {
        return getNClob(findColumn(columnLabel));
    }

    @Override
    public String getNString(int columnIndex) {
        throw new AbstractMethodError();
    }

    @Override
    public String getNString(String columnLabel) {
        return getNString(findColumn(columnLabel));
    }

    @Override
    public Object getObject(int columnIndex) {
        if (currentRow != null && columnIndex > 0 && columnIndex <= currentRow.length) {
            Object obj = currentRow[columnIndex - 1];
            wasNull = (obj == null);

            return obj;
        }

        return null;
    }

    @Override
    public Object getObject(String columnLabel) {
        return getObject(findColumn(columnLabel));
    }

    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        throw new SQLException(Messages.get("error.generic.notimp", "ResultSet.getObject(int, Map)"), "HYC00");
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException

    {
        return getObject(findColumn(columnLabel), map);
    }

    @Override
    public <T> T getObject(int i, Class<T> tClass) throws SQLException {
        throw new SQLException(Messages.get("error.generic.notimp", "ResultSet.getObject(int, Class<T>)"), "HYC00");
    }

    @Override
    public <T> T getObject(String s, Class<T> tClass) throws SQLException {
        throw new SQLException(Messages.get("error.generic.notimp", "ResultSet.getObject(String, Class<T>)"), "HYC00");
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        throw new SQLException(Messages.get("error.generic.notimp", "ResultSet.getRef(int)"), "HYC00");
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        return getRef(findColumn(columnLabel));
    }

    @Override
    public int getRow() {
        return currentRowIndex;
    }

    @Override
    public RowId getRowId(int columnIndex) {
        throw new AbstractMethodError();
    }

    @Override
    public RowId getRowId(String columnLabel) {
        return getRowId(findColumn(columnLabel));
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) {
        throw new AbstractMethodError();
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) {
        return getSQLXML(findColumn(columnLabel));
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        if (currentRow != null && columnIndex > 0 && columnIndex <= currentRow.length) {
            Object obj = currentRow[columnIndex - 1];
            wasNull = (obj == null);
            return ((Integer) SupportWrapper.convert(this, obj, java.sql.Types.SMALLINT, null)).shortValue();
        }

        return 0;
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        return getShort(findColumn(columnLabel));
    }

    @Override
    public Statement getStatement() {
        return null;
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        return getString(columnIndex, "UTF-8");
    }

    public String getString(int columnIndex, String charset) throws SQLException {
        if (currentRow != null && columnIndex > 0 && columnIndex <= currentRow.length) {
            Object obj = currentRow[columnIndex - 1];
            wasNull = (obj == null);

            if (obj instanceof String)
                return (String) obj;

            return (String) SupportWrapper.convert(this, obj, java.sql.Types.VARCHAR, charset);
        }

        return null;
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        return getString(findColumn(columnLabel));
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        if (currentRow != null && columnIndex > 0 && columnIndex <= currentRow.length) {
            Object obj = currentRow[columnIndex - 1];
            wasNull = (obj == null);
            return (java.sql.Time) SupportWrapper.convert(this, obj, java.sql.Types.TIME, null);
        }

        return null;
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        return getTime(findColumn(columnLabel));
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        java.sql.Time time = getTime(columnIndex);

        if (time != null && cal != null)
            return new Time(SupportWrapper.timeToZone(time, cal));

        return time;
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return getTime(findColumn(columnLabel), cal);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        if (currentRow != null && columnIndex > 0 && columnIndex <= currentRow.length) {
            Object obj = currentRow[columnIndex - 1];
            wasNull = (obj == null);
            return (Timestamp) SupportWrapper.convert(this, obj, java.sql.Types.TIMESTAMP, null);
        }

        return null;
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return getTimestamp(findColumn(columnLabel));
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        Timestamp timestamp = getTimestamp(columnIndex);

        if (timestamp != null && cal != null)
            timestamp = new Timestamp(SupportWrapper.timeToZone(timestamp, cal));

        return timestamp;
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        return getTimestamp(findColumn(columnLabel), cal);
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException {
        String url = getString(columnIndex);
        try {
            return new java.net.URL(url);
        } catch (MalformedURLException e) {
            throw new SQLException(Messages.get("error.resultset.badurl", url), "22000");
        }
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException {
        return getURL(findColumn(columnLabel));
    }

    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        ClobImpl clob = (ClobImpl) getClob(columnIndex);
        if (clob != null)
            return SupportWrapper.getClobInputStream(clob);

        return null;
    }

    @Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        return getUnicodeStream(findColumn(columnLabel));
    }

    @Override
    public SQLWarning getWarnings() {
        return null;
    }

    @Override
    public void insertRow() {
        throw new AbstractMethodError();
    }

    @Override
    public boolean isAfterLast() {
        return (rows.size() != 0) && (currentRowIndex == rows.size());
    }

    @Override
    public boolean isBeforeFirst() {
        return (rows.size() != 0) && (currentRowIndex == -1);
    }

    @Override
    public boolean isClosed() {
        return true;
    }

    @Override
    public boolean isFirst() {
        return (rows.size() != 0) && (currentRowIndex == 0);
    }

    @Override
    public boolean isLast() {
        return (rows.size() != 0) && (currentRowIndex == rows.size() - 1);
    }

    @Override
    public boolean last() {
        if (rows.size() > 0) {
            currentRow = rows.get(rows.size() - 1);
            currentRowIndex = rows.size() - 1;

            return true;
        }

        return false;
    }

    @Override
    public void moveToCurrentRow() {
        throw new AbstractMethodError();
    }

    @Override
    public void moveToInsertRow() {
        throw new AbstractMethodError();
    }

    @Override
    public boolean next() {
        return relative(1);
    }

    @Override
    public boolean previous() {
        return relative(-1);
    }

    @Override
    public void refreshRow() {
        throw new AbstractMethodError();
    }

    @Override
    public boolean relative(int rowCount) {
        if (rowCount != 0) {
            currentRow = (currentRowIndex + rowCount > -1 && currentRowIndex + rowCount < rows.size() ? rows.get(currentRowIndex + rowCount) : null);
            currentRowIndex = Math.max(Math.min(currentRowIndex + rowCount, rows.size()), -1);
        }

        return (currentRow != null);
    }

    @Override
    public boolean rowDeleted() {
        return false;
    }

    @Override
    public boolean rowInserted() {
        return false;
    }

    @Override
    public boolean rowUpdated() {
        return false;
    }

    @Override
    public void setFetchDirection(int direction) {
        throw new AbstractMethodError();
    }

    @Override
    public void setFetchSize(int rows) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateArray(int columnIndex, Array x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateArray(String columnLabel, Array x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateBlob(int columnIndex, Blob x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateBlob(String columnLabel, Blob x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream,
                           long length) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateByte(int columnIndex, byte x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateByte(String columnLabel, byte x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader,
                                      int length) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader,
                                      long length) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateClob(int columnIndex, Clob x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateClob(String columnLabel, Clob x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateClob(int columnIndex, Reader reader) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateClob(String columnLabel, Reader reader) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateDate(int columnIndex, Date x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateDate(String columnLabel, Date x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateDouble(int columnIndex, double x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateDouble(String columnLabel, double x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateFloat(int columnIndex, float x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateFloat(String columnLabel, float x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateInt(int columnIndex, int x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateInt(String columnLabel, int x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateLong(int columnIndex, long x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateLong(String columnLabel, long x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader,
                                       long length) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateNString(int columnIndex, String nString)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateNString(String columnLabel, String nString)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateNull(int columnIndex) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateNull(String columnLabel) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateObject(int columnIndex, Object x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateObject(String columnLabel, Object x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateRef(int columnIndex, Ref x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateRef(String columnLabel, Ref x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateRow() {
        throw new AbstractMethodError();
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateRowId(String columnLabel, RowId x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject)

    {
        throw new AbstractMethodError();
    }

    @Override
    public void updateShort(int columnIndex, short x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateShort(String columnLabel, short x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateString(int columnIndex, String x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateString(String columnLabel, String x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateTime(int columnIndex, Time x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateTime(String columnLabel, Time x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) {
        throw new AbstractMethodError();
    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x)

    {
        throw new AbstractMethodError();
    }

    @Override
    public boolean wasNull() {
        return wasNull;
    }

}
