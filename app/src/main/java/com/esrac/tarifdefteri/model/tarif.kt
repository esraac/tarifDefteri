package com.esrac.tarifdefteri.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity

data class tarif (
    @ColumnInfo(name="isim")
    var isim: String? = null,
    @ColumnInfo(name="malzeme")
    var malzeme: String? = null,
    @ColumnInfo(name="gorsel")
    var gorsel : ByteArray,
    @ColumnInfo(name="tarif")
    var tarif: String? = null
){
    @PrimaryKey(autoGenerate = true)
    var id=0
    //override fun toString(): String {
       // return "Tarif(id=$id, isim='$isim', malzeme='$malzeme', tarif='$tarif')"
    }

