package malbeth.javautils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JSONTest
{

    @Test
    public final void testJSONFormat()
    {
        assertEquals("\"Onyx\\\\diamond brooch,\\r\\n\\\"Panthère\\\",\\tCartier\"", JSON.formatString("Onyx\\diamond brooch,\r\n\"Panthère\",\tCartier\b"));
    }

}
