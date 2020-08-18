package com.potato.tools.db.helper

import com.potato.tools.db.AppDatabase
import com.epoch.rupeeLoan.tools.db.DBHandler
import com.epoch.rupeeLoan.tools.db.createFlowable
import com.epoch.rupeeLoan.tools.db.dbSubscribe
import com.potato.tools.db.entity.ApiLogEntity
import org.reactivestreams.Subscription
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale.CHINA

/**
 * create by Potato
 * create time 2020/5/30
 * Description：接口日志记录工具
 */
class ApiDaoHelper {

    companion object {
        private var subscription1: Subscription? = null
        private var subscription2: Subscription? = null
        private var subscription3: Subscription? = null
        //接口日志记录
        fun apiRecord(name: String, code: String, header: String, request: String, json: String) {
            insertDB(apiLogEntity(name, code, header, request, json))
        }

        //获取接口日志实体
        private fun apiLogEntity(name: String, code: String, header: String, request: String, json: String): ApiLogEntity {
            val apiLogEntity = ApiLogEntity()
            apiLogEntity.name = name
            apiLogEntity.status = when {
                code.startsWith("2") -> "请求成功$code"
                code.startsWith("3") -> "请求重定向$code"
                else -> "请求失败$code"
            }
            apiLogEntity.time = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", CHINA).format(Date())
            apiLogEntity.json = json
            apiLogEntity.request = request
            apiLogEntity.header = header
            return apiLogEntity
        }

        //插入数据库
        private fun insertDB(entity: ApiLogEntity) {
            if (entity.name != null) {
                createFlowable(object : DBHandler<List<ApiLogEntity>>() {
                    override fun process(): List<ApiLogEntity>? {
                        return AppDatabase.INSTANCE.apiLogDao().queryByName(entity.name)
                    }
                }).dbSubscribe({
                    if (it.isNotEmpty()) {
                        //更新数据库
                        entity.id = it[0].id
                        realUpdate(entity)
                    } else {
                        //插入数据
                        realInsert(entity)
                    }
                }, {}, {
                    subscription1 = it
                })
            }
        }

        //更新数据
        private fun realUpdate(entity: ApiLogEntity) {
            createFlowable(object : DBHandler<Int>() {
                override fun process(): Int? {
                    return AppDatabase.INSTANCE.apiLogDao().update(entity)
                }
            }).dbSubscribe({
//                Log.e("Potato", "更新成功${entity.name}")
            }, {}, {
                subscription2 = it
            })
        }

        //真正得插入数据库
        private fun realInsert(entity: ApiLogEntity) {
            createFlowable(object : DBHandler<Long>() {
                override fun process(): Long? {
                    return AppDatabase.INSTANCE.apiLogDao().insert(entity)
                }
            }).dbSubscribe({
//                Log.e("Potato", "插入成功${entity.name}")
            }, {}, {
                subscription3 = it
            })
        }

        //取消订阅
        fun cancel() {
            if (subscription1 != null) subscription1?.cancel()
            if (subscription2 != null) subscription2?.cancel()
            if (subscription3 != null) subscription3?.cancel()
        }
    }
}