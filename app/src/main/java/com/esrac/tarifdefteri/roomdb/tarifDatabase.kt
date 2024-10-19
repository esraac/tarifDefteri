package com.esrac.tarifdefteri.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.esrac.tarifdefteri.model.tarif

@Database(entities = [tarif::class], version = 1)
abstract class tarifDatabase : RoomDatabase() {
    abstract fun tarifDAO(): tarifDAO

}