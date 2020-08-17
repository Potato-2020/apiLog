package com.potato.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * create by Potato
 * create time 2020/8/17
 * Description：接受到广播
 */
public abstract class ReceiverPotato extends BroadcastReceiver {

    public static final String ACTION = "com.potato.BroadcastDebug";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == intent) return;
        //处理广播
        if (ACTION.equals(intent.getAction())) {
            String baseUrl = intent.getStringExtra("baseUrl");
            String imageUrl = intent.getStringExtra("imageUrl");
            String h5 = intent.getStringExtra("h5");
            if (!TextUtils.isEmpty(baseUrl)) {
                changeBaseUrl(baseUrl);
            }
            if (!TextUtils.isEmpty(imageUrl)) {
                changeImageUrl(imageUrl);
            }
            if (!TextUtils.isEmpty(h5)) {
                openWebView(h5);
            }
        }
    }

    /**
     * 修改baseUrl
     * @param baseUrl host
     */
    public abstract void changeBaseUrl(String baseUrl);

    /**
     *
     * 修改imamgeUrl
     * @param imageUrl host
     */
    public abstract void changeImageUrl(String imageUrl);

    /**
     * 打开H5调试界面
     * @param h5 h5
     */
    public abstract void openWebView(String h5);

}
