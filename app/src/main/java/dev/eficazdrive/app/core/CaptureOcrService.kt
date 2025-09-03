package dev.eficazdrive.app.core

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dev.eficazdrive.app.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * PT-BR: Serviço foreground que captura frames, roda OCR e envia o texto para o motor de regras.
 * EN: Foreground service that captures frames, runs OCR and feeds the rule engine.
 */
class CaptureOcrService : Service() {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var job: Job? = null
    private var projection: MediaProjection? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startInForeground()
        startLoop()
        ServiceStatus.setRunning(true)
    }

    private fun startInForeground() {
        val channelId = "eficazdrive_overlay"
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_LOW))
        val piFlags = if (Build.VERSION.SDK_INT >= 31) PendingIntent.FLAG_IMMUTABLE else 0
        val pendingIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), piFlags)
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentTitle("EficazDrive")
            .setContentText("Serviço de OCR em execução")
            .setContentIntent(pendingIntent)
            .build()
        val serviceType = if (Build.VERSION.SDK_INT >= 34) {
            // Android 14+ exige tipo de FGS quando envolve MediaProjection
            android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
        } else 0
        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(2, notification, serviceType)
        } else {
            startForeground(2, notification)
        }
    }

    private fun startLoop() {
        job?.cancel()
        job = scope.launch {
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            while (isActive) {
                // TODO: capturar bitmap via MediaProjection; por ora, placeholder
                val bitmap: Bitmap? = null
                if (bitmap != null) {
                    val image = InputImage.fromBitmap(bitmap, 0)
                    val result = recognizer.process(image).await()
                    RuleEngine.onText(result.text, this@CaptureOcrService)
                }
                delay(600)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
        projection?.stop()
        ServiceStatus.setRunning(false)
    }
}

// Await extension for ML Kit Task
private suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T =
    kotlinx.coroutines.suspendCancellableCoroutine { cont ->
        addOnSuccessListener { result -> cont.resume(result) }
        addOnFailureListener { error -> cont.resumeWithException(error) }
    }


