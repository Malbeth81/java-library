package malbeth.javautils;

import org.junit.Test;

import static org.junit.Assert.*;

public class ByteUtilsTest
{

    @Test
    public final void testAppendToArray()
    {
        byte[] a = {0x54, 0x65};
        byte[] b = {0x73, 0x74};
        assertArrayEquals(null, ByteUtils.appendToArray(null, null));
        assertArrayEquals(null, ByteUtils.appendToArray(a, null));
        assertArrayEquals(null, ByteUtils.appendToArray(null, b));
        assertArrayEquals(new byte[0], ByteUtils.appendToArray(new byte[0], new byte[0]));
        assertArrayEquals(a, ByteUtils.appendToArray(a, new byte[0]));
        assertArrayEquals(b, ByteUtils.appendToArray(new byte[0], b));
        assertArrayEquals(new byte[]{0x54, 0x65, 0x73, 0x74}, ByteUtils.appendToArray(a, b));
    }


    @Test
    public final void testByteArrayFindOneOf()
    {
        byte[] a = {0x54, 0x65, 0x73, 0x74, 0x54, 0x65, 0x73, 0x74};
        assertEquals(-1, ByteUtils.byteArrayFindOneOf(null, null));
        assertEquals(-1, ByteUtils.byteArrayFindOneOf(a, null));
        assertEquals(-1, ByteUtils.byteArrayFindOneOf(null, new byte[][]{a}));
        assertEquals(-1, ByteUtils.byteArrayFindOneOf(new byte[0], new byte[0][0]));
        assertEquals(-1, ByteUtils.byteArrayFindOneOf(a, new byte[0][0]));
        assertEquals(-1, ByteUtils.byteArrayFindOneOf(new byte[0], new byte[][]{a}));
        assertEquals(0, ByteUtils.byteArrayFindOneOf(a, new byte[][]{new byte[]{0x54}, new byte[]{0x73}}));
        assertEquals(1, ByteUtils.byteArrayFindOneOf(a, new byte[][]{new byte[]{0x65, 0x73}}));
        assertEquals(3, ByteUtils.byteArrayFindOneOf(a, new byte[][]{new byte[]{0x74, 0x54}}));
        assertEquals(2, ByteUtils.byteArrayFindOneOf(a, new byte[][]{new byte[]{0x54}, new byte[]{0x73}}, 1));
        assertEquals(3, ByteUtils.byteArrayFindOneOf(a, new byte[][]{new byte[]{0x65}, new byte[]{0x74}}, 2));
        assertEquals(5, ByteUtils.byteArrayFindOneOf(a, new byte[][]{new byte[]{0x73}, new byte[]{0x65}}, 3));
    }

    @Test
    public final void testByteArrayIndexOf()
    {
        byte[] a = {0x54, 0x65, 0x73, 0x74, 0x54, 0x65, 0x73, 0x74};
        assertEquals(-1, ByteUtils.byteArrayIndexOf(null, null));
        assertEquals(-1, ByteUtils.byteArrayIndexOf(a, null));
        assertEquals(-1, ByteUtils.byteArrayIndexOf(null, a));
        assertEquals(-1, ByteUtils.byteArrayIndexOf(new byte[0], new byte[0]));
        assertEquals(-1, ByteUtils.byteArrayIndexOf(a, new byte[0]));
        assertEquals(-1, ByteUtils.byteArrayIndexOf(new byte[0], a));
        assertEquals(0, ByteUtils.byteArrayIndexOf(a, new byte[]{0x54, 0x65}));
        assertEquals(1, ByteUtils.byteArrayIndexOf(a, new byte[]{0x65, 0x73}));
        assertEquals(2, ByteUtils.byteArrayIndexOf(a, new byte[]{0x73, 0x74}));
        assertEquals(4, ByteUtils.byteArrayIndexOf(a, new byte[]{0x54, 0x65}, 4));
        assertEquals(5, ByteUtils.byteArrayIndexOf(a, new byte[]{0x65, 0x73}, 4));
        assertEquals(6, ByteUtils.byteArrayIndexOf(a, new byte[]{0x73, 0x74}, 4));
    }

