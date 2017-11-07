package malbeth.javautils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/*
 * NE PAS OUBLIER D'EXÉCUTER/MODIFIER LES TESTS UNITAIRES!
 */

public class ByteUtils {
    public static final byte[] appendToArray(byte[] array1, byte[] array2) {
        return appendToArray(array1, array2, 0);
    }

    public static final byte[] appendToArray(byte[] array1, byte[] array2, int length) {
        if (array1 != null && array2 != null) {
            byte[] result = new byte[array1.length + array2.length];
            for (int i = 0; i < array1.length; i++)
                result[i] = array1[i];
            for (int i = 0; i < (length > 0 && length < array2.length ? length : array2.length); i++)
                result[array1.length + i] = array2[i];
            return result;
        }
        return null;
    }

    public static final int byteArrayFindInBetween(byte[] data, byte first, byte last) {
        return byteArrayFindInBetween(data, first, last, 0);
    }

    public static final int byteArrayFindInBetween(byte[] data, byte first, byte last, int index) {
        if (data != null && data.length > 0) {
            index = Math.max(Math.min(index, data.length), 0);
            for (int i = index; i < data.length; i++)
                if (data[i] >= first && data[i] <= last)
                    return i;
        }
        return -1;
    }

    public static final int byteArrayFindOneOf(byte[] data, byte[][] values) {
        return byteArrayFindOneOf(data, values, 0);
    }

    public static final int byteArrayFindOneOf(byte[] data, byte[][] values, int index) {
        if (data != null && values != null && data.length > 0 && values.length > 0) {
            index = Math.max(Math.min(index, data.length), 0);
            for (int i = index; i < data.length; i++) {
                boolean match = false;
                for (int j = 0; j < values.length; j++) {
                    boolean partMatch = true;
                    for (int k = 0; k < Math.min(values[j].length, data.length - i); k++)
                        partMatch = partMatch && (data[i + k] == values[j][k]);
                    match = match || partMatch;
                }
                if (match)
                    return i;
            }
        }
        return -1;
    }

    public static final int byteArrayIndexOf(byte[] data, byte[] pattern) {
        return byteArrayIndexOf(data, pattern, 0);
    }

    public static final int byteArrayIndexOf(byte[] data, byte[] pattern, int index) {
        if (data != null && pattern != null && data.length > 0 && pattern.length > 0) {
            index = Math.max(Math.min(index, data.length), 0);
            for (int i = index; i <= data.length - pattern.length; i++) {
                boolean match = true;
                for (int j = 0; j < pattern.length; j++)
                    match = match && (data[i + j] == pattern[j]);
                if (match)
                    return i;
            }
        }
        return -1;
    }

    public static final boolean byteArrayIsASCII(byte[] data) {
        if (data != null)
            return byteArrayIsASCII(data, data.length);
        return false;
    }

    public static final boolean byteArrayIsASCII(byte[] data, int count) {
        if (data != null && data.length > 0) {
            count = Math.max(Math.min(count, data.length), 0);
            for (int i = 0; i < count; i++)
                if ((data[i] < 0x20 || data[i] > 0x7E) && data[i] != 0x09 && data[i] != 0x0A && data[i] != 0x0D)
                    return false;
            return true;
        }
        return false;
    }

    public static final String byteArrayToBase64(byte[] data) {
        return javax.xml.bind.DatatypeConverter.printBase64Binary(data);
    }

    public static final String byteArrayToHexString(byte[] data) {
        if (data != null)
            return byteArrayToHexString(data, 0, data.length);
        return null;
    }

    public static final String byteArrayToHexString(byte[] data, int offset) {
        if (data != null)
            return byteArrayToHexString(data, offset, data.length - offset);
        return null;
    }

    public static final String byteArrayToHexString(byte[] data, int offset, int count) {
        String values = "0123456789ABCDEF";
        if (data != null) {
            offset = Math.max(Math.min(offset, data.length), 0);
            count = Math.max(Math.min(count, data.length - offset), 0);
            StringBuilder hex = new StringBuilder(count * 2);
            for (int i = 0; i < count; i++)
                hex.append(values.charAt((data[offset + i] & 0xF0) >> 4)).append(values.charAt((data[offset + i] & 0x0F)));
            return hex.toString();
        }
        return null;
    }

