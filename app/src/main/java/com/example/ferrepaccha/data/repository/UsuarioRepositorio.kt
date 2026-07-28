package com.example.ferrepaccha.data.repository

import android.util.Log
import com.example.ferrepaccha.data.model.UsuarioFirebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.functions.FirebaseFunctions
import com.example.ferrepaccha.util.normalizarNombre
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class UsuarioRepositorio {
    private val db = FirebaseFirestore.getInstance()
    private val coleccion = db.collection("usuarios")
    private val auth = FirebaseAuth.getInstance()
    private val functions = FirebaseFunctions.getInstance()

    companion object {
        private const val TAG = "UsuarioRepositorio"
    }

    fun escucharPorRol(rol: String): Flow<List<UsuarioFirebase>> {
        return coleccion
            .whereEqualTo("rol", rol)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(UsuarioFirebase::class.java)
                        ?.copy(uid = doc.id)
                        ?.normalizarNombre()
                }
            }
    }

    suspend fun guardarUsuario(usuario: UsuarioFirebase): Boolean {
        return try {
            coleccion.document(usuario.uid).set(usuario).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar usuario", e)
            false
        }
    }

    suspend fun actualizarUsuarioEnFirestore(
        uid: String,
        nombre: String,
        correo: String,
        rol: String
    ): Boolean {
        return try {
            coleccion.document(uid).update(
                mapOf(
                    "nombre" to nombre,
                    "nombreCompleto" to nombre,
                    "correo" to correo,
                    "rol" to rol
                )
            ).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar usuario en Firestore", e)
            false
        }
    }

    suspend fun actualizarCredencialesAuth(
        uid: String,
        correoNuevo: String,
        contrasenaNueva: String?
    ): Result<String> {
        return try {
            val datos = hashMapOf<String, Any>(
                "uid" to uid,
                "email" to correoNuevo
            )
            if (!contrasenaNueva.isNullOrBlank()) {
                datos["password"] = contrasenaNueva
            }
            functions
                .getHttpsCallable("adminActualizarUsuario")
                .call(datos)
                .await()
            Result.success("Credenciales de acceso actualizadas")
        } catch (e: Exception) {
            Log.w(TAG, "Cloud Function no disponible, intentando alternativa", e)
            if (!contrasenaNueva.isNullOrBlank()) {
                try {
                    auth.sendPasswordResetEmail(correoNuevo).await()
                    return Result.success(
                        "Perfil guardado. Se envió correo para restablecer contraseña"
                    )
                } catch (resetError: Exception) {
                    Log.e(TAG, "Error al enviar restablecimiento", resetError)
                }
            }
            Result.failure(
                Exception(
                    "Perfil guardado en Firestore. Para actualizar correo/contraseña de acceso " +
                        "despliegue la Cloud Function adminActualizarUsuario"
                )
            )
        }
    }

    suspend fun eliminarUsuario(uid: String): Boolean {
        return try {
            coleccion.document(uid).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun obtenerPorUid(uid: String): UsuarioFirebase? {
        return try {
            val doc = coleccion.document(uid).get().await()
            if (doc.exists()) {
                doc.toObject(UsuarioFirebase::class.java)?.copy(uid = doc.id)?.normalizarNombre()
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
