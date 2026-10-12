package com.example.primerproyecto.data.repository

import com.example.primerproyecto.data.local.dao.PlataformaDao
import com.example.primerproyecto.data.local.dao.SesionJuegoDao
import com.example.primerproyecto.data.local.dao.VideojuegoDao
import com.example.primerproyecto.data.local.entities.PlataformaEntity
import com.example.primerproyecto.data.local.entities.SesionJuegoEntity
import com.example.primerproyecto.data.local.entities.VideojuegoEntity
import kotlinx.coroutines.flow.Flow

class GameRepositoryImpl(
    private val videojuegoDao: VideojuegoDao,
    private val plataformaDao: PlataformaDao,
    private val sesionJuegoDao: SesionJuegoDao
) : GameRepository {

    override fun getVideojuegosStream(usuarioId: Int): Flow<List<VideojuegoEntity>> =
        videojuegoDao.getVideojuegosPorUsuario(usuarioId)

    override fun getPlataformasStream(): Flow<List<PlataformaEntity>> =
        plataformaDao.getAllPlataformas()

    override fun getSesionesStream(videojuegoId: Int): Flow<List<SesionJuegoEntity>> =
        sesionJuegoDao.getSesionesPorVideojuego(videojuegoId)

    override fun getTotalMinutosStream(videojuegoId: Int): Flow<Int?> =
        sesionJuegoDao.getTotalMinutosPorVideojuego(videojuegoId)

    override suspend fun insertarVideojuego(videojuego: VideojuegoEntity) =
        videojuegoDao.insertarVideojuego(videojuego)

    override suspend fun actualizarVideojuego(videojuego: VideojuegoEntity) =
        videojuegoDao.actualizarVideojuego(videojuego)

    override suspend fun insertarPlataforma(plataforma: PlataformaEntity) =
        plataformaDao.insertarPlataforma(plataforma)

    override suspend fun registrarSesion(sesion: SesionJuegoEntity) =
        sesionJuegoDao.registrarSesion(sesion)
}