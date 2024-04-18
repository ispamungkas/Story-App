package com.example.submissionaplikasistory.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DaoService {

    @Query("SELECT * FROM ENTITYDAOSTORY")
    fun getStory() : List<EntityDaoStory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStory(story: EntityDaoStory)

}