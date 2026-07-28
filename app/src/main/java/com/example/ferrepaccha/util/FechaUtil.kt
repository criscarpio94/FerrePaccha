package com.example.ferrepaccha.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object FechaUtil {

    fun inicioDelDia(calendario: Calendar = Calendar.getInstance()): Long {
        val copia = calendario.clone() as Calendar
        copia.set(Calendar.HOUR_OF_DAY, 0)
        copia.set(Calendar.MINUTE, 0)
        copia.set(Calendar.SECOND, 0)
        copia.set(Calendar.MILLISECOND, 0)
        return copia.timeInMillis
    }

    fun finDelDia(calendario: Calendar = Calendar.getInstance()): Long {
        val copia = calendario.clone() as Calendar
        copia.set(Calendar.HOUR_OF_DAY, 23)
        copia.set(Calendar.MINUTE, 59)
        copia.set(Calendar.SECOND, 59)
        copia.set(Calendar.MILLISECOND, 999)
        return copia.timeInMillis
    }

    fun esMismoDia(fecha: Date, referencia: Calendar = Calendar.getInstance()): Boolean {
        val cal = Calendar.getInstance().apply { time = fecha }
        return cal.get(Calendar.YEAR) == referencia.get(Calendar.YEAR) &&
            cal.get(Calendar.DAY_OF_YEAR) == referencia.get(Calendar.DAY_OF_YEAR)
    }

    fun timestampADate(timestamp: Timestamp?): Date? = timestamp?.toDate()

    fun formatear(fecha: Date, patron: String = "dd/MM/yyyy HH:mm"): String {
        return SimpleDateFormat(patron, Locale.getDefault()).format(fecha)
    }

    fun formatearCorto(fecha: Date): String {
        return SimpleDateFormat("dd-MMM, hh:mm a", Locale("es", "EC")).format(fecha)
    }

    fun hace24Horas(): Long = System.currentTimeMillis() - 24 * 60 * 60 * 1000L
}
