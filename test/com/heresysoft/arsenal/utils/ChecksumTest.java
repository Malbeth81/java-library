package com.heresysoft.arsenal.utils;

import com.heresysoft.arsenal.utils.ByteUtils;
import com.heresysoft.arsenal.utils.Checksum;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChecksumTest
{

    @Test
    public final void testChecksum()
    {
        byte[] a = ByteUtils.hexStringToByteArray("24240061123456FFFFFFFF9999033033353930312E3030302C412C323233322E363038332C4E2C31313430342E383133372C452C302E30302C2C3031303830392C2C2A31327C31322E327C3139347C303430307C303030302C30303030");
        assertEquals(0, Checksum.calculate(null, "CRC16-CCITT"));
        assertEquals(0, Checksum.calculate(new byte[0], "CRC16-CCITT"));
        assertEquals(33611, Checksum.calculate(a, "CRC16-CCITT"));
        assertEquals(46203, Checksum.calculate(a, 15, "CRC16-CCITT"));
        byte[] b = ByteUtils.hexStringToByteArray("0033000900000000000018FD4D754D1D4D754D1C4D754D21FB9B48BE02BA6042001C6A0000000D001300000C0000000007D007D000");
        assertEquals(10294, Checksum.calculate(b, "CRC16-IBM"));
        byte[] c = ByteUtils.hexStringToByteArray("405028360033000900000000000018FD4D754D1D4D754D1C4D754D21FB9B48BE02BA6042001C6A0000000D001300000C0000000007D007D000");
        assertEquals(10294, Checksum.calculate(c, 4, c.length - 4, "CRC16-IBM"));
    }

}
