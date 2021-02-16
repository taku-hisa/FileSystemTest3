package com.example.filesystemtest3

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.filesystemtest3.data.entity.item
import com.example.filesystemtest3.data.service.itemDao
import com.example.filesystemtest3.data.service.itemRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application): AndroidViewModel(application){
    private val dao: itemDao
    init {
        val db = itemRoomDatabase.buildDatabase(application) // DBにアクセスするclassで一度だけDBをビルドする
        dao = db.itemDao() // 使用するDaoを指定
    }

    val getItem = dao.getItem()

    //Itemを保存する
    fun insertItem(item:item) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertItem(item)
        }
    }

    //DBを全消去する
    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteAll()
        }
    }
}