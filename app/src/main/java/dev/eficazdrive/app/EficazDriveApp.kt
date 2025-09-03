package dev.eficazdrive.app

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class EficazDriveApp : Application() {
    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
}


