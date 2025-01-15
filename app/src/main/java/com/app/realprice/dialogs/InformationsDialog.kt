package com.app.realprice.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.realprice.R
import com.app.realprice.services.ThemeService

class InformationsDialog {
    companion object {
        fun show(
            context: Context,
            title: String,
            text: String,
            buttonText: String,
            callback: () -> Unit
        ) {
            val themeService = ThemeService(context)

            val dialog = Dialog(context)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_informations)

            val color = if (themeService.isDarkThemeActive()) {
                context.getColor(R.color.grey)
            } else {
                context.getColor(R.color.true_white)
            }

            val titleTextView = dialog.findViewById<TextView>(R.id.dialog_informations_title)
            titleTextView.text = title

            val textTextView = dialog.findViewById<TextView>(R.id.dialog_informations_text)
            textTextView.text = text

            val button = dialog.findViewById<Button>(R.id.dialog_informations_button)
            button.text = buttonText
            button.setOnClickListener {
                callback()
                dialog.dismiss()
            }

            val root = dialog.findViewById<ConstraintLayout>(R.id.dialog_root)
            root.setBackgroundColor(color)

            dialog.show()

            // change the size of the dialog
            val window = dialog.window
            if (window != null) {
                val width =
                    (context.resources.displayMetrics.widthPixels * 0.85).toInt() // 85% de la largeur de l'écran
                val height =
                    (context.resources.displayMetrics.heightPixels * 0.5).toInt() // 90% de la hauteur de l'écran
                window.setLayout(width, height)

                // Définir le fond de la fenêtre de dialogue sur transparent
                window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
    }
}
