package malbeth.javautils.http;

import malbeth.javautils.ByteUtils;

import java.nio.ByteBuffer;

public final class WebSocketFrame {
    private byte[] content;
    private boolean last;
    private byte[] mask;
    private WebSocketFrameType type;

    public WebSocketFrame(byte[] content, WebSocketFrameType type, byte[] mask, boolean last) {
        this.content = content;
        this.last = last;
        this.mask = mask;
        this.type = type;
    }

    private static final byte[] applyMaskToData(byte[] data, byte[] mask) {
        if (data != null && mask != null && mask.length == 4) {
            byte[] result = new byte[data.length];
            for (int i = 0; i < data.length; i++)
                result[i] = (byte) (data[i] ^ mask[i % 4]);
            return result;
        }
        return null;
    }

    public synchronized final byte[] getBytes() {
        if (content != null && type != null && (mask == null || mask.length == 4)) {
            try {
                // Setup frame header
                byte[] header = new byte[]{type.getValue(), (byte) (content.length > 65535 ? 127 : (content.length > 125 ? 126 : content.length))};
                ByteUtils.setBit(header, 15, (last ? 1 : 0));
                ByteUtils.setBit(header, 7, (mask != null ? 1 : 0));

                // Build frame
                ByteBuffer buffer = ByteBuffer.allocate(size());
                buffer.put(header);
                if (content.length > 65535)
                    buffer.put(ByteUtils.longToByteArray(content.length));
                else if (content.length > 125)
                    buffer.put(ByteUtils.integerToByteArray(content.length, 2));
                if (mask != null) {
                    buffer.put(mask, 0, 4);
                    buffer.put(applyMaskToData(content, mask));
                } else
                    buffer.put(content);
                return buffer.array();
            } catch (Exception e) {
                System.err.println("An error occured:");
                e.printStackTrace();
            }
        }
        return null;
    }

    public synchronized final byte[] getContent() {
        return content;
    }

    public synchronized final byte[] getMask() {
        return mask;
    }

    public synchronized final WebSocketFrameType getType() {
        return type;
    }

    public synchronized final boolean isLast() {
        return last;
    }

    public synchronized final void isLast(boolean last) {
        this.last = last;
    }

    public static final WebSocketFrame parse(byte[] data) {
        if (data != null && data.length > 2) {
            try {
                byte[] content = null;
                int contentLength = ByteUtils.getBits(data[1], 0, 7); // Only read first 7 bits
                boolean isLast = ByteUtils.getBit(data[0], 7) > 0;
                boolean isMasked = ByteUtils.getBit(data[1], 7) > 0;
                byte[] mask = null;
                WebSocketFrameType type = WebSocketFrameType.parse(data[0]);
                int index = 2;
                if (contentLength == 126) {
                    if (data.length > index + 2)
                        contentLength = ByteUtils.byteArrayToInteger(data, index, 2);
                    else
                        return null;
                    index += 2;
                } else if (contentLength == 127) {
                    if (data.length > index + 8)
                        contentLength = (int) ByteUtils.byteArrayToLong(data, index, 8);
                    else
                        return null;
                    index += 8;
                }
                if (isMasked) {
                    if (data.length > index + 4)
                        mask = ByteUtils.copyArray(data, index, 4);
                    else
                        return null;
                    index += 4;
                }
                if (data.length >= index + contentLength)
                    content = ByteUtils.copyArray(data, index, contentLength);
                else
                    return null;
                return new WebSocketFrame((mask != null ? applyMaskToData(content, mask) : content), type, mask, isLast);
            } catch (Exception e) {
                System.err.println("An error occured:");
                e.printStackTrace();
            }
        }
        return null;
    }

    public synchronized final void setContent(byte[] content) {
        this.content = content;
    }

    public synchronized final void setMask(byte[] mask) {
        this.mask = mask;
    }

    public synchronized final void setType(WebSocketFrameType type) {
        this.type = type;
    }

    public synchronized final int size() {
        int result = 2;
        if (content != null)
            result += content.length + (content.length > 65535 ? 8 : (content.length > 125 ? 2 : 0));
        if (mask != null)
            result += 4;
        return result;
    }

}
