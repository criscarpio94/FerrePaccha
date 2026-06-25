package com.example.ferrepaccha.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MovableContent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewDynamicColors

private val DarkColorScheme = darkColorScheme(
    primary = FerreAmarillo,
    secondary = FerreVerde,
    background = FerreGrisOscuro,
    surface = FerreGrisOscuro,
    onPrimary = FerreNegro,
    onSecondary = FerreBlanco
)

private val LighColorScheme = lightColorScheme(
    primary = FerreAmarillo,
    secondary = FerreVerde,
    background = FerreGrisClaro,
    surface = FerreBlanco,
    onPrimary = FerreBlanco,
    onSecondary = FerreBlanco
)

@Composable
fun FerrePacchaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
   val colorScheme = when {
       dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
           val context = LocalContext.current
           if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
       }
       darkTheme -> DarkColorScheme
       else -> LighColorScheme
   }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}