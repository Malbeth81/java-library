package com.heresysoft.arsenal.utils;

import com.google.gson.*;

import java.util.*;

public class JSON {
    public static final String formatArray(Object[] array) {
        StringBuilder result = new StringBuilder();
        result.append('[');
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (result.length() > 1)
                    result.append(',');
                if (array[i] instanceof String)
                    result.append(formatString(array[i].toString()));
                else
                    result.append(array[i].toString());
            }
        }
        result.append(']');
        return result.toString();
    }

    public static final String formatArray(int[] array) {
        StringBuilder result = new StringBuilder();
        result.append('[');
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (result.length() > 1)
                    result.append(',');
                result.append(array[i]);
            }
            result.append(']');
        }
        return result.toString();
    }

    public static final String formatBoolean(boolean value) {
        if (value)
            return "true";
        return "false";
    }

    public static final String formatList(List<? extends Object> list) {
        StringBuilder result = new StringBuilder();
        result.append('[');
        if (list != null) {
            for (Iterator<? extends Object> it = list.iterator(); it.hasNext(); ) {
                Object element = it.next();
                if (result.length() > 1)
                    result.append(',');
                if (element instanceof String)
                    result.append(formatString(element.toString()));
                else
                    result.append(element.toString());
            }
        }
        result.append(']');
        return result.toString();
    }

    public static final String formatSet(Set<? extends Object> set) {
        StringBuilder result = new StringBuilder();
        result.append('[');
        if (set != null) {
            for (Iterator<? extends Object> it = set.iterator(); it.hasNext(); ) {
                Object element = it.next();
                if (result.length() > 1)
                    result.append(',');
                if (element instanceof String)
                    result.append(formatString(element.toString()));
                else
                    result.append(element.toString());
            }
        }
        result.append(']');
        return result.toString();
    }

    public static final String formatSimpleObject(Map<String, String> data) {
        if (data != null) {
            StringBuilder result = new StringBuilder();
            result.append('{');
            for (Iterator<Map.Entry<String, String>> it = data.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, String> entry = it.next();
                if (entry.getKey() != null && entry.getKey().trim().length() > 0) {
                    if (result.length() > 1)
                        result.append(',');
                    result.append("\"");
                    result.append(entry.getKey().replaceAll("\\p{Cntrl}", "").trim());
                    result.append("\":");
                    result.append(formatValue(entry.getValue()));
                }
            }
            result.append('}');
            return result.toString();
        }
        return null;
    }

    public static final String formatString(String text) {
        return "\"" + (text != null ? text.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\r", "\\\\r").replaceAll("\\n", "\\\\n").replaceAll("\\t", "\\\\t").replaceAll("\\\"", "\\\\\"").replaceAll("\\p{Cntrl}", "") : "") + "\"";
    }

    public static final String formatTimeZone(String timeZone) {
        return formatTimeZone(java.util.TimeZone.getTimeZone(timeZone));
    }

    public static final String formatTimeZone(String timeZone, Date date) {
        return formatTimeZone(java.util.TimeZone.getTimeZone(timeZone), date);
    }

    public static final String formatTimeZone(TimeZone timeZone) {
        return formatTimeZone(timeZone, new Date());
    }

    public static final String formatTimeZone(TimeZone timeZone, Date date) {
        if (timeZone != null) {
            return "{\"id\":" + JSON.formatString(timeZone.getID()) +
                    ",\"code\":" + JSON.formatString(timeZone.getDisplayName(timeZone.inDaylightTime(date), TimeZone.SHORT)) +
                    ",\"offset\":" + timeZone.getOffset(date.getTime()) / 1000 +
                    "}";
        }

        return "null";
    }

    public static final String formatValue(Object value) {
        if (value != null) {
            if (value instanceof String)
                return formatString((String) value);
            else
                return value.toString();
        }
        return "null";
    }

    public static final List<Object> parseArray(JsonElement elem) {
        if (elem != null && elem.isJsonArray() && !elem.isJsonNull()) {
            JsonArray object = elem.getAsJsonArray();
            List<Object> result = new LinkedList<Object>();
            for (Iterator<JsonElement> it = object.iterator(); it.hasNext(); ) {
                JsonElement value = it.next();
                if (value.isJsonNull())
                    result.add(null);
                else if (value.isJsonArray())
                    result.add(parseArray(value));
                else if (value.isJsonObject())
                    result.add(parseObject(value));
                else
                    result.add(parsePrimitive(value));
            }
            return result;
        }
        return null;
    }

    public static final List<Object> parseArray(String data) {
        if (data != null)
            return parseArray(new JsonParser().parse(data));
        return null;
    }

    public static final Map<String, Object> parseObject(JsonElement elem) {
        if (elem != null && elem.isJsonObject() && !elem.isJsonNull()) {
            JsonObject object = elem.getAsJsonObject();
            Map<String, Object> result = new TreeMap<String, Object>();
            for (Iterator<Map.Entry<String, JsonElement>> it = object.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, JsonElement> value = it.next();
                if (value.getValue().isJsonNull())
                    result.put(value.getKey(), null);
                else if (value.getValue().isJsonArray())
                    result.put(value.getKey(), parseArray(value.getValue()));
                else if (value.getValue().isJsonObject())
                    result.put(value.getKey(), parseObject(value.getValue()));
                else
                    result.put(value.getKey(), parsePrimitive(value.getValue()));
            }
            return result;
        }
        return null;
    }

    public static final Map<String, Object> parseObject(String data) {
        if (data != null)
            return parseObject(new JsonParser().parse(data));
        return null;
    }

    public static final Object parsePrimitive(JsonElement elem) {
        if (elem != null && elem.isJsonObject() && !elem.isJsonNull()) {
            JsonPrimitive value = elem.getAsJsonPrimitive();
            if (value.isBoolean())
                return value.getAsBoolean();
            else if (value.isNumber())
                return value.getAsNumber();
            else
                return value.getAsString();
        }
        return null;
    }

    public static final Object parsePrimitive(String data) {
        if (data != null)
            return parsePrimitive(new JsonParser().parse(data));
        return null;
    }

    public static final Map<String, String> parseSimpleObject(String data) {
        if (data != null) {
            JsonElement elem = new JsonParser().parse(data);
            if (elem.isJsonObject() && !elem.isJsonNull()) {
                JsonObject object = elem.getAsJsonObject();
                Map<String, String> params = new TreeMap<String, String>();
                for (Iterator<Map.Entry<String, JsonElement>> it = object.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<String, JsonElement> entry = it.next();
                    if (entry.getValue().isJsonNull())
                        params.put(entry.getKey(), null);
                    else
                        params.put(entry.getKey(), entry.getValue().getAsString());
                }
                return params;
            }
        }
        return null;
    }

}
