package com.potato.tools

import android.app.Activity
import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.toolbar_layout.view.*

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
        initStatusHeight(activity, view.iv_status_bar)
        view.toolbar_back?.setOnClickListener {
            activity.finish()
        }
        view.toolbar_title?.text = title
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