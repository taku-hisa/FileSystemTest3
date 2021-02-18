package com.example.filesystemtest3.data.service

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.filesystemtest3.data.entity.item

@Dao
interface itemDao {                 //原則インターフェースを使います
                                    //インターフェースとは、コンセントのようなものです
                                    //たとえばDBをテストするときは、テスト用のインターフェースに付け替えます
    //戻り値として指定したLiveDataが監視対象となる
    @Query("SELECT * FROM item_table")
    fun getItem(): LiveData<List<item>>

    //@Query("SELECT * FROM item_table WHERE category = :Category")
    //fun getItem(Category : String): LiveData<List<item>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem (item:item)

    @Query("DELETE FROM item_table")
    suspend fun deleteAll()

}