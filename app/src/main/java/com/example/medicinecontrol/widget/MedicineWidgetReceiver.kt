package com.example.medicinecontrol.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.medicinecontrol.HomeActivity
import com.example.medicinecontrol.R
import com.example.medicinecontrol.Repository
import com.example.medicinecontrol.calcularProximaToma
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MedicineWidgetReceiver : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val meds = Repository.medicamentos
            val ahora = LocalDateTime.now()
            // Buscar el medicamento con la próxima toma más cercana
            val proximoMed = meds.minByOrNull { calcularProximaToma(it, ahora) }

            val views = RemoteViews(context.packageName, R.layout.widget_medicine)

            if (proximoMed != null) {
                val proximaToma = calcularProximaToma(proximoMed, ahora)
                views.setTextViewText(R.id.widget_med_name, proximoMed.nombre)
                views.setTextViewText(R.id.widget_med_dosis, "Dosis: ${proximoMed.dosis}")
                views.setTextViewText(
                    R.id.widget_med_time,
                    "Próxima toma: ${proximaToma.format(DateTimeFormatter.ofPattern("HH:mm"))}"
                )
            } else {
                views.setTextViewText(R.id.widget_med_name, "MedicineControl")
                views.setTextViewText(R.id.widget_med_dosis, "Abre la app para ver medicamentos")
                views.setTextViewText(R.id.widget_med_time, "")
            }

            // Click en el widget abre la app
            val intent = Intent(context, HomeActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
