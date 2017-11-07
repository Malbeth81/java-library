package malbeth.javautils.http;

import malbeth.javautils.ByteUtils;
import org.junit.Test;

import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class HTTPServerConnectionHandlerTest
{
    private String lastFrameContent = null;
    private HTTPServerHandler server = null;

    private static final String serverAddress = "127.0.0.1";
    private static final int serverPort = 9999;

    private class TestConnectionHandler extends HTTPServerConnectionHandler
    {
        public TestConnectionHandler(HTTPServerHandler server, Socket socket)
        {
            super(server, socket, false, true);
        }

        @Override
        protected void onError()
        {
        }

        @Override
        protected void onStart()
        {
        }

        @Override
        protected void onStop()
        {
        }

        @Override
        protected boolean onUpgrade(String upgradeTo)
        {
            return true;
        }

        @Override
        protected void processFrame(WebSocketFrame frame)
        {
            if (frame.getType() == WebSocketFrameType.textFrame)
            {
                try
                {
                    lastFrameContent = new String(frame.getContent(), "UTF-8");
                }
                catch (Exception e)
                {
                    lastFrameContent = "";
                    e.printStackTrace();
                }
            }
            else
                lastFrameContent = ByteUtils.byteArrayToHexString(frame.getContent());
        }

        @Override
        protected void processRequest(HTTPRequest request, HTTPResponse response)
        {
        }

        @Override
        protected void requestProcessed(int responseTime)
        {
        }

    }

    private boolean prepareTest()
    {
        try
        {
            server = new HTTPServerHandler(serverAddress, serverPort, 0, 10)
            {
                @Override
                protected HTTPServerConnectionHandler initializeConnectionHandler(Socket socket, boolean secure)
                {
                    return null;
                }
            };

            return true;
        }
        catch (Exception e)
        {
            fail("An error occured: " + e);
        }

        return false;
    }

    private void terminateTest()
    {
        server.shutdown();
    }

    @Test
    public void testHandleData()
    {
        if (prepareTest())
        {
            TestConnectionHandler handler = new TestConnectionHandler(server, null);
            server.addConnectionHandler(handler, false);

            HTTPRequest request = new HTTPRequest();
            request.method = HTTPMethod.GET;
            request.version = HTTPVersion.HTTP11;
            request.headers.Connection = "Upgrade";
            request.headers.Host = serverAddress;
            request.headers.Origin = "http://" + serverAddress;
            request.headers.SecWebSocketKey = "NAc6fvx9i1iZNy+oLZQ/8Q==";
            request.headers.Upgrade = "websocket";
            request.url = "/";
            handler.handleData(request.getHeaderBytes());

            handler.handleData(ByteUtils.hexStringToByteArray("81FE00BFE46878FB9F4A159E9000179FC6525A88811C318F81053E978B070A"));
            handler.handleData(ByteUtils.hexStringToByteArray("B98D0C5AD7C609159491060CD9DE5D4BCED4594ACEC84A118F8105319F974A42D9D7504BD9C84A1998870D0B88AF0D01D9DE4A3BCAD02C4FC8A05B55B9D02A4BD6D6583ACCC95149CDD65C4AC9D75A48CCD4584ECCDC4A54D9851D1B8F8D0716B2804A42CAD2445A88811B0B928B06319FC6525A9EDC0B49C2D75A1BD6DD5B40C3C95C4B9ED3451AC3D30955C9DD0B1EC9D55A1CCBD05F41D9C84A0A9E951D1D88904A42C9D05805"));
            assertEquals("{\"method\":\"setItemFloorBid\",\"amount\":5350125,\"itemIds\":\"383\",\"accessKey\":\"C14D73D3-B4B3-20B7-9162422320700678\",\"auctionId\":16,\"sessionId\":\"e8c1932c-9388-43e7-b87a-29cf212d0479\",\"request\":240}", lastFrameContent);

            //handler.handleData(ByteUtils.hexStringToByteArray("81FE00BF0F04A99E7426C4FB7B6CC6FA2D3E8BED6A70E0EA6A69EFF2606BDBDC66608BB22D65C4F17A6ADDBC35319AAB3F359BAB2326C0EA6A69E0FA7C2693BC3C3C9ABC2326C8FD6C61DAED4461D0BC3526EAAF3B409EAD4B3784DC3B469AB33D34EBA9223D98A83D309BAC3C3699A93F349FA9372685BC6E71CAEA666BC7D76B2693AF39288BED6A77DAF7606AE0FA2D3E8BFB376798A73C36CAB3363791A622309AFB3829CBA6386584AC3667CFAC3E36CDAE3B3390BC2326DBFB7E71CCED7B2693AC3B35D481FE00BFEB4C70BF906E1DDA9F241FDBC97652CC8E3839CB8E2136D3842302FD82285293C92D1DD09E22049DD179438ADB7D428AC76E19CB8E2139DB986E4A9DD874439DC76E11DC882903CCA029099DD16E338EDF08478CAF7F5DFDDF0E4392D97C3288C6754189D978428DD87E4088DB7C4688D36E5C9D8A3913CB82231EF68F6E4A8EDD6052CC8E3F03D6842239DBC97652DAD32F4186D87E1392D27F4887C67843DADC611287DC2D5D8DD22F168DDA7E148FDF7B499DC76E02DA9A3915CC9F6E4A8DDF7E0D81FE00BFEA6EA488914CC9ED9E06CBECC85486FB8F1AEDFC8F03E2E48501D6CA830A86A4C80FC9E79F00D0AAD05B97BDDA5F96BDC64CCDFC8F03EDEC994C9EAAD95697AAC64CC5EB890BD7FBA10BDDAAD04CE7B9DE2A93BBAE5D89CADE2C97A5D85EE6BFC75795BED85A96BAD95C94BFDA5E92BFD24C88AA8B1BC7FC8301CAC18E4C9EB9DC4286FB8F1DD7E18500EDECC85486EDD20D95B1D95CC7A5D35D9CB0C75A97EDDD43C6B0DD0F89BAD30DC2BADB5CC0B8DE599DAAC64CD6ED9B1BC1FB9E4C9EBADE5DD9"));

            terminateTest();
        }
    }
}
