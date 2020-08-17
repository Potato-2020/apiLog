package com.potato.tools.db.dao

import androidx.room.*
import com.potato.tools.db.entity.ApiLogEntity
import org.jetbrains.annotations.NotNull

/**
 * create by Potato
 * create time 2020/8/15
 * Description：增删改查：网络数据日志
 */
@Dao
interface ApiLogDao {
    //插入数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(@NotNull entity: ApiLogEntity): Long

    //查询所有数据
    @Query("SELECT * FROM apiLogEntity")
    fun queryAll(): List<ApiLogEntity>

    //查询某条接口数据
    @Query("SELECT * FROM apiLogEntity WHERE name = :name")
    fun queryByName(name: String?): List<ApiLogEntity>

    //查询某条接口数据
    @Query("SELECT * FROM apiLogEntity WHERE name LIKE '%' || :name || '%'")
    fun queryLikeName(name: String?): List<ApiLogEntity>

    //更新数据
    @Update
    fun update(@NotNull entity: ApiLogEntity): Int
}