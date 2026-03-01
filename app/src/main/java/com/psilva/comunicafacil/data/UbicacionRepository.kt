package com.psilva.comunicafacil.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.psilva.comunicafacil.model.UbicacionUsuario // Importa desde tu carpeta model
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UbicacionRepository {
    private val db = FirebaseDatabase.getInstance().getReference("ubicaciones_usuarios")

    // CREATE / UPDATE
    suspend fun guardarOActualizarUbicacion(uid: String, ubicacion: UbicacionUsuario) {
        try {
            db.child(uid).setValue(ubicacion).await()
        } catch (e: Exception) {
            throw e
        }
    }

    // READ
    fun obtenerUbicacionRealTime(uid: String): Flow<UbicacionUsuario?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.getValue(UbicacionUsuario::class.java)
                trySend(data)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        db.child(uid).addValueEventListener(listener)
        awaitClose { db.child(uid).removeEventListener(listener) }
    }

    // DELETE
    suspend fun eliminarUbicacion(uid: String) {
        try {
            db.child(uid).removeValue().await()
        } catch (e: Exception) {
            throw e
        }
    }
}