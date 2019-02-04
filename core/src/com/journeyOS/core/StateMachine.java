package com.journeyOS.core;

import com.journeyOS.core.type.EdgeDirection;

public class StateMachine {
    private static Object mEdLock = new Object();
    private static EdgeDirection mEd = EdgeDirection.LEFT;

    private static Object mBatteryLock = new Object();
    private static int mBattery = 100;

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

    public static void setBattery(int battery) {
        synchronized (mBatteryLock) {
            mBattery = battery;
        }
    }

    public static int getBattery() {
        synchronized (mBatteryLock) {
            return mBattery;
        }
    }
}
