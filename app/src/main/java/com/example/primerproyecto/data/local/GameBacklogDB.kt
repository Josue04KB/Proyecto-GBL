package com.example.primerproyecto.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.primerproyecto.data.local.dao.PlataformaDao
import com.example.primerproyecto.data.local.dao.SesionJuegoDao
import com.example.primerproyecto.data.local.dao.VideojuegoDao
import com.example.primerproyecto.data.local.entities.PlataformaEntity
import com.example.primerproyecto.data.local.entities.SesionJuegoEntity
import com.example.primerproyecto.data.local.entities.UsuarioEntity
import com.example.primerproyecto.data.local.entities.VideojuegoEntity

@Database(
    entities = [
        UsuarioEntity::class,
        VideojuegoEntity::class,
        PlataformaEntity::class,
        SesionJuegoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GameBacklogDB : RoomDatabase(){

    abstract fun videojuegoDao(): VideojuegoDao
    abstract fun plataformaDao(): PlataformaDao
    abstract fun sesionJuegoDao(): SesionJuegoDao

    companion object{
        @Volatile
        private var INSTANCE: GameBacklogDB? = null

        fun  getDatabase (context: Context): GameBacklogDB {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameBacklogDB::class.java,
                    "gamebacklog_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}