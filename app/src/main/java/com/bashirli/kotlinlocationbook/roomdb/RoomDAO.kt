package com.bashirli.kotlinlocationbook.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.bashirli.kotlinlocationbook.model.Data
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao

interface RoomDAO {
@Query("SELECT * FROM Data")
fun getAll() : Flowable<List<Data>>

@Insert
fun insert(data: Data): Completable
@Delete
fun delete(data: Data): Completable

}