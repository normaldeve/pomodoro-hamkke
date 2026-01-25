package com.junwoo.hamkke.common.logging;

public class LogFormatter {

    public static String indent(int depth) {
        return "│   ".repeat(Math.max(0, depth));
    }

    public static String request(int depth) {
        return indent(depth) + "→ ";
    }

    public static String response(int depth) {
        return indent(depth) + "← ";
    }

    public static String exception(int depth) {
        return indent(depth) + "✕ ";
    }
}