    @Test
    public final void testByteArrayIsASCII()
    {
        byte[] a = {0x54, 0x65, 0x73, 0x74, 0x0D, 0x0A};
        byte[] b = {0x54, 0x65, 0x73, 0x74};
        byte[] c = {0x54, 0x65, 0x73, 0x74, 0x08};
        assertFalse(ByteUtils.byteArrayIsASCII(null));
        assertFalse(ByteUtils.byteArrayIsASCII(new byte[0]));
        assertTrue(ByteUtils.byteArrayIsASCII(a));
        assertTrue(ByteUtils.byteArrayIsASCII(a, 4));
        assertTrue(ByteUtils.byteArrayIsASCII(b));
        assertFalse(ByteUtils.byteArrayIsASCII(c));
    }

    @Test
    public final void testByteArrayToHexString()
    {
        byte[] a = {0x54, 0x65, 0x73, 0x74};
        assertEquals(null, ByteUtils.byteArrayToHexString(null));
        assertEquals("", ByteUtils.byteArrayToHexString(new byte[0]));
        assertEquals("54657374", ByteUtils.byteArrayToHexString(a));
        assertEquals("657374", ByteUtils.byteArrayToHexString(a, 1));
        assertEquals("6573", ByteUtils.byteArrayToHexString(a, 1, 2));
        assertEquals("7374", ByteUtils.byteArrayToHexString(a, 2, 2));
        assertEquals("7374", ByteUtils.byteArrayToHexString(a, 2, 5));

        byte[] b = {-0x01, -0x01, -0x01, -0x01};
        assertEquals("FFFFFFFF", ByteUtils.byteArrayToHexString(b));
    }

    @Test
    public final void testByteArrayToInteger()
    {
        byte[] a = {0x54, 0x65, 0x73, 0x74};
        assertEquals(0, ByteUtils.byteArrayToInteger(null));
        assertEquals(0, ByteUtils.byteArrayToInteger(new byte[0]));
        assertEquals(1415934836, ByteUtils.byteArrayToInteger(a));
        assertEquals(6648692, ByteUtils.byteArrayToInteger(a, 1));
        assertEquals(25971, ByteUtils.byteArrayToInteger(a, 1, 2));
        assertEquals(29556, ByteUtils.byteArrayToInteger(a, 2, 2));
        assertEquals(29556, ByteUtils.byteArrayToInteger(a, 2, 5));
        byte[] b = {(byte) 0x80, 0x00, 0x28, 0x36};
        assertEquals(-2147473354, ByteUtils.byteArrayToInteger(b));
    }

    @Test
    public final void testByteArrayToLong()
    {
        byte[] a = {0x54, 0x65, 0x73, 0x74, 0x54, 0x65};
        assertEquals(0, ByteUtils.byteArrayToLong(null));
        assertEquals(0, ByteUtils.byteArrayToLong(new byte[0]));
        assertEquals(92794705433701L, ByteUtils.byteArrayToLong(a));
        assertEquals(1937003621L, ByteUtils.byteArrayToLong(a, 2));
        assertEquals(7566420L, ByteUtils.byteArrayToLong(a, 2, 3));
        assertEquals(7623781L, ByteUtils.byteArrayToLong(a, 3, 3));
        assertEquals(7623781L, ByteUtils.byteArrayToLong(a, 3, 5));
    }

    @Test
    public final void testByteArrayRTrim()
    {
        byte[] a = {0x54, 0x65, 0x73, 0x74, 0x00, 0x00, 0x00};
        byte[] b = {0x54, 0x65, 0x73, 0x74, 0x0D, 0x0D, 0x0D};
        assertArrayEquals(null, ByteUtils.byteArrayRTrim(null));
        assertArrayEquals(new byte[0], ByteUtils.byteArrayRTrim(new byte[0]));
        assertArrayEquals(new byte[]{0x54, 0x65, 0x73, 0x74}, ByteUtils.byteArrayRTrim(a));
        assertArrayEquals(new byte[]{0x54, 0x65, 0x73, 0x74}, ByteUtils.byteArrayRTrim(b, (byte) 0x0D));
    }


    @Test
    public final void testCopyArray()
    {
        byte[] a = {0x54, 0x65, 0x73, 0x74};
        assertArrayEquals(null, ByteUtils.copyArray(null));
        assertArrayEquals(new byte[0], ByteUtils.copyArray(new byte[0]));
        assertArrayEquals(a, ByteUtils.copyArray(a));
        assertArrayEquals(new byte[]{0x73, 0x74}, ByteUtils.copyArray(a, 2));
        assertArrayEquals(new byte[]{0x65, 0x73}, ByteUtils.copyArray(a, 1, 2));
        assertArrayEquals(new byte[]{}, ByteUtils.copyArray(a, 4));
    }

