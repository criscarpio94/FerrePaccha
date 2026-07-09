package com.example.ferrepaccha

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ferrepaccha.ui.PantallaInicio
import com.example.ferrepaccha.ui.theme.FerrePacchaTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //Inicializacion de firebase
        val db = Firebase.firestore

        setContent {
            FerrePacchaTheme {
                //llamada a pantalla inicio
                PantallaInicio()
            }
        }
    }
}