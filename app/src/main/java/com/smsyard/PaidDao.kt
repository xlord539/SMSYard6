package com.smsyard
import androidx.room.*
@Dao
interface PaidDao{
    @Insert(onConflict=OnConflictStrategy.IGNORE)
    suspend fun insert(p:PaidMessage)

    @Query("SELECT smsId FROM paid_messages")
    suspend fun allIds():List<Long>
}
