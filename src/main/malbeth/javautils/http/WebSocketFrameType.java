package malbeth.javautils.http;

import java.util.EnumSet;

public enum WebSocketFrameType {
    continuationFrame {
        @Override
        public byte getValue() {
            return 0x00;
        }

        @Override
        public String toString() {
            return "0";
        }
    },
    textFrame {
        @Override
        public byte getValue() {
            return 0x01;
        }

        @Override
        public String toString() {
            return "1";
        }
    },
    binaryFrame {
        @Override
        public byte getValue() {
            return 0x02;
        }

        @Override
        public String toString() {
            return "2";
        }
    },
    connectionClose {
        @Override
        public byte getValue() {
            return 0x08;
        }

        @Override
        public String toString() {
            return "8";
        }
    },
    ping {
        @Override
        public byte getValue() {
            return 0x09;
        }

        @Override
        public String toString() {
            return "9";
        }
    },
    pong {
        @Override
        public byte getValue() {
            return 0x0A;
        }

        @Override
        public String toString() {
            return "A";
        }
    };

    abstract byte getValue();

    public static final WebSocketFrameType parse(byte value) {
        for (final WebSocketFrameType element : EnumSet.allOf(WebSocketFrameType.class))
            if ((value & 0x0f) == element.getValue())
                return element;
        return null;
    }
}
