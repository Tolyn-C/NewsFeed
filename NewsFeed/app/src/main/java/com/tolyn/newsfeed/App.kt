package com.tolyn.newsfeed

import android.app.Application
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.kodein.di.DI

open class App : Application() {
    companion object {
        private val _initializedStateFlow = MutableStateFlow(false)
        val initializedStateFlow: StateFlow<Boolean> get() = _initializedStateFlow
        lateinit var di: DI
        fun setUpDi(someMoreDI: () -> List<DI.Module>) {
            if (initializedStateFlow.value) {
                // prevent from multiple initialization
                return
            }
            di = DI {
                someMoreDI.invoke().forEach {
                    import(it)
                }
            }
            _initializedStateFlow.value = true
        }
    }
}