package dev.eficazdrive.app.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import dev.eficazdrive.app.R
import dev.eficazdrive.app.core.CaptureOcrService
import dev.eficazdrive.app.core.ServiceStatus

/**
 * PT-BR: Activity simples para solicitar permissões e iniciar o serviço.
 * EN: Simple activity to request permissions and start the service.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val statusText = findViewById<TextView>(R.id.statusText)
        val startButton = findViewById<Button>(R.id.startButton)
        val stopButton = findViewById<Button>(R.id.stopButton)
        val statusDot = findViewById<ImageView>(R.id.statusDot)
        val monitorLabel = findViewById<TextView>(R.id.monitorLabel)

        // Notificações (Android 13+)
        if (Build.VERSION.SDK_INT >= 33) {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {}.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // Sobreposição
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivity(intent)
        }

        fun refreshStatus() {
            if (ServiceStatus.isRunning) {
                statusText.text = "Serviço em execução"
                monitorLabel.text = "Ativo"
                statusDot.setImageResource(R.drawable.circle_green)
            } else {
                statusText.text = "Serviço parado"
                monitorLabel.text = "Inativo"
                statusDot.setImageResource(R.drawable.circle_red)
            }
        }

        refreshStatus()

        startButton.setOnClickListener {
            startForegroundService(Intent(this, CaptureOcrService::class.java))
            refreshStatus()
        }
        stopButton.setOnClickListener {
            stopService(Intent(this, CaptureOcrService::class.java))
            refreshStatus()
        }
    }
}


