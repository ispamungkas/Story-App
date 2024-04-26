package com.example.submissionaplikasistory.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<RemoteKeys>)

    @Query("SELECT * FROM remotekeys WHERE id = :id")
    suspend fun getRemoteById(id: String): RemoteKeys?

    @Query("DELETE FROM remotekeys")
    suspend fun delete()

}