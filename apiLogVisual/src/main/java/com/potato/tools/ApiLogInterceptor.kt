package com.potato.tools

import android.content.Context
import com.google.gson.Gson
import com.potato.tools.db.ApiLogUtils
import com.potato.tools.db.helper.ApiDaoHelper
import okhttp3.*
import okio.Buffer
import java.net.URLDecoder
import java.nio.charset.Charset


/**
 * create by Potato
 * create time 2020/8/15
 * Description：拦截器：存储网络相关数据
 */
class ApiLogInterceptor(var context: Context) : Interceptor {
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
                ApiLogUtils.nameStyle(request.url.toString(), true),
                response.code.toString(),
                header,
                requestBody,
                responseJson, context)
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
        result = Gson().toJson(map)
        return result
    }

    private fun getParamKey(request: Request?): String {
        val body = request?.body
        var result = ""
        val httpUrl = request?.url.toString()
        if ("" == httpUrl) return ""
        if ("GET" == request?.method) {
            if (httpUrl.contains("?")) {
                val map = hashMapOf<String, Any>()
                val list = httpUrl.split("?")
                if (list[1].contains("&")) {//多参数
                    val requestList = list[1].split("&")
                    for (i in requestList.indices) {//requestList[i] = xx=xx
                        val key = URLDecoder.decode(requestList[i].split("=")[0], "UTF-8")
                        val value = URLDecoder.decode(requestList[i].split("=")[1], "UTF-8")
                        map[key] = value
                    }
                } else {//一个参数
                    //list[1] = xx=xx
                    val key = URLDecoder.decode(list[1].split("=")[0], "UTF-8")
                    val value = URLDecoder.decode(list[1].split("=")[1], "UTF-8")
                    map[key] = value
                }
                result = Gson().toJson(map)
            }
        } else {
            val buffer = Buffer()
            when (body) {
                is MultipartBody -> {
                    //文件
//                    val copy = body as MultipartBody?
//                    for (part in copy!!.parts) {
//                        part.body.writeTo(buffer)
//                        result = buffer.readUtf8()
//                    }
                    result = "{'表单请求体':'文件流不展示'}"
                }
                is FormBody -> {
                    //表单
                    val map = mutableMapOf<String, Any>()
                    for (i in 0 until body.size) {
                        map[body.name(i)] = body.value(i)
                    }
                    result = Gson().toJson(map)
                }
                else -> {
                    //json
                    body?.writeTo(buffer)
                    result = buffer.readUtf8()
                }
            }
        }
        return result
    }

}