    @Test
    public final void testDataToString()
    {
        assertEquals(null, ByteUtils.dataToString(null));
        assertEquals("", ByteUtils.dataToString(new byte[0]));
        assertEquals("Test", ByteUtils.dataToString(new byte[]{0x54, 0x65, 0x73, 0x74}));
        assertEquals("Test\r\n", ByteUtils.dataToString(new byte[]{0x54, 0x65, 0x73, 0x74, 0x0D, 0x0A}));
        assertEquals("Test\n", ByteUtils.dataToString(new byte[]{0x54, 0x65, 0x73, 0x74, 0x0A}));
        assertEquals("5465737408", ByteUtils.dataToString(new byte[]{0x54, 0x65, 0x73, 0x74, 0x08}));
    }

    @Test
    public final void testGetBit()
    {
        byte[] a = {0x44}; // 01000100
        byte[] b = {0x20, 0x00}; // 00100000 00000000
        byte[] c = {0x00, 0x44, 0x00}; //  00000000 01000100 00000000
        assertEquals(0, ByteUtils.getBit(null, 2));
        assertEquals(0, ByteUtils.getBit(new byte[0], 2));
        assertEquals(0, ByteUtils.getBit(a, -1));
        assertEquals(1, ByteUtils.getBit(a, 2));
        assertEquals(0, ByteUtils.getBit(a, 4));
        assertEquals(1, ByteUtils.getBit(a, 6));
        assertEquals(0, ByteUtils.getBit(a, 9));
        assertEquals(0, ByteUtils.getBit(b, -1));
        assertEquals(1, ByteUtils.getBit(b, 13));
        assertEquals(0, ByteUtils.getBit(b, 14));
        assertEquals(0, ByteUtils.getBit(b, 16));
        assertEquals(0, ByteUtils.getBit(c, -1));
        assertEquals(1, ByteUtils.getBit(c, 10));
        assertEquals(0, ByteUtils.getBit(c, 12));
        assertEquals(1, ByteUtils.getBit(c, 14));
        assertEquals(0, ByteUtils.getBit(c, 24));
    }

    @Test
    public final void testHexStringToByteArray()
    {
        assertArrayEquals(null, ByteUtils.hexStringToByteArray(null));
        assertArrayEquals(new byte[0], ByteUtils.hexStringToByteArray(""));
        assertArrayEquals(new byte[]{0x02}, ByteUtils.hexStringToByteArray("2"));
        assertArrayEquals(new byte[]{0x02, 0x43}, ByteUtils.hexStringToByteArray("243"));

        String a = "54657374";
        assertArrayEquals(new byte[]{0x54, 0x65, 0x73, 0x74}, ByteUtils.hexStringToByteArray(a));
        assertArrayEquals(new byte[]{0x65, 0x73, 0x74}, ByteUtils.hexStringToByteArray(a, 2));
        assertArrayEquals(new byte[]{0x65, 0x73}, ByteUtils.hexStringToByteArray(a, 2, 4));
        assertArrayEquals(new byte[]{0x73, 0x74}, ByteUtils.hexStringToByteArray(a, 4, 4));
        assertArrayEquals(new byte[]{0x73, 0x74}, ByteUtils.hexStringToByteArray(a, 4, 10));

        String b = "FFFFFFFF";
        assertArrayEquals(new byte[]{-0x01, -0x01, -0x01, -0x01}, ByteUtils.hexStringToByteArray(b));
    }

    @Test
    public final void testIntegerToByteArray()
    {
        assertArrayEquals(new byte[]{0x00, 0x00, 0x00, 0x00}, ByteUtils.integerToByteArray(0));
        assertArrayEquals(new byte[]{-0x01, -0x01, -0x01, -0x01}, ByteUtils.integerToByteArray(-1));
        assertArrayEquals(new byte[]{0x00, 0x00}, ByteUtils.integerToByteArray(0, 2));
        assertArrayEquals(new byte[]{0x54, 0x65, 0x73, 0x74}, ByteUtils.integerToByteArray(1415934836));
        assertArrayEquals(new byte[]{0x65, 0x73, 0x74}, ByteUtils.integerToByteArray(1415934836, 3));
        assertArrayEquals(new byte[]{0x73, 0x74}, ByteUtils.integerToByteArray(1415934836, 2));
        assertArrayEquals(new byte[]{0x74}, ByteUtils.integerToByteArray(1415934836, 1));
    }

