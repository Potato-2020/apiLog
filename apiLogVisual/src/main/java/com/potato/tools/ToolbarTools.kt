package com.potato.tools

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.potato.apilogvisual.R

/**
 * create by Potato
 * create time 2020/8/15
 * Description：toolbar管理
 * xml：toolbar_layout.xml
 */
class ToolbarTools {
    /**
     * 加载公共头
     */
    fun inject(activity: Activity?, title: String) {
        val view = activity!!.window.decorView
        initStatusHeight(activity, view.findViewById<ImageView>(R.id.iv_status_bar))
        view.findViewById<ImageView>(R.id.toolbar_back)?.setOnClickListener {
            activity.finish()
        }
        view.findViewById<TextView>(R.id.toolbar_title)?.text = title
    }

    private fun initStatusHeight(activity: Activity?, view: View?) {
        if (view == null) return
        val statusBarHeight = getStatusBarHeight(activity?.applicationContext!!)
        val layoutParams = view.layoutParams
        layoutParams.height = statusBarHeight
        view.layoutParams = layoutParams
    }

    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    private fun getStatusBarHeight(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }
}