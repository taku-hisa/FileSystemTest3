package com.example.filesystemtest3.data.service

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.filesystemtest3.data.entity.item

//Entityの指定
@Database(entities = [item::class], version = 1)
abstract class itemRoomDatabase: RoomDatabase() {
    //DAOの指定
    abstract fun itemDao() : itemDao
    //DBのビルド
    companion object {
        fun buildDatabase(context: Context): itemRoomDatabase {
            return Room.databaseBuilder(
                context,
                itemRoomDatabase::class.java, "item_db"
            ).build()
        }
    }
}