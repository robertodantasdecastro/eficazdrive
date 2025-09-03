package dev.eficazdrive.app.core

import java.util.concurrent.atomic.AtomicBoolean

/**
 * PT-BR: Estado simples e thread-safe do serviço para monitoramento pela UI.
 * EN: Simple thread-safe service state for UI monitoring.
 */
object ServiceStatus {
    private val _running = AtomicBoolean(false)
    val isRunning: Boolean get() = _running.get()

    fun setRunning(value: Boolean) {
        _running.set(value)
    }
}



