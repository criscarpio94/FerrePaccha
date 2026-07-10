package com.example.ferrepaccha

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImgBbApiService {
    @Multipart
    @POST("1/upload")
    suspend fun subirImagen(
        @Part("key") llaveApi: RequestBody, //llave de seguridad
        @Part imagenArchivo: MultipartBody.Part
    ): Response<RespuestaImgBb>
}