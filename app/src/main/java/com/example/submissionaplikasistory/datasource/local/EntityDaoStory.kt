package com.example.submissionaplikasistory.datasource.local

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class EntityDaoStory(
    val photoUrl: String? = null,

    val createdAt: String? = null,

    val name: String? = null,

    val description: String? = null,

    val lon: Double? = null,

    @PrimaryKey
    val id: String,

    val lat: Double? = null
) : Parcelable