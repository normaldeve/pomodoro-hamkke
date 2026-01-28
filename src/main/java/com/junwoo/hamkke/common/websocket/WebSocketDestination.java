package com.junwoo.hamkke.common.websocket;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 28.
 */
public final class WebSocketDestination {

    private WebSocketDestination(){}

    private static final String BASE_PATH = "/topic/study-room";

    public static String reflection(Long roomId) {
        return BASE_PATH + "/" + roomId + "/reflection";
    }

    public static String message(Long roomId) {
        return BASE_PATH + "/" + roomId + "/messages";
    }

    public static String member(Long roomId) {
        return BASE_PATH + "/" + roomId + "/members";
    }

    public static String timer(Long roomId) {
        return BASE_PATH + "/" + roomId + "/timer";
    }

    public static String focusTime(Long roomId) {
        return BASE_PATH + "/" + roomId + "/focus-time";
    }

    public static String roomStatus(Long roomId) {
        return BASE_PATH + "/" + roomId + "/status";
    }
}
