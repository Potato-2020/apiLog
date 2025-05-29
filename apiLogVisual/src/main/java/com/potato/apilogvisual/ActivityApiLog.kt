package com.potato.apilogvisual

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.potato.base.ApiLogBaseAc
import com.potato.tools.ToolbarTools
import com.potato.tools.db.AppDatabase
import com.potato.tools.db.DBHandler
import com.potato.tools.db.createFlowable
import com.potato.tools.db.dbSubscribe
import com.potato.tools.db.entity.ApiLogEntity
import org.reactivestreams.Subscription

class ActivityApiLog : ApiLogBaseAc(),
    BaseQuickAdapter.OnItemClickListener,
    TextView.OnEditorActionListener {

    private var adapterApiLog: AdapterActivityApiLog? = null
    private var subscription1: Subscription? = null//订阅1
    private var subscription2: Subscription? = null//订阅2
    private lateinit var recyclerView_apiLog: RecyclerView
    private lateinit var name_searchApi: EditText
    private lateinit var tv_size: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api_log)
        recyclerView_apiLog = findViewById(R.id.recyclerView_apiLog)
        name_searchApi = findViewById(R.id.name_searchApi)
        tv_size = findViewById(R.id.tv_size)
        initView()
    }

    private fun initView() {
        ToolbarTools().inject(this, "接口日志")
        recyclerView_apiLog.layoutManager = LinearLayoutManager(this)
        name_searchApi.imeOptions = EditorInfo.IME_ACTION_SEARCH
        name_searchApi.setOnEditorActionListener(this)
        queryAll()
    }

    //查询全部接口
    private fun queryAll() {
        createFlowable(object : DBHandler<List<ApiLogEntity>>() {
            override fun process(): List<ApiLogEntity>? {
                return AppDatabase.instance(this@ActivityApiLog).apiLogDao().queryAll()
            }
        }).dbSubscribe({
            if (it.isNotEmpty()) notifyView(it)
        }, {}, {
            subscription1 = it
        })
    }

    //搜索接口
    private fun queryByName(name: String) {
        createFlowable(object : DBHandler<List<ApiLogEntity>>() {
            override fun process(): List<ApiLogEntity>? {
                return AppDatabase.instance(this@ActivityApiLog).apiLogDao().queryLikeName(name)
            }
        }).dbSubscribe({
            if (it.isNotEmpty()) notifyView(it)
        }, {}, {
            subscription2 = it
        })
    }

    private fun notifyView(list: List<ApiLogEntity>) {
        tv_size.text = "共统计了${list.size}个接口"
        if (adapterApiLog == null) {
            adapterApiLog = AdapterActivityApiLog(list)
            recyclerView_apiLog.adapter = adapterApiLog
            adapterApiLog?.onItemClickListener = this
        } else {
            adapterApiLog?.setNewData(list)
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        val apiLogEntity = adapter?.getItem(position) as ApiLogEntity
        if (apiLogEntity.name!!.contains("jpeg") || apiLogEntity.name!!.contains("png")) {
//            RouteManager.openActivityWeb(apiLogEntity.name, "这是一张图片")
            return
        }
        val intent = Intent(this, ActivityApiLogDetails::class.java)
        intent.putExtra("name", apiLogEntity.name)
        intent.putExtra("json", apiLogEntity.json)
        intent.putExtra("request", apiLogEntity.request)
        intent.putExtra("header", apiLogEntity.header)
        startActivity(intent)
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH || event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) {
            val name = name_searchApi.text.toString().trim()
            if (name == "") {
                queryAll()
            } else {
                queryByName(name)
            }
            hideKeyboard()
            return true
        }
        return false
    }

    /**
     * 隐藏输入法
     */
    private fun hideKeyboard() {
        val imm =
            applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    //取消订阅
    override fun onDestroy() {
        super.onDestroy()
        subscription1?.cancel()
        subscription2?.cancel()
    }
}