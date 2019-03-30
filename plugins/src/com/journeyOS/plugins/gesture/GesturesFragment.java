/*
 * Copyright (c) 2019 anqi.huang@outlook.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.journeyOS.plugins.gesture;

import android.app.Activity;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;

import com.journeyOS.base.widget.SettingView;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edgeprovider.IGestureProvider;
import com.journeyOS.core.api.plugins.IPlugins;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.base.BaseFragment;
import com.journeyOS.core.database.gesture.Gesture;
import com.journeyOS.core.type.FingerDirection;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class GesturesFragment extends BaseFragment {

    @BindView(R2.id.up)
    SettingView mUp;
    @BindView(R2.id.up_long)
    SettingView mUpLong;
    @BindView(R2.id.down)
    SettingView mDown;
    @BindView(R2.id.down_long)
    SettingView mDownLong;
    @BindView(R2.id.left)
    SettingView mLeft;
    @BindView(R2.id.left_long)
    SettingView mLeftLong;
    @BindView(R2.id.right)
    SettingView mRight;
    @BindView(R2.id.right_long)
    SettingView mRightLong;
    @BindView(R2.id.click)
    SettingView mClick;
    @BindView(R2.id.click_long)
    SettingView mPress;

    static Activity mContext;
    static int sOrientation;

    public static Fragment newInstance(Activity activity, int orientation) {
        GesturesFragment fragment = new GesturesFragment();
        mContext = activity;
        sOrientation = orientation;
        return fragment;
    }

    @Override
    public int attachLayoutRes() {
        return R.layout.fragment_gestures;
    }

    @Override
    public void initBeforeView() {
        super.initBeforeView();
    }

    @Override
    public void initViews() {
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Configuration.ORIENTATION_PORTRAIT == sOrientation) {
            setDisabled(mLeft);
            setDisabled(mRight);
        } else {
            setDisabled(mDown);
        }
        setDisabled(mPress);
        mPress.setRightSummary(mContext.getString(R.string.press_action));

        CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
            @Override
            public void run() {
                final List<Gesture> gestures = CoreManager.getDefault().getImpl(IGestureProvider.class).getConfig(sOrientation);
                if (gestures != null) {
                    CoreManager.getDefault().getImpl(ICoreExecutors.class).mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            for (Gesture gesture : gestures) {
                                String direction = CoreManager.getDefault().getImpl(IGestureProvider.class).getDirection(gesture.gestureDirection);
                                //用switch报错，奇怪
                                if (FingerDirection.UP.name().toLowerCase().equals(direction)) {
                                    setRightSummary(mUp, gesture);
                                } else if (FingerDirection.LONG_UP.name().toLowerCase().equals(direction)) {
                                    setRightSummary(mUpLong, gesture);
                                } else if (FingerDirection.DOWN.name().toLowerCase().equals(direction)) {
                                    setRightSummary(mDown, gesture);
                                } else if (FingerDirection.LONG_DOWN.name().toLowerCase().equals(direction)) {
                                    setRightSummary(mDownLong, gesture);
                                } else if (FingerDirection.LEFT.name().toLowerCase().equals(direction)) {
                                    setRightSummary(mLeft, gesture);
                                } else if (FingerDirection.LONG_LEFT.name().toLowerCase().equals(direction)) {
                                    setRightSummary(mLeftLong, gesture);
                                } else if (FingerDirection.RIGHT.name().toLowerCase().equals(direction)) {
                                    setRightSummary(mRight, gesture);
                                } else if (FingerDirection.LONG_RIGHT.name().toLowerCase().equals(direction)) {
                                    setRightSummary(mRightLong, gesture);
                                } else if (FingerDirection.CLICK.name().toLowerCase().equals(direction)) {
                                    setRightSummary(mClick, gesture);
                                } else if (FingerDirection.LONG_PRESS.name().toLowerCase().equals(direction)) {
                                    setRightSummary(mPress, gesture);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    @OnClick({R2.id.up})
    public void listenerUp() {
        CoreManager.getDefault().getImpl(IPlugins.class).navigationMoreSelectorActivity(mContext, sOrientation, FingerDirection.UP);
    }

    @OnClick({R2.id.up_long})
    public void listenerUpLong() {
        CoreManager.getDefault().getImpl(IPlugins.class).navigationMoreSelectorActivity(mContext, sOrientation, FingerDirection.LONG_UP);
    }

    @OnClick({R2.id.down})
    public void listenerDown() {
        CoreManager.getDefault().getImpl(IPlugins.class).navigationMoreSelectorActivity(mContext, sOrientation, FingerDirection.DOWN);
    }

    @OnClick({R2.id.down_long})
    public void listenerDownLong() {
        CoreManager.getDefault().getImpl(IPlugins.class).navigationMoreSelectorActivity(mContext, sOrientation, FingerDirection.LONG_DOWN);
    }

    @OnClick({R2.id.left})
    public void listenerLeft() {
        CoreManager.getDefault().getImpl(IPlugins.class).navigationMoreSelectorActivity(mContext, sOrientation, FingerDirection.LEFT);
    }

    @OnClick({R2.id.left_long})
    public void listenerLeftLong() {
        CoreManager.getDefault().getImpl(IPlugins.class).navigationMoreSelectorActivity(mContext, sOrientation, FingerDirection.LONG_LEFT);
    }

    @OnClick({R2.id.right})
    public void listenerRight() {
        CoreManager.getDefault().getImpl(IPlugins.class).navigationMoreSelectorActivity(mContext, sOrientation, FingerDirection.RIGHT);
    }

    @OnClick({R2.id.right_long})
    public void listenerRightLong() {
        CoreManager.getDefault().getImpl(IPlugins.class).navigationMoreSelectorActivity(mContext, sOrientation, FingerDirection.LONG_RIGHT);
    }

    @OnClick({R2.id.click})
    public void listenerClick() {
        CoreManager.getDefault().getImpl(IPlugins.class).navigationMoreSelectorActivity(mContext, sOrientation, FingerDirection.CLICK);
    }

    @OnClick({R2.id.click_long})
    public void listenerClickLong() {
        CoreManager.getDefault().getImpl(IPlugins.class).navigationMoreSelectorActivity(mContext, sOrientation, FingerDirection.LONG_PRESS);
    }

    void setRightSummary(SettingView view, Gesture gesture) {
        view.setRightSummary(gesture.comment);
    }

    void setDisabled(SettingView view) {
        view.setEnabled(false);
        view.setAlpha(0.5f);
    }
}
