package com.bashirli.kotlinlocationbook.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bashirli.kotlinlocationbook.model.Data

@Database(entities = [Data::class], version = 1)
abstract class RoomDB : RoomDatabase() {
    abstract fun getDAO():RoomDAO
}