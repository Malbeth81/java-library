package com.heresysoft.arsenal.http;

import java.util.EnumSet;

public enum HTTPVersion {
    HTTP10 {
        @Override
        public String toString() {
            return "HTTP/1.0";
        }
    },
    HTTP11 {
        @Override
        public String toString() {
            return "HTTP/1.1";
        }
    };

    public static final HTTPVersion parse(String value) {
        for (final HTTPVersion element : EnumSet.allOf(HTTPVersion.class))
            if (value.equalsIgnoreCase(element.toString()))
                return element;
        return null;
    }
};