    @Test
    public final void testLongToByteArray()
    {
        assertArrayEquals(new byte[]{0x00, 0x00}, ByteUtils.longToByteArray(0, 2));
        assertArrayEquals(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}, ByteUtils.longToByteArray(0));
        assertArrayEquals(new byte[]{0x00, 0x00, 0x00, 0x00, 0x54, 0x65, 0x73, 0x74}, ByteUtils.longToByteArray(1415934836));
        assertArrayEquals(new byte[]{0x28, 0x5E, 0x48, 0x51, 0x6C, 0x33, 0x67, 0x31}, ByteUtils.longToByteArray(2908841923872778033L));
        assertArrayEquals(new byte[]{0x6C, 0x33, 0x67, 0x31}, ByteUtils.longToByteArray(2908841923872778033L, 4));
    }

    @Test
    public final void testReverseByteOrder()
    {
        byte[] a = {0x54, 0x65, 0x73, 0x74, 0x0D, 0x0A};
        byte[] b = {0x54, 0x65, 0x73, 0x74};
        assertArrayEquals(null, ByteUtils.reverseByteOrder((byte[]) null));
        assertArrayEquals(new byte[0], ByteUtils.reverseByteOrder(new byte[0]));
        assertArrayEquals(new byte[]{0x0A, 0x0D, 0x74, 0x73, 0x65, 0x54}, ByteUtils.reverseByteOrder(a));
        assertArrayEquals(new byte[]{0x74, 0x73, 0x65, 0x54}, ByteUtils.reverseByteOrder(b));

        assertEquals(null, ByteUtils.reverseByteOrder((String) null));
        assertEquals("", ByteUtils.reverseByteOrder(""));
        assertEquals("0A0D74736554", ByteUtils.reverseByteOrder(ByteUtils.byteArrayToHexString(a)));
        assertEquals("74736554", ByteUtils.reverseByteOrder(ByteUtils.byteArrayToHexString(b)));
    }

    @Test
    public final void testSetBit()
    {
        byte[] a = {0x10}; // 00010000
        byte[] b = {0x00, 0x10, 0x00}; // 00010000
        assertFalse(ByteUtils.setBit(a, -1, 1));
        assertTrue(ByteUtils.setBit(a, 2, 1));
        assertTrue(ByteUtils.setBit(a, 4, 0));
        assertTrue(ByteUtils.setBit(a, 6, 1));
        assertFalse(ByteUtils.setBit(a, 9, 1));
        assertTrue(ByteUtils.setBit(a, 0, 1));
        assertArrayEquals(new byte[]{0x45}, a); // 01000101
        assertFalse(ByteUtils.setBit(b, -1, 1));
        assertTrue(ByteUtils.setBit(b, 10, 1));
        assertTrue(ByteUtils.setBit(b, 12, 0));
        assertTrue(ByteUtils.setBit(b, 14, 1));
        assertFalse(ByteUtils.setBit(b, 24, 1));
        assertArrayEquals(new byte[]{0x00, 0x44, 0x00}, b); // 01000100
    }

    @Test
    public final void testSplitArray()
    {
        byte[] a = {0x54, 0x65, 0x73, 0x74, 0x0D, 0x0A, 0x54, 0x65, 0x73, 0x74, 0x0D, 0x0A};
        byte[] b = {0x0D, 0x0A, 0x54, 0x65, 0x73, 0x74, 0x0D, 0x0A, 0x54, 0x65, 0x73, 0x74};
        byte[] c = {0x0D, 0x0A};
        assertArrayEquals(null, ByteUtils.splitArray(null, null));
        assertArrayEquals(null, ByteUtils.splitArray(a, null));
        assertArrayEquals(null, ByteUtils.splitArray(null, c));
        assertArrayEquals(new byte[0][0], ByteUtils.splitArray(new byte[0], new byte[0]));
        assertArrayEquals(new byte[0][0], ByteUtils.splitArray(new byte[0], c));
        assertArrayEquals(new byte[][]{a}, ByteUtils.splitArray(a, new byte[0]));
        assertArrayEquals(new byte[][]{{0x54, 0x65, 0x73, 0x74}, {0x54, 0x65, 0x73, 0x74}}, ByteUtils.splitArray(a, c));
        assertArrayEquals(new byte[][]{{}, {0x54, 0x65, 0x73, 0x74}, {0x54, 0x65, 0x73, 0x74}}, ByteUtils.splitArray(b, c));
    }

}
