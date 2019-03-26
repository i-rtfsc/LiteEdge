package com.journeyOS.core;

import com.journeyOS.core.type.BallState;
import com.journeyOS.core.type.BarrageState;
import com.journeyOS.core.type.EdgeDirection;

public class StateMachine {
    private static Object mEdLock = new Object();
    private static EdgeDirection mEd = EdgeDirection.LEFT;

    private static Object mBallLock = new Object();
    private static BallState mBs = BallState.HIDE;

    private static Object mBarrageLock = new Object();
    private static BarrageState mBarrageState = BarrageState.HIDE;

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

    public static BarrageState getBarrageState() {
        synchronized (mBarrageLock) {
            return mBarrageState;
        }
    }

    public static void setBarrageState(BarrageState barrageState) {
        synchronized (mBarrageLock) {
            mBarrageState = barrageState;
        }
    }
}
