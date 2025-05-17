package org.akhsaul.core.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import org.akhsaul.core.data.source.local.entity.EventEntity


@Database(entities = [EventEntity::class], version = 1, exportSchema = false)
abstract class EventDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}