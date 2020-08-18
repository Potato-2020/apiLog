package com.potato.tools.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * create by Potato
 * create time 2020/8/15
 * Description：数据库实体类：存储网络数据
 */
@Entity(tableName = "apiLogEntity")
class ApiLogEntity {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0//主键，自增

    @ColumnInfo(name = "name")
    var name: String? = null//接口名称

    @ColumnInfo(name = "time")
    var time: String? = null//时间

    @ColumnInfo(name = "request")
    var request: String? = null//请求体

    @ColumnInfo(name = "header")
    var header: String? = null//请求头

    @ColumnInfo(name = "json")
    var json: String? = null//json

    @ColumnInfo(name = "status")
    var status: String? = null//请求状态

    override fun toString(): String {
        return "ApiLogEntity(id=$id, name=$name, time=$time, request=$request, json=$json, status=$status)"
    }


}