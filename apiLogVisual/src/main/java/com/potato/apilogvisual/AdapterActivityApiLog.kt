package com.potato.apilogvisual

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.potato.tools.db.entity.ApiLogEntity

/**
 * create by Potato
 * create time
 * Description：recyclerView配置器
 */
class AdapterActivityApiLog(var list: List<ApiLogEntity>) :
        BaseQuickAdapter<ApiLogEntity, BaseViewHolder>(R.layout.item_api_log, list) {
    override fun convert(helper: BaseViewHolder?, item: ApiLogEntity?) {
        if (helper == null || item == null) return
        helper.setText(R.id.name_apiLog, item.name)
        helper.setText(R.id.status_apiLog, item.status)
        helper.setText(R.id.time_apiLog, item.time)
        if (helper.adapterPosition == list.size - 1)
            helper.getView<View>(R.id.line_apiLog).visibility = View.GONE
    }

    override fun setNewData(data: List<ApiLogEntity>?) {
        super.setNewData(data)
        list = data!!
    }

}