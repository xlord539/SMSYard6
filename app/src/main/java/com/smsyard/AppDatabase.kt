package com.smsyard
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(entities=[PaidMessage::class], version=1)
abstract class AppDatabase:RoomDatabase(){
    abstract fun dao():PaidDao
    companion object{
        @Volatile private var INSTANCE:AppDatabase?=null
        fun get(ctx:Context)= INSTANCE ?: synchronized(this){
            Room.databaseBuilder(ctx,AppDatabase::class.java,"smsyard.db").build().also{INSTANCE=it}
        }
    }
}
