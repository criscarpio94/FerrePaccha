package com.example.ferrepaccha.data.model

data class RespuestaImgBb(
    val data: DatosImagen?,
    val succes: Boolean,
    val status: Int
)

data class DatosImagen(
    val url: String?,
    val display_url: String?,
    val image: DetallesImagen?
)

data class DetallesImagen(
    val url: String?
)
