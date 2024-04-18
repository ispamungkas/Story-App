package com.example.submissionaplikasistory.datasource.local

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class EntityDaoStory(
    @field:PrimaryKey(autoGenerate = true)
    @field:ColumnInfo(name = "id")
    var id: Int = 0,

    @field:ColumnInfo(name = "photoUrl")
    val photoUrl: String,

    @field:ColumnInfo(name = "name")
    val name: String,

    @field:ColumnInfo(name = "description")
    val desc: String
) : Parcelable