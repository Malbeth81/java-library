package com.heresysoft.arsenal.http;

import java.util.EnumSet;

public enum HTTPStatus {
    HTTP100 {
        @Override
        public String getCode() {
            return "100";
        }

        @Override
        public String toString() {
            return "100 Continue";
        }
    },
    HTTP101 {
        @Override
        public String getCode() {
            return "101";
        }

        @Override
        public String toString() {
            return "101 Switching Protocols";
        }
    },
    HTTP200 {
        @Override
        public String getCode() {
            return "200";
        }

        @Override
        public String toString() {
            return "200 OK";
        }
    },
    HTTP201 {
        @Override
        public String getCode() {
            return "201";
        }

        @Override
        public String toString() {
            return "201 Created";
        }
    },
    HTTP202 {
        @Override
        public String getCode() {
            return "202";
        }

        @Override
        public String toString() {
            return "202 Accepted";
        }
    },
    HTTP203 {
        public String getCode() {
            return "203";
        }

        @Override
        public String toString() {
            return "203 Non-Authoritative Information";
        }
    },
    HTTP204 {
        public String getCode() {
            return "204";
        }

        @Override
        public String toString() {
            return "204 No Content";
        }
    },
    HTTP205 {
        public String getCode() {
            return "205";
        }

        @Override
        public String toString() {
            return "205 Reset Content";
        }
    },
    HTTP206 {
        @Override
        public String getCode() {
            return "206";
        }

        @Override
        public String toString() {
            return "206 Partial Content";
        }
    },
    HTTP300 {
        @Override
        public String getCode() {
            return "300";
        }

        @Override
        public String toString() {
            return "300 Multiple Choices";
        }
    },
    HTTP301 {
        @Override
        public String getCode() {
            return "301";
        }

        @Override
        public String toString() {
            return "301 Moved Permanently";
        }
    },
    HTTP302 {
        @Override
        public String getCode() {
            return "302";
        }

        @Override
        public String toString() {
            return "302 Found";
        }
    },
    HTTP303 {
        @Override
        public String getCode() {
            return "303";
        }

        @Override
        public String toString() {
            return "303 See Other";
        }
    },
    HTTP304 {
        @Override
        public String getCode() {
            return "304";
        }

        @Override
        public String toString() {
            return "304 Not Modified";
        }
    },
    HTTP305 {
        @Override
        public String getCode() {
            return "305";
        }

        @Override
        public String toString() {
            return "305 Use Proxy";
        }
    },
    HTTP307 {
        @Override
        public String getCode() {
            return "307";
        }

        @Override
        public String toString() {
            return "307 Temporary Redirect";
        }
    },
    HTTP400 {
        @Override
        public String getCode() {
            return "400";
        }

        @Override
        public String toString() {
            return "400 Bad Request";
        }
    },
    HTTP401 {
        @Override
        public String getCode() {
            return "401";
        }

        @Override
        public String toString() {
            return "401 Unauthorized";
        }
    },
    HTTP402 {
        @Override
        public String getCode() {
            return "402";
        }

        @Override
        public String toString() {
            return "402 Payment Required";
        }
    },
    HTTP403 {
        @Override
        public String getCode() {
            return "403";
        }

        @Override
        public String toString() {
            return "403 Forbidden";
        }
    },
    HTTP404 {
        @Override
        public String getCode() {
            return "404";
        }

        @Override
        public String toString() {
            return "404 Not Found";
        }
    },
    HTTP405 {
        @Override
        public String getCode() {
            return "405";
        }

        @Override
        public String toString() {
            return "405 Method Not Allowed";
        }
    },
    HTTP406 {
        @Override
        public String getCode() {
            return "406";
        }

        @Override
        public String toString() {
            return "406 Not Acceptable";
        }
    },
    HTTP407 {
        @Override
        public String getCode() {
            return "407";
        }

        @Override
        public String toString() {
            return "407 Proxy Authentication Required";
        }
    },
    HTTP408 {
        @Override
        public String getCode() {
            return "408";
        }

        @Override
        public String toString() {
            return "408 Request Timeout";
        }
    },
    HTTP409 {
        @Override
        public String getCode() {
            return "409";
        }

        @Override
        public String toString() {
            return "409 Conflict";
        }
    },
    HTTP410 {
        @Override
        public String getCode() {
            return "410";
        }

        @Override
        public String toString() {
            return "410 Gone";
        }
    },
    HTTP411 {
        @Override
        public String getCode() {
            return "411";
        }

        @Override
        public String toString() {
            return "411 Length Required";
        }
    },
    HTTP412 {
        @Override
        public String getCode() {
            return "412";
        }

        @Override
        public String toString() {
            return "412 Precondition Failed";
        }
    },
    HTTP413 {
        @Override
        public String getCode() {
            return "413";
        }

        @Override
        public String toString() {
            return "413 Request Entity Too Large";
        }
    },
    HTTP414 {
        @Override
        public String getCode() {
            return "414";
        }

        @Override
        public String toString() {
            return "414 Request-URI Too Long";
        }
    },
    HTTP415 {
        @Override
        public String getCode() {
            return "415";
        }

        @Override
        public String toString() {
            return "415 Unsupported Media Type";
        }
    },
    HTTP416 {
        @Override
        public String getCode() {
            return "416";
        }

        @Override
        public String toString() {
            return "416 Requested Range Not Satisfiable";
        }
    },
    HTTP417 {
        @Override
        public String getCode() {
            return "417";
        }

        @Override
        public String toString() {
            return "417 Expectation Failed";
        }
    },
    HTTP500 {
        @Override
        public String getCode() {
            return "500";
        }

        @Override
        public String toString() {
            return "500 Internal Server Error";
        }
    },
    HTTP501 {
        @Override
        public String getCode() {
            return "501";
        }

        @Override
        public String toString() {
            return "501 Not Implemented";
        }
    },
    HTTP502 {
        @Override
        public String getCode() {
            return "502";
        }

        @Override
        public String toString() {
            return "502 Bad Gateway";
        }
    },
    HTTP503 {
        @Override
        public String getCode() {
            return "503";
        }

        @Override
        public String toString() {
            return "503 Service Unavailable";
        }
    },
    HTTP504 {
        @Override
        public String getCode() {
            return "504";
        }

        @Override
        public String toString() {
            return "504 Gateway Timeout";
        }
    },
    HTTP505 {
        @Override
        public String getCode() {
            return "505";
        }

        @Override
        public String toString() {
            return "505 HTTP Version Not Supported";
        }
    };

    abstract String getCode();

    public static final HTTPStatus parse(String value) {
        for (final HTTPStatus element : EnumSet.allOf(HTTPStatus.class))
            if (value.startsWith(element.getCode()))
                return element;
        return null;
    }
};
