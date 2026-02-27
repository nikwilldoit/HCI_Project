package com.example.phasmatic.data.ai;

public class OpenAIErrorHandler {

    public static String mapHttpError(int code, String body) {
        if (code == 401) {
            return "Authentication error: check API key.";
        } else if (code == 403) {
            return "Access forbidden: account or model not allowed.";
        } else if (code == 404) {
            return "Endpoint or model not found.";
        } else if (code == 429) {
            return "Rate limit / quota exceeded. Try again later.";
        } else if (code >= 500 && code < 600) {
            return "OpenAI server error (" + code + "). Please retry.";
        } else {
            return "Unexpected HTTP " + code + " : " + safeBody(body);
        }
    }

    public static String mapClientException(String message) {
        if (message == null) return "Unknown client error.";
        if (message.contains("timeout")) {
            return "Network timeout. Please check your connection.";
        }
        return "Network/client error: " + message;
    }

    public static String mapParseException(String message) {
        return "Response parse error: " + (message != null ? message : "invalid JSON");
    }

    private static String safeBody(String body) {
        if (body == null) return "";
        body = body.trim();
        if (body.length() > 200) {
            return body.substring(0, 200) + "...";
        }
        return body;
    }
}
