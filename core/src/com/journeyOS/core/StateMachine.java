package com.journeyOS.core;

import com.journeyOS.core.type.EdgeDirection;

public class StateMachine {
    private static Object mEdLock = new Object();
    private static EdgeDirection mEd = EdgeDirection.LEFT;

    public static void setEdgeDirection(EdgeDirection direction) {
        synchronized (mEdLock) {
            mEd = direction;
        }
    }

    public static EdgeDirection getEdgeDirection() {
        synchronized (mEdLock) {
            return mEd;
        }
    }
}
