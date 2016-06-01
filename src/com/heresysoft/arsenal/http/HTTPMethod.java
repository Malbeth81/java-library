package com.heresysoft.arsenal.http;

import java.util.EnumSet;

public enum HTTPMethod {
    OPTIONS,
    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    TRACE,
    CONNECT;

    public static final HTTPMethod parse(String value) {
        for (final HTTPMethod element : EnumSet.allOf(HTTPMethod.class))
            if (value.equalsIgnoreCase(element.toString()))
                return element;
        return null;
    }
};
