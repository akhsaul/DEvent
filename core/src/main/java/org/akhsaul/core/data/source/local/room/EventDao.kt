package org.akhsaul.core.data.source.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.akhsaul.core.data.source.local.entity.EventEntity

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavoriteEvent(event: EventEntity)

    @Delete
    suspend fun deleteFavoriteEvent(event: EventEntity)

    @Query("SELECT * FROM events_table WHERE id = :eventId LIMIT 1")
    suspend fun findFavoriteEvent(eventId: Int): EventEntity?

    @Query("SELECT * FROM events_table")
    fun getAllFavoriteEvents(): LiveData<List<EventEntity>>


    @Query("SELECT * FROM events_table")
    fun getAllFavoriteEvent(): Flow<List<EventEntity>>
}