    public static final int byteArrayToInteger(byte[] data) {
        if (data != null)
            return byteArrayToInteger(data, 0, data.length);
        return 0;
    }

    public static final int byteArrayToInteger(byte[] data, int offset) {
        if (data != null)
            return byteArrayToInteger(data, offset, data.length - offset);
        return 0;
    }

    public static final int byteArrayToInteger(byte[] data, int offset, int count) {
        if (data != null && data.length > 0) {
            offset = Math.max(Math.min(offset, data.length), 0);
            count = Math.max(Math.min(count, Math.min(4, data.length - offset)), 0);
            int value = 0;
            for (int i = 0; i < count; i++)
                value += ((int) data[offset + i] & 0xFF) << (count - (i + 1)) * 8;
            return value;
        }
        return 0;
    }

    public static final long byteArrayToLong(byte[] data) {
        if (data != null)
            return byteArrayToLong(data, 0, data.length);
        return 0;
    }

    public static final long byteArrayToLong(byte[] data, int offset) {
        if (data != null)
            return byteArrayToLong(data, offset, data.length - offset);
        return 0;
    }

    public static final long byteArrayToLong(byte[] data, int offset, int count) {
        if (data != null && data.length > 0) {
            offset = Math.max(Math.min(offset, data.length), 0);
            count = Math.max(Math.min(count, Math.min(8, data.length - offset)), 0);
            long value = 0;
            for (int i = 0; i < count; i++)
                value += ((long) data[offset + i] & 0xFF) << (count - (i + 1)) * 8;
            return value;
        }
        return 0;
    }

    public static final byte[] byteArrayRTrim(byte[] array) {
        return byteArrayRTrim(array, (byte) 0);
    }

    public static final byte[] byteArrayRTrim(byte[] array, byte value) {
        if (array == null || array.length == 0)
            return array;
        int i = array.length - 1;
        while (i >= 0 && array[i] == value)
            i--;
        return copyArray(array, 0, i + 1);
    }

    public static final byte[] copyArray(byte[] data) {
        if (data != null)
            return copyArray(data, 0, data.length);
        return null;
    }

    public static final byte[] copyArray(byte[] data, int offset) {
        if (data != null)
            return copyArray(data, offset, data.length - offset);
        return null;
    }

