package com.heresysoft.arsenal.utils;

import com.heresysoft.arsenal.utils.Blacklist;
import com.heresysoft.arsenal.utils.Blacklist.BlacklistAddress;
import com.heresysoft.arsenal.utils.Blacklist.BlacklistRule;
import org.junit.Test;

import static org.junit.Assert.*;

public class BlacklistTest
{
    @Test
    public void testBlacklistAddress()
    {
        BlacklistAddress a = BlacklistAddress.parse("192.168.1.96");
        assertEquals("192.168.1.96", a.toString());
        assertEquals(-1, a.compareTo(BlacklistAddress.parse("192.168.1.97")));
        assertEquals(-1, a.compareTo(BlacklistAddress.parse("192.168.1.138")));
        assertEquals(0, a.compareTo(BlacklistAddress.parse("192.168.1.96")));
        assertEquals(1, a.compareTo(BlacklistAddress.parse("192.168.1.95")));
        assertEquals(1, a.compareTo(BlacklistAddress.parse("192.168.1.38")));
    }

    @Test
    public void testBlacklistRule()
    {
        BlacklistRule a = new BlacklistRule();
        a.fromAddress = BlacklistAddress.parse("192.168.1.96");
        a.toAddress = BlacklistAddress.parse("192.168.1.96");
        a.source = "192.168.1.96";
        assertEquals(true, a.matches("192.168.1.96"));
        assertEquals(false, a.matches("192.168.1.95"));
        assertEquals(false, a.matches("192.168.1.38"));
        assertEquals(false, a.matches("192.168.1.97"));
        assertEquals(false, a.matches("192.168.1.138"));

        BlacklistRule b = new BlacklistRule();
        b.fromAddress = BlacklistAddress.parse("192.168.1.90");
        b.toAddress = BlacklistAddress.parse("192.168.1.100");
        b.source = "192.168.1.90-192.168.1.100";
        assertEquals(true, b.matches("192.168.1.90"));
        assertEquals(true, b.matches("192.168.1.91"));
        assertEquals(true, b.matches("192.168.1.95"));
        assertEquals(true, b.matches("192.168.1.99"));
        assertEquals(true, b.matches("192.168.1.100"));
        assertEquals(false, b.matches("192.168.1.38"));
        assertEquals(false, b.matches("192.168.1.138"));

        BlacklistRule c = new BlacklistRule();
        c.fromAddress = BlacklistAddress.parse("192.168.0.90");
        c.toAddress = BlacklistAddress.parse("192.168.1.90");
        c.source = "192.168.1.90-192.168.1.100";
        assertEquals(true, c.matches("192.168.0.90"));
        assertEquals(true, c.matches("192.168.0.91"));
        assertEquals(true, c.matches("192.168.0.255"));
        assertEquals(true, c.matches("192.168.1.0"));
        assertEquals(true, c.matches("192.168.1.1"));
        assertEquals(true, c.matches("192.168.1.90"));
        assertEquals(false, c.matches("192.168.0.38"));
        assertEquals(false, c.matches("192.168.1.138"));
    }

    @Test
    public void testBlacklistHandler()
    {
        Blacklist a = new Blacklist();
        assertTrue(a.add("192.168.1.96"));
        assertTrue(a.contains("192.168.1.96"));
        assertTrue(a.contains("192.168.1.96"));
        assertFalse(a.contains("192.168.1.95"));
        assertFalse(a.contains("192.168.1.97"));
        assertFalse(a.contains("192.168.0.96"));
        assertFalse(a.contains("192.168.2.96"));
        assertTrue(a.remove("192.168.1.96"));
        assertFalse(a.contains("192.168.1.96"));
        assertFalse(a.remove("192.168.1.96"));
        assertTrue(a.add("192.168.1.90-192.168.1.100"));
        assertTrue(a.contains("192.168.1.90"));
        assertTrue(a.contains("192.168.1.96"));
        assertTrue(a.contains("192.168.1.100"));
        assertFalse(a.contains("192.168.0.90"));
        assertFalse(a.contains("192.168.1.89"));
        assertFalse(a.contains("192.168.1.101"));
        assertFalse(a.contains("192.168.2.100"));
        assertTrue(a.remove("192.168.1.90-192.168.1.100"));
        assertFalse(a.contains("192.168.1.96"));
        assertFalse(a.remove("192.168.1.90-192.168.1.100"));
    }
}
