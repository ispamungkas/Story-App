package com.example.submissionaplikasistory.datasource.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DaoService {

    @Query("SELECT * FROM ENTITYDAOSTORY")
    fun getStory() : PagingSource<Int, EntityDaoStory>

    @Query("SELECT * FROM ENTITYDAOSTORY")
    suspend fun getStoryListEntityDaoStory() : List<EntityDaoStory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStory(story: List<EntityDaoStory>)

    @Query("DELETE FROM EntityDaoStory")
    suspend fun deleteAll()

}