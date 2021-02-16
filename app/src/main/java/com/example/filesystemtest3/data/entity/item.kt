package com.example.filesystemtest3.data.entity

import androidx.annotation.NonNull
import androidx.room.*

//Entity
@Entity(tableName = "item_table")
data class item(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo val category: String,
    @ColumnInfo val image: ByteArray,
    @ColumnInfo val detail: String
)