package com.heresysoft.arsenal.utils;

//import static org.junit.Assert.assertEquals;

import com.heresysoft.arsenal.utils.Hash;
import org.junit.Test;

public class HashTest
{

    @Test
    public final void testHash()
    {
        System.out.println(javax.xml.bind.DatatypeConverter.printBase64Binary(Hash.getHash("SHA-1", "dGhlIHNhbXBsZSBub25jZQ==258EAFA5-E914-47DA-95CA-C5AB0DC85B11")));
        //assertEquals(0, Hash.getHash(null, "CRC16CCITT"));
    }

}
