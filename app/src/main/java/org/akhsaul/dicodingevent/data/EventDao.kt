package org.akhsaul.dicodingevent.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavoriteEvent(event: Event)

    @Delete
    suspend fun deleteFavoriteEvent(event: Event)

    @Query("SELECT * FROM events_table WHERE id = :eventId LIMIT 1")
    suspend fun findFavoriteEvent(eventId: Int): Event?

    @Query("SELECT * FROM events_table")
    fun getAllFavoriteEvents(): LiveData<List<Event>>
}