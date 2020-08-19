package com.potato.apilogvisual

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.potato.base.ApiLogBaseAc
import com.potato.tools.ToolbarTools
import kotlinx.android.synthetic.main.activity_api_log_details.*

/**
 * create by Potato
 * create time 2020/5/30
 * Description：接口日志详情
 */
class ActivityApiLogDetails : ApiLogBaseAc(),
        TextView.OnEditorActionListener,
        TextWatcher {

    private var jsonFormat = ""
    private var requestFormat = ""
    private var headerFormat = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api_log_details)
        ToolbarTools().inject(this, "接口详情")
        initView()
    }

    private fun initView() {
        val intent = intent ?: return
        name_apiLogDetails.text = intent.getStringExtra("name")
        jsonFormat = JsonFormatUtils.formatJson(intent.getStringExtra("json"))
        requestFormat = JsonFormatUtils.formatJson(intent.getStringExtra("request"))
        headerFormat = JsonFormatUtils.formatJson(intent.getStringExtra("header"))
        json_apiDetails.text = jsonFormat
        request_apiDetails.text = requestFormat
        header_apiDetails.text = headerFormat
        content_apiDetails.imeOptions = EditorInfo.IME_ACTION_SEARCH
        content_apiDetails.setOnEditorActionListener(this)
        content_apiDetails.addTextChangedListener(this)
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH || event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) {
            val name = content_apiDetails.text.toString().trim()
            if (name == "") {
                Toast.makeText(this, "搜索不能为空", Toast.LENGTH_SHORT).show()
            } else {
                //完全匹配
                mathAll(name)
            }
            hideKeyboard()
            return true
        }
        return false
    }

    //完全匹配
    private fun mathAll(name: String) {
        val allList = KMPUtils.kmpIndexAll(json_apiDetails.text.toString(), name)
        if (allList.isNotEmpty()) {
            for (i in allList) {
                drawColor(i, i + name.length)
            }
        }
    }

    //匹配内容上色
    private fun drawColor(startIndex: Int, endIndex: Int) {
        if (startIndex < 0 || endIndex < 0 || endIndex < startIndex) return
        val style = SpannableStringBuilder()
        style.append(json_apiDetails.text)
        style.setSpan(ForegroundColorSpan(Color.GREEN), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        json_apiDetails.text = style
    }

    /**
     * 隐藏输入法
     */
    private fun hideKeyboard() {
        val imm = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val content = s.toString().trim()
        if (content.isEmpty()) {
            json_apiDetails.text = jsonFormat
        }
    }

}
