package com.esrac.tarifdefteri.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.esrac.tarifdefteri.model.tarif
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface tarifDAO {
    @Query("SELECT * FROM tarif")
    fun getAll(): Flowable<List<tarif>>

    @Query("SELECT * FROM tarif WHERE id= :id ")
    fun findById(id:Int): Flowable<tarif>

    @Insert
    fun insert(tarif: tarif): Completable

    @Delete
    fun delete(tarif: tarif): Completable


}