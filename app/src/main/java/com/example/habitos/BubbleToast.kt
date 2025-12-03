package com.example.habitos

import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat

object BubbleToast {
    
    private var currentToast: View? = null
    
    fun show(activity: Activity, message: String, duration: Long = 2000) {
        // Remover toast anterior si existe
        currentToast?.let { 
            (it.parent as? ViewGroup)?.removeView(it)
        }
        
        val inflater = LayoutInflater.from(activity)
        val layout = inflater.inflate(R.layout.custom_toast_bubble, null)
        
        val textView = layout.findViewById<TextView>(R.id.tvBubbleMessage)
        textView.text = message
        
        // Obtener el contenedor raíz de la actividad
        val rootView = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
        
        // Crear parámetros de layout para posicionar el toast
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
            topMargin = 200 // Margen desde arriba
        }
        
        layout.layoutParams = params
        
        // Agregar el toast al contenedor
        rootView.addView(layout)
        currentToast = layout
        
        // Crear animación de entrada (aparece desde abajo con fade in)
        val translateIn = TranslateAnimation(
            0f, 0f,
            100f, 0f
        ).apply {
            this.duration = 400
        }
        
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            this.duration = 400
        }
        
        val animationSetIn = AnimationSet(true).apply {
            addAnimation(translateIn)
            addAnimation(fadeIn)
        }
        
        layout.startAnimation(animationSetIn)
        
        // Programar la animación de salida (sube y desaparece)
        layout.postDelayed({
            val translateOut = TranslateAnimation(
                0f, 0f,
                0f, -300f
            ).apply {
                this.duration = 800
                interpolator = AccelerateInterpolator()
            }
            
            val fadeOut = AlphaAnimation(1f, 0f).apply {
                this.duration = 800
                startOffset = 200
            }
            
            val animationSetOut = AnimationSet(true).apply {
                addAnimation(translateOut)
                addAnimation(fadeOut)
                
                setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                    override fun onAnimationStart(animation: android.view.animation.Animation?) {}
                    override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
                    override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                        rootView.removeView(layout)
                        currentToast = null
                    }
                })
            }
            
            layout.startAnimation(animationSetOut)
        }, duration)
    }
}
