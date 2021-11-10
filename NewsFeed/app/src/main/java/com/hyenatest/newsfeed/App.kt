package com.hyenatest.newsfeed

import android.app.Application
import org.kodein.di.DI

open class App : Application() {
    companion object {
        lateinit var di: DI
    }

    override fun onCreate() {
        super.onCreate()
    }

    fun setUpDi(someMoreDI: () -> List<DI.Module>) {
        di = DI {
            someMoreDI.invoke().forEach {
                import(it)
            }
        }
    }

}