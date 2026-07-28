package com.example.ferrepaccha.network

import android.content.Context
import android.net.Uri
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream

object ImgBbRepository {
    private const val TAG = "ImgBbRepository"

    private val servicioApi: ImgBbApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Constantes.IMGBB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ImgBbApiService::class.java)
    }

    suspend fun subirFotoAdmin(context: Context, uriId: Uri, nombreArchivo: String? = null): String? {
        return try {
            val archivoTemporal = uriAArchivo(context, uriId, nombreArchivo) ?: return null

            val peticionArchivo = archivoTemporal.asRequestBody("image/*".toMediaTypeOrNull())
            val parteImagen = MultipartBody.Part.createFormData("image", archivoTemporal.name, peticionArchivo)
            val parteLlaveApi = Constantes.IMGBB_API_KEY.toRequestBody("text/plain".toMediaTypeOrNull())

            val respuestaServidor = servicioApi.subirImagen(parteLlaveApi, parteImagen)

            if (archivoTemporal.exists()) archivoTemporal.delete()

            if (respuestaServidor.isSuccessful) {
                val cuerpoRespuesta = respuestaServidor.body()
                val url = extraerUrlImagen(cuerpoRespuesta)
                if (url != null) {
                    url
                } else {
                    Log.e(TAG, "Respuesta sin URL: success=${cuerpoRespuesta?.success} status=${cuerpoRespuesta?.status}")
                    null
                }
            } else {
                Log.e(TAG, "Error HTTP ImgBB: ${respuestaServidor.code()} ${respuestaServidor.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al subir imagen", e)
            null
        }
    }

    private fun extraerUrlImagen(respuesta: com.example.ferrepaccha.data.model.RespuestaImgBb?): String? {
        if (respuesta == null) return null
        if (!respuesta.success && respuesta.data == null) return null

        return respuesta.data?.displayUrl?.takeIf { it.isNotBlank() }
            ?: respuesta.data?.image?.url?.takeIf { it.isNotBlank() }
            ?: respuesta.data?.url?.takeIf { it.isNotBlank() }
    }

    private fun uriAArchivo(context: Context, uri: Uri, nombreArchivo: String? = null): File? {
        return try {
            val flujoEntrada = context.contentResolver.openInputStream(uri) ?: return null
            val prefijo = nombreArchivo?.replace(Regex("[^a-zA-Z0-9_-]"), "_") ?: "ferre_subida"
            val archivoDestino = File.createTempFile(prefijo, ".jpg", context.cacheDir)
            val flujoSalida = FileOutputStream(archivoDestino)
            flujoEntrada.use { entrada ->
                flujoSalida.use { salida ->
                    entrada.copyTo(salida)
                }
            }
            archivoDestino
        } catch (e: Exception) {
            Log.e(TAG, "Error al leer URI", e)
            null
        }
    }
}
