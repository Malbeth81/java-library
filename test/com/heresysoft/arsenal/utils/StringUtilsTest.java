package com.heresysoft.arsenal.utils;

import com.heresysoft.arsenal.utils.StringUtils;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class StringUtilsTest
{

    @Test
    public final void testAddPadding()
    {
        String a = "Test";
        assertEquals(null, StringUtils.addPadding(null, ' ', 4, true));
        assertEquals("    ", StringUtils.addPadding("", ' ', 4, true));
        assertEquals("Test", StringUtils.addPadding(a, ' ', 4, true));
        assertEquals("    Test", StringUtils.addPadding(a, ' ', 8, true));
        assertEquals("Test    ", StringUtils.addPadding(a, ' ', 8, false));
    }

    @Test
    public final void testArrayToString()
    {
        String[] a = {"this", "is", "a", "test"};
        assertEquals("", StringUtils.arrayToString((String[]) null, null));
        assertEquals("", StringUtils.arrayToString(a, null));
        assertEquals("", StringUtils.arrayToString((String[]) null, ","));
        assertEquals("this,is,a,test", StringUtils.arrayToString(a, ","));
        assertEquals("this;is;a;test", StringUtils.arrayToString(a, ";"));
        assertEquals("this,is,a,test", StringUtils.arrayToString(a));

        assertEquals("", StringUtils.listToString((LinkedList<String>) null, null));
        assertEquals("", StringUtils.listToString((LinkedList<String>) null, ","));
        LinkedList<String> c = new LinkedList<String>();
        c.add("this");
        c.add("is");
        c.add("a");
        c.add("test");
        assertEquals("", StringUtils.listToString(c, null));
        assertEquals("this,is,a,test", StringUtils.listToString(c, ","));
        assertEquals("this;is;a;test", StringUtils.listToString(c, ";"));
        assertEquals("this,is,a,test", StringUtils.listToString(c));
    }

    @Test
    public final void testDecimalFormat()
    {
        assertNotNull(StringUtils.decimalFormat(0.0, 6));
        assertEquals("-79.952300", StringUtils.decimalFormat(-79.952300, 6));
        assertEquals("-79.009523", StringUtils.decimalFormat(-79.009523, 6));
        assertEquals("-79.000000", StringUtils.decimalFormat(-79.0, 6));
        assertEquals("45.000001", StringUtils.decimalFormat(45.000001, 6));
        assertEquals("45.100000", StringUtils.decimalFormat(45.100000, 6));
        assertEquals("0.000000", StringUtils.decimalFormat(0.0, 6));
    }

    @Test
    public final void testIsMD5()
    {
        assertFalse(StringUtils.isMD5(""));
        assertFalse(StringUtils.isMD5("hello world!"));
        assertTrue(StringUtils.isMD5("FC5E038D38A57032085441E7FE7010B0"));
        assertFalse(StringUtils.isMD5("FC5E038D38A5G032085441E7FE7010B0"));
    }

    @Test
    public final void testLPad()
    {
        String a = "Test";
        assertEquals(null, StringUtils.lPad(null, ' ', 4));
        assertEquals("    ", StringUtils.lPad("", ' ', 4));
        assertEquals("Test", StringUtils.lPad(a, ' ', 4));
        assertEquals("    Test", StringUtils.lPad(a, ' ', 8));
    }

    @Test
    public final void testLTrim()
    {
        assertEquals(null, StringUtils.lTrim(null));
        assertEquals("", StringUtils.lTrim(""));
        assertEquals("Test", StringUtils.lTrim("Test"));
        assertEquals("Test", StringUtils.lTrim("    Test"));
        assertEquals("Test", StringUtils.lTrim("____Test", '_'));
    }

    @Test
    public final void testRPad()
    {
        String a = "Test";
        assertEquals(null, StringUtils.rPad(null, ' ', 4));
        assertEquals("    ", StringUtils.rPad("", ' ', 4));
        assertEquals("Test", StringUtils.rPad(a, ' ', 4));
        assertEquals("Test    ", StringUtils.rPad(a, ' ', 8));
    }

    @Test
    public final void testRTrim()
    {
        assertEquals(null, StringUtils.rTrim(null));
        assertEquals("", StringUtils.rTrim(""));
        assertEquals("Test", StringUtils.rTrim("Test"));
        assertEquals("Test", StringUtils.rTrim("Test    "));
        assertEquals("Test", StringUtils.rTrim("Test____", '_'));
    }

}
