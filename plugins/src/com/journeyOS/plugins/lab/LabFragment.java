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

package com.journeyOS.plugins.lab;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.widget.SettingSwitch;
import com.journeyOS.base.widget.SettingView;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.StateMachine;
import com.journeyOS.core.api.plugins.IPlugins;
import com.journeyOS.core.api.ui.IContainer;
import com.journeyOS.core.base.BaseFragment;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;
import com.journeyOS.plugins.scene.SceneFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class LabFragment extends BaseFragment {

    static Activity mContext;

    @BindView(R2.id.autoHideInGame)
    SettingSwitch mAutoHideInGame;
    @BindView(R2.id.gameScene)
    SettingView mGameScene;

    @BindView(R2.id.autoHideInVideo)
    SettingSwitch mAutoHideInVideo;
    @BindView(R2.id.videoScene)
    SettingView mVideoScene;

    public static Fragment newInstance(Activity activity) {
        LabFragment fragment = new LabFragment();
        mContext = activity;
        return fragment;
    }

    @Override
    public int attachLayoutRes() {
        return R.layout.fragment_lab;
    }

    @Override
    public void initBeforeView() {
        super.initBeforeView();
    }

    @Override
    public void initViews() {
        int flags = SpUtils.getInstant().getInt(Constant.AUTO_HIDE_BALL, Constant.AUTO_HIDE_BALL_DEFAULT);
        if ((flags & Constant.AUTO_HIDE_BALL_GAME) != 0) {
            mAutoHideInGame.setCheckedImmediately(true);
        }

        if ((flags & Constant.AUTO_HIDE_BALL_VIDEO) != 0) {
            mAutoHideInVideo.setCheckedImmediately(true);
        }
    }

    @OnClick({R2.id.autoHideInGame})
    public void listenerAutoHideInGame() {
        int flags = SpUtils.getInstant().getInt(Constant.AUTO_HIDE_BALL, Constant.AUTO_HIDE_BALL_DEFAULT);
        if ((flags & Constant.AUTO_HIDE_BALL_GAME) == 0) {
            mAutoHideInGame.setCheck(true);
            SpUtils.getInstant().put(Constant.AUTO_HIDE_BALL, flags + Constant.AUTO_HIDE_BALL_GAME);
        } else {
            mAutoHideInGame.setCheck(false);
            SpUtils.getInstant().put(Constant.AUTO_HIDE_BALL, flags - Constant.AUTO_HIDE_BALL_GAME);
        }
    }

    @OnClick({R2.id.gameScene})
    public void listenerGameScene() {
        Fragment fragment = CoreManager.getDefault().getImpl(IPlugins.class).provideSceneFragment(mContext, SceneFragment.SCENE_GAME);
        CoreManager.getDefault().getImpl(IContainer.class).subWithMenuActivity(mContext, fragment, mContext.getString(R.string.game_scene_revise));
    }

    @OnClick({R2.id.autoHideInVideo})
    public void listenerAutoHideInVideo() {
        int flags = SpUtils.getInstant().getInt(Constant.AUTO_HIDE_BALL, Constant.AUTO_HIDE_BALL_DEFAULT);
        if ((flags & Constant.AUTO_HIDE_BALL_VIDEO) == 0) {
            mAutoHideInVideo.setCheck(true);
            SpUtils.getInstant().put(Constant.AUTO_HIDE_BALL, flags + Constant.AUTO_HIDE_BALL_VIDEO);
        } else {
            mAutoHideInVideo.setCheck(false);
            SpUtils.getInstant().put(Constant.AUTO_HIDE_BALL, flags - Constant.AUTO_HIDE_BALL_VIDEO);
        }
    }

    @OnClick({R2.id.videoScene})
    public void listenerVideoScene() {
        Fragment fragment = CoreManager.getDefault().getImpl(IPlugins.class).provideSceneFragment(mContext, SceneFragment.SCENE_VIDEO);
        CoreManager.getDefault().getImpl(IContainer.class).subWithMenuActivity(mContext, fragment, mContext.getString(R.string.video_scene_revise));
    }
}
