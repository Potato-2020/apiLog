package com.potato.tools.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.potato.tools.db.dao.ApiLogDao
import com.potato.tools.db.entity.ApiLogEntity

/**
 * create by Potato
 * create time 2020/5/30
 * Description：创建数据库
 */
@Database(entities = arrayOf(ApiLogEntity::class), version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun apiLogDao(): ApiLogDao//获得接口日志工具

    companion object {
        lateinit var INSTANCE: AppDatabase
        fun instance(context: Context): AppDatabase {
            INSTANCE = Room.databaseBuilder(context, AppDatabase::class.java, "AppDataBase").build()
            return INSTANCE
        }
    }

    /**
     * 关闭链接
     */
    override fun close() {
        try {
            INSTANCE?.close()
        } catch (e: Exception) {

        }

    }

}