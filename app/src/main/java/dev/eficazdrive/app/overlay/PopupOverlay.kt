package dev.eficazdrive.app.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import dev.eficazdrive.app.databinding.PopupOverlayBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * PT-BR: Overlay simples para exibir JSON com botão de fechar e auto-dismiss.
 * EN: Simple overlay to display JSON with close button and auto-dismiss.
 */
object PopupOverlay {
    private var binding: PopupOverlayBinding? = null
    private var job: Job? = null

    fun showJson(context: Context, json: String, displayMs: Long) {
        hide(context)
        val inflater = context.getSystemService(LayoutInflater::class.java)
        val b = PopupOverlayBinding.inflate(inflater)
        binding = b
        b.jsonText.text = json
        b.closeButton.setOnClickListener { hide(context) }

        val wm = context.getSystemService(WindowManager::class.java)
        val type = if (Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                   else WindowManager.LayoutParams.TYPE_PHONE
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP; y = 60 }

        wm.addView(b.root, params)

        job = CoroutineScope(Dispatchers.Main).launch {
            delay(displayMs)
            hide(context)
        }
    }

    fun hide(context: Context) {
        job?.cancel()
        job = null
        val wm = context.getSystemService(WindowManager::class.java)
        val view: View? = binding?.root
        if (view != null) runCatching { wm.removeView(view) }
        binding = null
    }
}


