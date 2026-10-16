package com.example.primerproyecto.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

//Tabla Transaccional
@Entity(
    tableName = "sesiones_juego",
    foreignKeys = [
        ForeignKey(
            entity = VideojuegoEntity::class,
            parentColumns = ["id"],
            childColumns = ["videojuego_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PlataformaEntity::class,
            parentColumns = ["id"],
            childColumns = ["plataforma_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class SesionJuegoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "videojuego_id") val videojuegoId: Int,
    @ColumnInfo(name = "plataforma_id") val plataformaId: Int,
    @ColumnInfo(name = "fecha_sesion") val fechaSesion: Long,
    @ColumnInfo(name = "minutos_jugados") val minutosJugados: Int,
    val notas: String
)