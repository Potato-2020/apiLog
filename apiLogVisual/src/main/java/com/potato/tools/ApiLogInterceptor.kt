package com.potato.tools

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.potato.tools.db.ApiLogUtils
import com.potato.tools.db.helper.ApiDaoHelper
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import java.net.URLDecoder
import java.nio.charset.Charset


/**
 * create by Potato
 * create time 2020/8/15
 * Description：拦截器：存储网络相关数据
 */
class ApiLogInterceptor(
    var context: Context,
    var port: String,
    var recordData: ((String, String, String, String) -> Unit)? = null
) :
    Interceptor {

    private val gson = GsonBuilder()
        .disableHtmlEscaping() // 禁用 HTML 转义（防止 = 变成 \u003d）
        .create()
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            throw e
        }
        try {
            val source = response.body!!.source()
            source.request(java.lang.Long.MAX_VALUE)
            val buffer = source.buffer
            val responseJson = buffer.clone().readString(Charset.forName("UTF-8"))//响应体
            val header = getHeader(request)//请求头
            val requestBody = getParamKey(request)//请求体
            ApiDaoHelper.apiRecord(
                ApiLogUtils.nameStyle(request.url.toString(), true, port),
                response.code.toString(),
                header,
                requestBody,
                responseJson, context
            )
            recordData?.invoke(request.url.toString(), header, requestBody, responseJson)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            return response
        }
    }

    private fun getHeader(request: Request?): String {
        val result: String
        val headers = request?.headers
        val map = HashMap<String, Any>()
        headers?.forEach {
            val key = it.first
            val value = it.second
            map[key] = value
        }
        result = gson.toJson(map)
        return result
    }

    private fun getParamKey(request: Request?): String {
        val body = request?.body
        var result = ""
        val httpUrl = request?.url.toString()
        if (httpUrl.isEmpty()) return ""

        if ("GET" == request?.method) {
            if (httpUrl.contains("?")) {
                val map = hashMapOf<String, Any>()
                val list = httpUrl.split("?")
                if (list[1].contains("&")) { // 多参数
                    list[1].split("&").forEach { param ->
                        val keyValue = param.split("=", limit = 2)
                        if (keyValue.size == 2) {
                            val key = URLDecoder.decode(keyValue[0], "UTF-8")
                            val value = URLDecoder.decode(keyValue[1], "UTF-8")
                            map[key] = value
                        }
                    }
                } else { // 单参数
                    val keyValue = list[1].split("=", limit = 2)
                    if (keyValue.size == 2) {
                        val key = URLDecoder.decode(keyValue[0], "UTF-8")
                        val value = URLDecoder.decode(keyValue[1], "UTF-8")
                        map[key] = value
                    }
                }
                result = gson.toJson(map)
            }
        } else {
            val contentType = body?.contentType()?.toString()?.lowercase()
            when (body) {
                is MultipartBody -> {
                    val partsMap = mutableMapOf<String, Any>()
                    body.parts.forEach { part ->
                        val disposition = part.headers?.get("Content-Disposition")
                        disposition?.let { disp ->
                            val nameMatch = Regex("name=\"([^\"]*)\"").find(disp)
                            val name = nameMatch?.groupValues?.get(1) ?: return@forEach
                            if (Regex("filename=\"([^\"]*)\"").containsMatchIn(disp)) {
                                partsMap[name] = "file" // 标记为文件
                            } else {
                                val buffer = Buffer()
                                part.body.writeTo(buffer)
                                partsMap[name] = buffer.readUtf8()
                            }
                        }
                    }
                    result = gson.toJson(partsMap)
                }
                is FormBody -> {
                    val formMap = (0 until body.size).associate { i ->
                        body.name(i) to body.value(i)
                    }
                    result = gson.toJson(formMap)
                }
                else -> {
                    try {
                        val buffer = Buffer()
                        body?.writeTo(buffer)
                        result = buffer.readUtf8()
                        // 如果是 JSON，尝试解析并重新序列化
                        if (contentType?.contains("application/json") == true) {
                            val jsonObj = gson.fromJson(result, Any::class.java)
                            result = gson.toJson(jsonObj)
                        }
                    } catch (e: Exception) {
                        result = "{ \"error\": \"failed to parse body\", \"type\": \"${contentType ?: "unknown"}\" }"
                    }
                }
            }
        }
        return result
    }
}