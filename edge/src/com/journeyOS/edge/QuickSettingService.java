package com.journeyOS.edge;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.thread.CoreExecutorsImpl;

public class QuickSettingService extends TileService implements CoreExecutorsImpl.OnMessageListener {
    private static final String TAG = QuickSettingService.class.getSimpleName();
    private static final boolean DEBUG = false;

    private static Handler mHandler = CoreManager.getDefault().getImpl(ICoreExecutors.class).getHandle(ICoreExecutors.HANDLER_QS);
    private static final int MSG_BING_SERVICE = 0x01;
    private static final int MSG_BALL_SHOW = 0x02;
    private static final int MSG_BALL_HIDE = 0x04;

    public QuickSettingService() {
        super();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (DEBUG) LogUtils.d(TAG, "on destroy");
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        if (DEBUG) LogUtils.d(TAG, "on tile added");
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
        if (DEBUG) LogUtils.d(TAG, "on tile removed");
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        if (DEBUG) LogUtils.d(TAG, "on start listening");
        if (mHandler == null) {
            mHandler = CoreManager.getDefault().getImpl(ICoreExecutors.class).getHandle(ICoreExecutors.HANDLER_QS);
        }
        CoreManager.getDefault().getImpl(ICoreExecutors.class).setOnMessageListener(mHandler, this);
        boolean daemon = SpUtils.getInstant().getBoolean(Constant.DAEMON, true);
        if (daemon) mHandler.sendEmptyMessage(MSG_BING_SERVICE);
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        if (DEBUG) LogUtils.d(TAG, "on stop listening");
    }

    @Override
    public void onClick() {
        super.onClick();
        int state = getQsTile().getState();
        LogUtils.d(TAG, "on click, state = [" + state + "]");
        mHandler.sendEmptyMessage(state == Tile.STATE_ACTIVE ? MSG_BALL_HIDE : MSG_BALL_SHOW);
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (DEBUG) LogUtils.d(TAG, "on bind, intent = [" + intent + "]");
        return super.onBind(intent);
    }

    @Override
    public void handleMessage(Message msg) {
        LogUtils.d(TAG, "message what = " + msg.what);
        switch (msg.what) {
            case MSG_BING_SERVICE:
                EdgeServiceManager.getDefault().bindEgdeService();
                break;
            case MSG_BALL_SHOW:
                SpUtils.getInstant().put(Constant.BALL, true);
                showingOrHidingBall(true);
                break;
            case MSG_BALL_HIDE:
                SpUtils.getInstant().put(Constant.BALL, false);
                showingOrHidingBall(false);
                break;
        }
    }

    private void showingOrHidingBall(boolean isShowing) {
        Icon icon;
        if (isShowing) {
            icon = Icon.createWithResource(getApplicationContext(), R.drawable.svg_core_ball);
            getQsTile().setState(Tile.STATE_ACTIVE);
            EdgeServiceManager.getDefault().showingOrHidingBall(true);
        } else {
            icon = Icon.createWithResource(getApplicationContext(), R.drawable.svg_core_ball_disable);
            getQsTile().setState(Tile.STATE_INACTIVE);
            EdgeServiceManager.getDefault().showingOrHidingBall(false);
        }
        getQsTile().setIcon(icon);
        getQsTile().updateTile();
    }
}
