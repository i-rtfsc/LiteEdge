package com.journeyOS.core;

import com.journeyOS.core.type.BallState;
import com.journeyOS.core.type.EdgeDirection;

public class StateMachine {
    private static Object mEdLock = new Object();
    private static EdgeDirection mEd = EdgeDirection.LEFT;

    private static Object mBallLock = new Object();
    private static BallState mBs = BallState.HIDE;

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

    public static BallState getBallState() {
        synchronized (mBallLock) {
            return mBs;
        }
    }

    public static void setBallState(BallState ballState) {
        synchronized (mBallLock) {
            mBs = ballState;
        }
    }
}