    public static final byte[] copyArray(byte[] data, int offset, int count) {
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

    public static final String dataToString(byte[] data) {
        try {
            if (ByteUtils.byteArrayIsASCII(data))
                return new String(data, "US-ASCII");
            else
                return ByteUtils.byteArrayToHexString(data);
        } catch (java.io.UnsupportedEncodingException e) {
        }
        return null;
    }

    public static final int getBit(byte data, int bit) {
        int mask = (int) Math.pow(2, (bit % 8));
        return (data & mask);
    }

    public static final int getBit(byte[] data, long bit) {
        if (data != null && data.length > 0) {
            int index = data.length - 1 - (int) Math.floor(bit / 8);
            if (index >= 0 && index < data.length) {
                long mask = (long) Math.pow(2, (bit % 8));
                return ((data[index] & mask) != 0 ? 1 : 0);
            }
        }
        return 0;
    }

    public static final int getBits(byte data, int offset, int count) {
        int mask = (int) Math.pow(2, (offset + count % 8)) - (int) Math.pow(2, (offset % 8));
        return (data & mask);
    }

    public static final byte[] hexStringToByteArray(String data) {
        if (data != null)
            return hexStringToByteArray(data, 0, data.length());
        return null;
    }

    public static final byte[] hexStringToByteArray(String data, int offset) {
        if (data != null)
            return hexStringToByteArray(data, offset, data.length() - offset);
        return null;
    }

    public static final byte[] hexStringToByteArray(String data, int offset, int count) {
        if (data != null) {
            count = Math.max(Math.min(count, data.length() - offset), 0);
            byte[] result = new byte[(int) Math.ceil(count / 2.0)];
            int i = 0;
            int j = 0;
            while (i < count) {
                if (i == 0 && count % 2 == 1) {
                    result[j] = (byte) Character.digit(data.charAt(offset + i), 16);
                    i += 1;
                } else {
                    result[j] = (byte) ((Character.digit(data.charAt(offset + i), 16) << 4) + Character.digit(data.charAt(offset + i + 1), 16));
                    i += 2;
                }
                j++;
            }
            return result;
        }
        return null;
    }

    public static final byte[] integerToByteArray(int value) {
        return integerToByteArray(value, 4);
    }

    public static final byte[] integerToByteArray(int value, int size) {
        size = Math.max(Math.min(size, 4), 0);
        byte[] result = new byte[size];
        for (int i = 0; i < size; i++)
            result[i] = (byte) ((value >> (size - (i + 1)) * 8) & 0x000000FF);
        return result;
    }

    public static final byte[] longToByteArray(long value) {
        return longToByteArray(value, 8);
    }

    public static final byte[] longToByteArray(long value, int size) {
        size = Math.max(Math.min(size, 8), 0);
        byte[] result = new byte[size];
        for (int i = 0; i < size; i++)
            result[i] = (byte) ((value >> (size - (i + 1)) * 8) & 0x00000000000000FF);
        return result;
    }

    public static final byte[] readBytesFromFile(File file) {
        byte[] result = null;

        if (file != null) {
            try {
                long length = Math.min(file.length(), Integer.MAX_VALUE);
                FileInputStream fileStream = new FileInputStream(file);
                try {
                    result = new byte[(int) length];
                    int offset = 0;
                    int numRead = 0;
                    while (offset < result.length && (numRead = fileStream.read(result, offset, result.length - offset)) >= 0)
                        offset += numRead;
                } finally {
                    fileStream.close();
                }
            } catch (Exception e) {
            }
        }

        return result;
    }

    public static final byte[] reverseByteOrder(byte[] data) {
        if (data != null && data.length > 0) {
            byte[] result = new byte[data.length];
            for (int i = 0; i < data.length; i++)
                result[i] = data[(data.length - 1) - i];
            return result;
        }
        return data;
    }

    public static final String reverseByteOrder(String data) {
        if (data != null && data.length() > 0) {
            StringBuffer result = new StringBuffer(data.length());
            for (int i = data.length(); i > 0; i -= 2)
                result.append(data.substring(i - 2, i));
            return result.toString();
        }
        return data;
    }

    public static final boolean setBit(byte[] data, long bit, int value) {
        if (data != null && data.length > 0 && bit >= 0 && bit < data.length * 8) {
            int index = data.length - 1 - (int) Math.floor(bit / 8);
            long mask = (long) Math.pow(2, (bit % 8));
            data[index] = (byte) (value == 0 ? data[index] & ~mask : data[index] | mask);
            return true;
        }
        return false;
    }

    public static final byte[][] splitArray(byte[] array, byte[] separator) {
        if (array != null && separator != null) {
            byte[][] result = new byte[][]{};
            int start = 0;
            while (start < array.length) {
                byte[][] newResult = new byte[result.length + 1][];
                for (int i = 0; i < result.length; i++)
                    newResult[i] = result[i];
                result = newResult;
                int end = byteArrayIndexOf(array, separator, start);
                if (end < 0)
                    end = array.length;
                result[result.length - 1] = copyArray(array, start, end - start);
                start = end + separator.length;
            }
            return result;
        }
        return null;
    }

    public static final byte[][] splitArray(byte[] array, int size) {
        if (array != null && size > 0) {
            byte[][] result = new byte[(int) Math.ceil((double) array.length / (double) size)][];
            for (int i = 0; i < result.length; i++)
                result[i] = copyArray(array, i * size, size);
            return result;
        }
        return new byte[][]{};
    }

    public static final boolean writeBytesToFile(File file, byte[] bytes) {
        boolean result = false;

        if (file != null && bytes != null) {
            try {
                FileOutputStream fileStream = new FileOutputStream(file);
                try {
                    fileStream.write(bytes);
                    result = true;
                } finally {
                    fileStream.close();
                }
            } catch (Exception e) {
            }
        }

        return result;
    }

}
