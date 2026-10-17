package com.example.primerproyecto.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.primerproyecto.data.local.entities.PlataformaEntity
import com.example.primerproyecto.data.local.entities.SesionJuegoEntity
import com.example.primerproyecto.data.local.entities.VideojuegoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VideojuegoDao {

    @Query("SELECT * FROM usuarios WHERE (correo = :identificador OR nombre_usuario = :identificador) AND password_hash = :password LIMIT 1")
    suspend fun loginUsuario(identificador: String, password: String): com.example.primerproyecto.data.local.entities.UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE correo = :correo OR nombre_usuario = :nombreUsuario LIMIT 1")
    suspend fun buscarUsuarioExistente(nombreUsuario: String, correo: String): com.example.primerproyecto.data.local.entities.UsuarioEntity?
    @Query ("SELECT * FROM videojuegos WHERE usuario_id = :usuarioId")
    fun getVideojuegosPorUsuario(usuarioId: Int): Flow<List<VideojuegoEntity>>

    @Query("SELECT * FROM videojuegos WHERE id = :id LIMIT 1")
    fun getVideojuegoPorId(id: Int): Flow<VideojuegoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVideojuego(videojuego: VideojuegoEntity)

    @Update
    suspend fun actualizarVideojuego(videojuego: VideojuegoEntity)

    @Delete
    suspend fun eliminarVideojuego(videojuego: VideojuegoEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarUsuarioBase(usuario: com.example.primerproyecto.data.local.entities.UsuarioEntity)
}

@Dao
interface PlataformaDao {
    @Query("SELECT * FROM plataformas")
    fun getAllPlataformas(): Flow<List<PlataformaEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarPlataforma(plataforma: PlataformaEntity)

    @Delete
    suspend fun eliminarPlataforma(plataforma: PlataformaEntity)
}

@Dao
interface SesionJuegoDao {
    @Query("SELECT * FROM sesiones_juego WHERE videojuego_id = :videojuegoId ORDER BY fecha_sesion DESC")
    fun getSesionesPorVideojuego(videojuegoId: Int): Flow<List<SesionJuegoEntity>>

    @Query("SELECT SUM(minutos_jugados) FROM sesiones_juego WHERE videojuego_id = :videojuegoId")
    fun getTotalMinutosPorVideojuego(videojuegoId: Int): Flow<Int?>

    @Insert
    suspend fun registrarSesion(sesion: SesionJuegoEntity)
}