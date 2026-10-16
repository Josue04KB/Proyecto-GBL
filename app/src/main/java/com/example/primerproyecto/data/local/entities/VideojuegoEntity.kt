package com.example.primerproyecto.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

//Tabla maestra 1, la de los juegos
@Entity(
    tableName = "videojuegos",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuario_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class VideojuegoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "usuario_id") val usuarioId: Int,
    val titulo: String,
    val genero: String,
    val estado: String
)