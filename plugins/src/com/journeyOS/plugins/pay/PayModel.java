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

package com.journeyOS.plugins.pay;

import android.arch.lifecycle.MutableLiveData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.journeyOS.base.R;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.viewmodel.BaseViewModel;
import com.journeyOS.plugins.pay.adapter.PayInfoData;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class PayModel extends BaseViewModel {
    private static final String TAG = PayModel.class.getSimpleName();
    private Context mContext;
    private MutableLiveData<List<PayInfoData>> mPayInfoData = new MutableLiveData<>();

    public static final String ALIPAY_PKG = "com.eg.android.AlipayGphone";
    public static final String TENCENT_MM_PKG = "com.tencent.mm";

    public static final String ALIPAY_SCAN = "alipay_scan";
    public static final String ALIPAY_QRCODE = "alipay_qrcode";
    public static final String TENCENT_MM_SCAN = "tencent_mm_scan";
    public static final String TENCENT_MM_QRCODE = "tencent_mm_qrcode";

    @Override
    protected void onCreate() {
        mContext = CoreManager.getDefault().getContext();
    }

    public void getPayApps() {
        List<PayInfoData> infoDatas = new ArrayList<>();

        infoDatas.add(new PayInfoData(mContext.getDrawable(R.mipmap.alipay_scan),
                mContext.getString(R.string.alipay_scan), ALIPAY_SCAN));

        infoDatas.add(new PayInfoData(mContext.getDrawable(R.mipmap.alipay_paycode),
                mContext.getString(R.string.alipay_barcode), ALIPAY_QRCODE));

        infoDatas.add(new PayInfoData(mContext.getDrawable(R.mipmap.wechat_scan),
                mContext.getString(R.string.wechat_scan), TENCENT_MM_SCAN));

        mPayInfoData.postValue(infoDatas);
    }

    public static Intent alipayScan() {
        try {
            String uri = "alipayqr://platformapi/startapp?saId=10000007";
            Intent intent = Intent.parseUri(uri, Intent.URI_INTENT_SCHEME);
            intent.setPackage(ALIPAY_PKG);
            return intent;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Intent alipayBarcode() {
        try {
            String uri = "alipayqr://platformapi/startapp?saId=20000056";
            Intent intent = Intent.parseUri(uri, Intent.URI_INTENT_SCHEME);
            intent.setPackage(ALIPAY_PKG);
            return intent;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Intent weChatScan() {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"));
            intent.putExtra("LauncherUI.From.Scaner.Shortcut", true);
            intent.setFlags(335544320);
            intent.setAction("android.intent.action.VIEW");
            intent.setPackage(TENCENT_MM_PKG);
            return intent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public MutableLiveData<List<PayInfoData>> getAllPayData() {
        return mPayInfoData;
    }
}
