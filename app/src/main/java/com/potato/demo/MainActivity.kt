package com.potato.demo

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import com.potato.apiannotation.R
import com.potato.apilogvisual.ActivityDebug
import com.potato.base.ApiLogBaseAc
import com.potato.base.ReceiverPotato
import com.potato.tools.DebugManager

class MainActivity : ApiLogBaseAc(), DebugManager.DebugListener {
    private var debugManager : DebugManager? = null
    private var receiverPotato: ReceiverPotato? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //动态注册广播
        val intentFilter = IntentFilter()
        intentFilter.addAction(ReceiverPotato.ACTION)
        receiverPotato = object : ReceiverPotato() {
            override fun changeBaseUrl(baseUrl: String?) {
                Log.e("Potato>>>baseUrl>>>", baseUrl)
            }

            override fun changeImageUrl(imageUrl: String?) {
                Log.e("Potato>>>imageUrl>>>", imageUrl)
            }

            override fun openWebView(h5: String?) {
                Log.e("Potato>>>h5>>>", h5)
            }
        }
        registerReceiver(receiverPotato, intentFilter)
        if (debugManager == null) debugManager = DebugManager()
        if (debugManager != null) debugManager?.setListener(this, this)
    }

    override fun onResume() {
        super.onResume()
        if (debugManager != null) debugManager?.onResume()
    }

    override fun onPause() {
        super.onPause()
        if(debugManager != null) debugManager?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        //解注册
        unregisterReceiver(receiverPotato)
        if(debugManager != null) debugManager?.onDestory()
    }

    override fun debugApiLog() {
        debugManager?.openDebug(
            this,
            "https://www.baidu.com",
            "https://www.ailiuynos.cn",
            "1.0.0");
    }
}