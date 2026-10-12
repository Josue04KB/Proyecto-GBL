package com.example.primerproyecto.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

//Tabla de Autenticación

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "nombre_usuario") val nombreUsuario: String,
    val correo: String,
    @ColumnInfo(name = "password_hash") val passwordHash: String
)

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

//Tabla maestra 2, la de las plataformas
@Entity(tableName = "plataformas")
data class PlataformaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    @ColumnInfo(name = "formato_default") val formatoDefault: String
)

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