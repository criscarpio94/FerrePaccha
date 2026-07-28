package com.example.ferrepaccha.data.model

import com.google.gson.annotations.SerializedName

data class RespuestaImgBb(
    val data: DatosImagen?,
    @SerializedName("success")
    val success: Boolean = false,
    val status: Int = 0
)

data class DatosImagen(
    val url: String?,
    @SerializedName("display_url")
    val displayUrl: String?,
    val image: DetallesImagen?
)

data class DetallesImagen(
    val url: String?
)
