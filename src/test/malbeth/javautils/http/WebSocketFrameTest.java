package malbeth.javautils.http;

import malbeth.javautils.ByteUtils;
import org.junit.Test;

import static org.junit.Assert.*;


public class WebSocketFrameTest
{
    private final String extractFrameContent(WebSocketFrame frame)
    {
        if (frame.getType() == WebSocketFrameType.textFrame)
        {
            try
            {
                return new String(frame.getContent(), "UTF-8");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return "";
        }
        else
            return ByteUtils.byteArrayToHexString(frame.getContent());
    }

    @Test
    public final void testWebSocketFrame()
    {
        WebSocketFrame f1 = WebSocketFrame.parse(ByteUtils.hexStringToByteArray("8184F473FC8280168FF6"));
        assertNotNull(f1);
        assertEquals("test", extractFrameContent(f1));

        WebSocketFrame f2 = WebSocketFrame.parse(ByteUtils.hexStringToByteArray("81FE00BFE46878FB9F4A159E9000179FC6525A88811C318F81053E978B070A"));
        assertNull(f2);

        WebSocketFrame f3 = WebSocketFrame.parse(ByteUtils.hexStringToByteArray("81FE00BFE46878FB9F4A159E9000179FC6525A88811C318F81053E978B070AB98D0C5AD7C609159491060CD9DE5D4BCED4594ACEC84A118F8105319F974A42D9D7504BD9C84A1998870D0B88AF0D01D9DE4A3BCAD02C4FC8A05B55B9D02A4BD6D6583ACCC95149CDD65C4AC9D75A48CCD4584ECCDC4A54D9851D1B8F8D0716B2804A42CAD2445A88811B0B928B06319FC6525A9EDC0B49C2D75A1BD6DD5B40C3C95C4B9ED3451AC3D30955C9DD0B1EC9D55A1CCBD05F41D9C84A0A9E951D1D88904A42C9D05805"));
        assertNotNull(f3);
        assertEquals(191, f3.getContent().length);
        System.out.println(extractFrameContent(f3));
    }

}
