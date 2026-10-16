package com.example.primerproyecto.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//Tabla maestra 2, la de las plataformas
@Entity(tableName = "plataformas")
data class PlataformaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    @ColumnInfo(name = "formato_default") val formatoDefault: String
)