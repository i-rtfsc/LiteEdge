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

package com.journeyOS.core.pay;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.Singleton;
import com.journeyOS.core.CoreManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

public class PayManager {
    private static final String TAG = PayManager.class.getSimpleName();

    // 支付宝包名
    public static final String ALIPAY_PACKAGE = "com.eg.android.AlipayGphone";

    private static final String INTENT_URL_FORMAT = "intent://platformapi/startapp?saId=10000007&" +
            "clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2F{payCode}%3F_s" +
            "%3Dweb-other&_t=1472443966571#Intent;" +
            "scheme=alipayqr;package=com.eg.android.AlipayGphone;end";

    // 微信包名
    public static final String TENCENT_PACKAGE = "com.tencent.mm";
    // 微信二维码扫描页面地址
    private static final String TENCENT_ACTIVITY_BIZSHORTCUT = "com.tencent.mm.action.BIZSHORTCUT";
    // Extra data
    private static final String TENCENT_EXTRA_ACTIVITY_BIZSHORTCUT = "LauncherUI.From.Scaner.Shortcut";

    private static final String EDGE = "edge";

    private static final String TENCENT_PAY_QR = "soloPayWeixin.png";

    Context mContext;

    private PayManager() {
        mContext = CoreManager.getDefault().getContext();
    }

    private static final Singleton<PayManager> gDefault = new Singleton<PayManager>() {
        @Override
        protected PayManager create() {
            return new PayManager();
        }
    };

    public static PayManager getDefault() {
        return gDefault.get();
    }

    /**
     * 支付宝扫一扫
     */
    public Intent alipayScan() {
        try {
            String uri = "alipayqr://platformapi/startapp?saId=10000007";
            Intent intent = Intent.parseUri(uri, Intent.URI_INTENT_SCHEME);
            intent.setPackage(ALIPAY_PACKAGE);
            return intent;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 支付宝付款码
     */
    public Intent alipayBarcode() {
        try {
            String uri = "alipayqr://platformapi/startapp?saId=20000056";
            Intent intent = Intent.parseUri(uri, Intent.URI_INTENT_SCHEME);
            intent.setPackage(ALIPAY_PACKAGE);
            return intent;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 支付宝乘车码
     */
    public Intent alipayCarcode() {
        try {
            String uri = "alipays://platformapi/startapp?appId=200011235&transparentTitle=auto&url=/www/offline_qrcode.html?cardType=ANT00001&source=shortCut&snapshot=no&canPullDown=NO&showOptionMenu=NO";
            Intent intent = Intent.parseUri(uri, Intent.URI_INTENT_SCHEME);
            intent.setPackage(ALIPAY_PACKAGE);
            return intent;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 微信扫一扫
     */
    public Intent tencentMMScan() {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"));
            intent.putExtra("LauncherUI.From.Scaner.Shortcut", true);
            intent.setFlags(335544320);
            intent.setAction("android.intent.action.VIEW");
            intent.setPackage(TENCENT_PACKAGE);
            return intent;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean payAlipay() {
        return AppUtils.startUri(mContext, INTENT_URL_FORMAT.replace("{payCode}", "FKX01967I8HGU4UAZV7Q0E"));
    }

    /**
     * 手动解析二维码获得地址中的参数，例如 https://qr.alipay.com/FKX01967I8HGU4UAZV7Q0E 最后那段
     */
    public boolean payAlipay(String payCode) {
        return AppUtils.startUri(mContext, INTENT_URL_FORMAT.replace("{payCode}", payCode));
    }

    public void payTencentMM() {
        try {
            InputStream is = mContext.getAssets().open(TENCENT_PAY_QR);
            String qrPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + EDGE + File.separator + TENCENT_PAY_QR;
            LogUtils.d(TAG, "qr path = [" + qrPath + "]");
            PayManager.getDefault().saveQr2SDCard(qrPath, BitmapFactory.decodeStream(is));
            payTencentMM(qrPath);
        } catch (IOException e) {
            LogUtils.e(TAG, "io exception = " + e);
        }
    }

    public void payTencentMM(String qrSavePath) {
        if (TextUtils.isEmpty(qrSavePath)) {
            LogUtils.e(TAG, "can't pay NULL object!");
            return;
        }
        sendPictureStoredBroadcast(mContext, qrSavePath);
        AppUtils.startApp(mContext, tencentMMScan());
    }

    public void saveQr2SDCard(String qrSavePath, Bitmap qrBitmap) {
        if (qrSavePath == null || qrBitmap == null) {
            LogUtils.e(TAG, "can't save NULL object!");
            return;
        }

        File qrFile = new File(qrSavePath);
        File parentFile = qrFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        try {
            FileOutputStream fos = new FileOutputStream(qrFile);
            qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendPictureStoredBroadcast(Context context, String qrSavePath) {
        LogUtils.d(TAG, "send picture stored broadcast, save qr path = [" + qrSavePath + "]");
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(qrSavePath));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

}
