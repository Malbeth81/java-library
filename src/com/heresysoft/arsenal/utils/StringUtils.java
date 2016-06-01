package com.heresysoft.arsenal.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class StringUtils {

    public static final String addPadding(String source, char padding, int targetSize, boolean left) {
        if (source != null && source.length() < targetSize) {
            char[] buffer = new char[targetSize - source.length()];
            Arrays.fill(buffer, padding);
            if (left)
                return new String(buffer) + source;
            else
                return source + new String(buffer);
        }
        return source;
    }

    public static final String arrayToString(Object[] array) {
        return arrayToString(array, ",");
    }

    public static final String arrayToString(Object[] array, String delimiter) {
        if (array != null && delimiter != null && array.length > 0 && delimiter.length() > 0) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < array.length; i++) {
                builder.append(array[i]);
                if (i < array.length - 1)
                    builder.append(delimiter);
            }
            return builder.toString();
        }
        return "";
    }

    public static final String decimalFormat(Double value, int decimals) {
        if (value != null)
            return new BigDecimal(value).setScale(decimals, RoundingMode.HALF_EVEN).toString();
        return null;
    }

    public static final byte[] getByteOrderMark(byte[] data) {
        if (data.length >= 3 && data[0] == 0xEF && data[1] == 0xBB && data[2] == 0xBF)
            return copyArray(data, 0, 3); // UTF-8
        else if (data.length >= 2 && data[0] == 0xFF && data[1] == 0xFE)
            return copyArray(data, 0, 2); // UTF-16LE
        else if (data.length >= 2 && data[0] == 0xFE && data[1] == 0xFF)
            return copyArray(data, 0, 2); // UTF-16BE
        return null;
    }

    public static final String getCharacterEncoding(byte[] data) {
        if (data != null) {
            if (data.length >= 3 && data[0] == 0xEF && data[1] == 0xBB && data[2] == 0xBF)
                return "UTF-8";
            else if (data.length >= 2 && data[0] == 0xFF && data[1] == 0xFE)
                return "UTF-16LE";
            else if (data.length >= 2 && data[0] == 0xFE && data[1] == 0xFF)
                return "UTF-16BE";
        }
        return "";
    }

    public static final String getExtension(String filename) {
        if (filename != null) {
            int index = filename.lastIndexOf(".");
            if (index >= 0)
                return filename.substring(index + 1);
        }
        return "";
    }

    public static final boolean isMD5(String value) {
        if (value.length() > 0)
            return Pattern.matches("^[a-fA-F0-9]{32}$", value);
        return false;
    }

    public static final boolean listContains(String list, String value) {
        return listContains(list, value, ",");
    }

    public static final boolean listContains(String list, String value, String separator) {
        if (list != null && value != null && separator != null) {
            String[] values = list.split(separator);
            for (int i = 0; i < values.length; i++)
                if (values[i].trim().equals(value.trim()))
                    return true;
        }
        return false;
    }

    public static final boolean listContainsIgnoreCase(String list, String value) {
        return listContainsIgnoreCase(list, value, ",");
    }

    public static final boolean listContainsIgnoreCase(String list, String value, String separator) {
        if (list != null && value != null && separator != null) {
            String[] values = list.split(separator);
            for (int i = 0; i < values.length; i++)
                if (values[i].trim().equalsIgnoreCase(value.trim()))
                    return true;
        }
        return false;
    }

    public static final String listToString(List<?> list) {
        return listToString(list, ",");
    }

    public static final String listToString(List<?> list, String delimiter) {
        if (list != null && delimiter != null && list.size() > 0 && delimiter.length() > 0) {
            StringBuilder builder = new StringBuilder();
            ListIterator<?> it = list.listIterator();
            while (it.hasNext()) {
                builder.append(it.next());
                if (it.hasNext())
                    builder.append(delimiter);
            }
            return builder.toString();
        }
        return "";
    }

    public static final String lPad(String source, char padding, int targetSize) {
        return addPadding(source, padding, targetSize, true);
    }

    public static final String lTrim(String string) {
        return lTrim(string, ' ');
    }

    public static final String lTrim(String string, char Character) {
        if (string == null || string.length() == 0)
            return string;
        int i = 0;
        while (i <= string.length() - 1 && string.charAt(i) == Character)
            i++;
        return string.substring(i);
    }

    protected static final GregorianCalendar parseDateTime(String date, String time) {
        return parseDateTime(date, time, TimeZone.getTimeZone("UTC"));
    }

    protected static final GregorianCalendar parseDateTime(String date, String time, TimeZone timezone) {
        // Generate a calendar with the specified date and time
        GregorianCalendar calendar = new GregorianCalendar(timezone);
        if (date != null && time != null && date.length() > 0 && time.length() > 0) {
            // Date must be in the format yyyy-mm-dd
            calendar.set(GregorianCalendar.YEAR, Integer.parseInt(date.substring(0, 4).trim()));
            calendar.set(GregorianCalendar.MONTH, Integer.parseInt(date.substring(5, 7).trim()) - 1);
            calendar.set(GregorianCalendar.DAY_OF_MONTH, Integer.parseInt(date.substring(8, 10).trim()));

            // Time must be in the format hh:mm:ss
            calendar.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, 2).trim()));
            calendar.set(GregorianCalendar.MINUTE, Integer.parseInt(time.substring(3, 5).trim()));
            calendar.set(GregorianCalendar.SECOND, Integer.parseInt(time.substring(6, 8).trim()));
            calendar.set(GregorianCalendar.MILLISECOND, 0);
        }
        return calendar;
    }

    public static final Double parseDouble(String string) {
        try {
            // Return and double object representing the specified string value
            return Double.valueOf(string);
        } catch (Exception e) {
        }
        return null;
    }

    public static final Float parseFloat(String string) {
        try {
            // Return and float object representing the specified string value
            return Float.valueOf(string);
        } catch (Exception e) {
        }
        return null;
    }

    public static final Integer parseInteger(String string) {
        try {
            // Return and integer object representing the specified string value
            return Integer.valueOf(string);
        } catch (Exception e) {
        }
        return null;
    }

    public static final List<String> parseList(String list) {
        if (list != null && list.length() > 0)
            return Arrays.asList(list.split(","));
        return new LinkedList<String>();
    }

    public static final String parseString(String value) {
        if (value != null && value.length() > 0) {
            if (value.equalsIgnoreCase("null"))
                return null;
            else if (value.startsWith("\"") && value.endsWith("\""))
                return value.substring(1, value.length() - 2);
        }
        return value;
    }

    public static final List<Integer> parseIntegerList(String list) {
        List<Integer> result = new LinkedList<Integer>();
        if (list != null && list.length() > 0) {
            String[] values = list.split(",");
            for (int i = 0; i < values.length; i++) {
                try {
                    result.add(new Integer(values[i]));
                } catch (Exception e) {
                }
            }
        }
        return result;
    }

    public static final String readFile(String filename) {
        return readFile(filename, null);
    }

    public static final String readFile(String filename, String charSet) {
        if (filename != null && filename.length() > 0) {
            return readFile(new File(filename), charSet);
        }

        return "";
    }

    public static final String readFile(File file) {
        return readFile(file, null);
    }

    public static final String readFile(File file, String charSet) {
        if (file != null) {
            try {
                InputStream reader = new FileInputStream(file);
                byte[] buffer = new byte[(int) file.length()];
                reader.read(buffer);
                reader.close();
                return (charSet != null ? new String(buffer, charSet) : new String(buffer));
            } catch (Exception e) {
            }
        }

        return "";
    }

    public static final String removeExtension(String filename) {
        if (filename != null) {
            int index = filename.lastIndexOf(".");
            if (index >= 0)
                return filename.substring(0, index);
        }
        return filename;
    }

    public static final String rPad(String source, char padding, int targetSize) {
        return addPadding(source, padding, targetSize, false);
    }

    public static final String rTrim(String string) {
        return rTrim(string, ' ');
    }

    public static final String rTrim(String string, char Character) {
        if (string == null || string.length() == 0)
            return string;
        int i = string.length() - 1;
        while (i >= 0 && string.charAt(i) == Character)
            i--;
        return string.substring(0, i + 1);
    }

    public static final String timestampFormat(long timestamp) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").format(new Date(timestamp));
    }

    public static final boolean writeFile(String filename, String content) {
        return writeFile(filename, content, null);
    }

    public static final boolean writeFile(String filename, String content, String charSet) {
        if (filename != null && filename.length() > 0) {
            return writeFile(new File(filename), content, charSet);
        }

        return false;
    }

    public static final boolean writeFile(File file, String content) {
        return writeFile(file, content, null);
    }

    public static final boolean writeFile(File file, String content, String charSet) {
        if (file != null) {
            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                if (content != null && content.length() > 0)
                    outputStream.write(charSet != null ? content.getBytes(charSet) : content.getBytes());
                outputStream.close();

                return true;
            } catch (Exception e) {
            }
        }

        return false;
    }

    private static final byte[] copyArray(byte[] data, int offset, int count) {
        if (data != null) {
            offset = Math.max(Math.min(offset, data.length), 0);
            count = Math.max(Math.min(count, data.length - offset), 0);
            byte[] result = new byte[count];
            for (int i = 0; i < count; i++)
                result[i] = data[offset + i];
            return result;
        }
        return null;
    }

}
