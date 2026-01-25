package com.junwoo.hamkke.common.logging;

public class LogTraceContext {

    private static final ThreadLocal<Integer> depthHolder =
            ThreadLocal.withInitial(() -> 0);

    public static int increase() {
        int depth = depthHolder.get() + 1;
        depthHolder.set(depth);
        return depth;
    }

    public static int decrease() {
        int depth = depthHolder.get() - 1;
        depthHolder.set(Math.max(depth, 0));
        return depth;
    }

    public static int getDepth() {
        return depthHolder.get();
    }

    public static void clear() {
        depthHolder.remove();
    }
}