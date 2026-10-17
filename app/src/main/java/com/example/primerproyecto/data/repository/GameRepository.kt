package com.example.primerproyecto.data.repository

import com.example.primerproyecto.data.local.entities.PlataformaEntity
import com.example.primerproyecto.data.local.entities.SesionJuegoEntity
import com.example.primerproyecto.data.local.entities.VideojuegoEntity
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    //Lecturas reactivas
    fun getVideojuegosStream(usuarioId: Int): Flow<List<VideojuegoEntity>>
    fun getPlataformasStream(): Flow<List<PlataformaEntity>>
    fun getSesionesStream(videojuegoId: Int): Flow<List<SesionJuegoEntity>>
    fun getTotalMinutosStream(videojuegoId: Int): Flow<Int?>

    //Operaciones de escritura
    suspend fun loginUsuario(identificador: String, password: String): com.example.primerproyecto.data.local.entities.UsuarioEntity?
    suspend fun registrarUsuario(nombreUsuario: String, correo: String, password: String): Result<com.example.primerproyecto.data.local.entities.UsuarioEntity>
    suspend fun insertarVideojuego(videojuego: VideojuegoEntity)
    suspend fun actualizarVideojuego(videojuego: VideojuegoEntity)
    suspend fun insertarPlataforma(plataforma: PlataformaEntity)
    suspend fun registrarSesion(sesion: SesionJuegoEntity)
    suspend fun asegurarDatosIniciales()

    fun getVideojuegoPorIdStream(id: Int): Flow<VideojuegoEntity?>
    suspend fun eliminarVideojuego(videojuego: VideojuegoEntity)
    suspend fun eliminarPlataforma(plataforma: PlataformaEntity)
}