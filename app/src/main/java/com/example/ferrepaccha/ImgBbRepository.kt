package com.example.ferrepaccha

import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.io.FileSystem
import java.io.File
import java.io.FileOutputStream

object ImgBbRepository {
    //Servidor de red
    private val servicioApi: ImgBbApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Constantes.IMGBB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ImgBbApiService::class.java)
    }

    //Subir foto tomada con el dispositivo y devolver la url final
    suspend fun subirFotoAdmin(context: Context, uriId: Uri): String? {
        return try {
            //Convertir la Uri en un archivo temporal a manera de fisico
            val archivoTemporal = uriAArchivo(context, uriId) ?: return null

            //preparar datos para la solicitud HTTP
            val peticionArchivo = archivoTemporal.asRequestBody("image/*".toMediaTypeOrNull())
            val parteImagen = MultipartBody.Part.createFormData("image", archivoTemporal.name, peticionArchivo)
            val parteLlaveApi = Constantes.IMGBB_API_KEY.toRequestBody("text/plain".toMediaTypeOrNull())

            //Enviar datos al servidor
            val respuestaServidor = servicioApi.subirImagen(parteLlaveApi, parteImagen)

            //Limpiar almacenamiento temporal en el telefono
            if (archivoTemporal.exists()) archivoTemporal.delete()

            //Devolver URL limpia si funciona correctamente
            if (respuestaServidor.isSuccessful) {
                val cuerpoRespuesta = respuestaServidor.body()
                if (cuerpoRespuesta != null && cuerpoRespuesta.succes) {
                    cuerpoRespuesta.data?.image?.url
                } else {
                    null
                }
            } else {
                null
            }

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    //Funcion para copia de imagen en un archivo intermedio
    private fun uriAArchivo(context: Context, uri: Uri): File? {
        return try {
            val flujoEntrada = context.contentResolver.openInputStream(uri) ?: return null
            val archivoDestino = File.createTempFile("ferre_subida", ".jpg", context.cacheDir)
            val flujoSalida = FileOutputStream(archivoDestino)
            flujoEntrada.use { entrada ->
                flujoSalida.use { salida ->
                    entrada.copyTo(salida)
                }
            }
            archivoDestino
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}