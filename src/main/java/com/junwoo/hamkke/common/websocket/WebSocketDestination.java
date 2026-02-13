package com.junwoo.hamkke.common.websocket;

import java.util.UUID;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 28.
 */
public final class WebSocketDestination {

    private WebSocketDestination(){}

    private static final String BASE_PATH = "/topic/study-room";

    public static String reflection(UUID roomId) {
        return BASE_PATH + "/" + roomId + "/reflection";
    }

    public static String message(UUID roomId) {
        return BASE_PATH + "/" + roomId + "/messages";
    }

    public static String member(UUID roomId) {
        return BASE_PATH + "/" + roomId + "/members";
    }

    public static String tick(UUID roomId) {
        return BASE_PATH + "/" + roomId + "/tick";
    }

    public static String focusTime(UUID roomId) {
        return BASE_PATH + "/" + roomId + "/focus-time";
    }

    public static String roomStatus(UUID roomId) {
        return BASE_PATH + "/" + roomId + "/status";
    }
}
