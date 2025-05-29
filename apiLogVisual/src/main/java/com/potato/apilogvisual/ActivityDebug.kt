package com.potato.apilogvisual

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.potato.base.ApiLogBaseAc
import com.potato.tools.InputPopWindow
import com.potato.tools.ToolbarTools

class ActivityDebug : ApiLogBaseAc(),
    View.OnClickListener {
    private lateinit var tv_url: TextView
    private lateinit var tv_img_url: TextView
    private lateinit var tv_version: TextView
    private lateinit var linear_url: LinearLayout
    private lateinit var linear_img_url: LinearLayout
    private lateinit var linear_log: LinearLayout
    private lateinit var linear_h5: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)
        tv_url = findViewById(R.id.tv_url)
        tv_img_url = findViewById(R.id.tv_img_url)
        tv_version = findViewById(R.id.tv_version)
        linear_url = findViewById(R.id.linear_url)
        linear_img_url = findViewById(R.id.linear_img_url)
        linear_log = findViewById(R.id.linear_log)
        linear_h5 = findViewById(R.id.linear_h5)
        ToolbarTools().inject(this, "Debug调试")
        initView()
    }

    private fun initView() {
        tv_url.text = intent.getStringExtra("baseUrl")
        tv_img_url.text = intent.getStringExtra("baseImageUrl")
        tv_version.text = intent.getStringExtra("versionName")
        linear_url.setOnClickListener(this)
        linear_img_url.setOnClickListener(this)
        linear_log.setOnClickListener(this)
        linear_h5.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.linear_url -> {//BaseUrl
                showPoP(v.id, tv_url.text.toString(), "https://...")
            }

            R.id.linear_img_url -> {//BaseImageUrl
                showPoP(v.id, tv_img_url.text.toString(), "https://...")
            }

            R.id.linear_h5 -> {
                //H5测试
                showPoP(v.id, "", "https://...")
            }

            R.id.linear_log -> {
                //接口记录
                startActivity(Intent(this, ActivityApiLog::class.java))
            }
        }
    }

    // 弹框
    private fun showPoP(viewId: Int, text: String, mHint: String) {
        val inputPopWindow =
            InputPopWindow(
                this,
                mHint,
                "取消",
                "确定"
            )
        inputPopWindow.setmItemsOnClick(object : InputPopWindow.ItemsOnClick {
            override fun onLeftClick(mInput: String, flag: Any) {
                inputPopWindow.dismiss()
            }

            override fun onRightClick(mInput: String, flag: Any) {
                inputPopWindow.dismiss()
                process(viewId, mInput)
            }
        })
        inputPopWindow.show(text, mHint, viewId)
    }

    //更改配置
    private fun process(viewId: Int, content: String) {
        when (viewId) {
            R.id.linear_url -> {//BaseUrl
                tv_url.text = content
                //发送一个广播(告知app，baseUrl变了)
                val intent = Intent("com.potato.BroadcastDebug")
                intent.putExtra("baseUrl", content)
                sendBroadcast(intent)
            }

            R.id.linear_img_url -> {//BaseImageUrl
                tv_img_url.text = content
                //发送一个广播(告知app，imageUrl变了)
                val intent = Intent("com.potato.BroadcastDebug")
                intent.putExtra("imageUrl", content)
                sendBroadcast(intent)
            }

            R.id.linear_h5 -> {
                //发送一个广播(告知app，imageUrl变了)
                val intent = Intent("com.potato.BroadcastDebug")
                intent.putExtra("h5", content)
                sendBroadcast(intent)
            }
        }
    }

}
