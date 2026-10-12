package com.example.primerproyecto

import android.app.Application
import com.example.primerproyecto.di.AppContainer
import com.example.primerproyecto.di.DefaultAppContainer


class GameBacklogApplication : Application() {
    //instancia global del contenedor de inyeccion
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}