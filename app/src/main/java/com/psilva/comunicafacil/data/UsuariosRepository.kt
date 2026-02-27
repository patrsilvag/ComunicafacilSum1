package com.psilva.comunicafacil.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.psilva.comunicafacil.model.Usuario
import kotlinx.coroutines.tasks.await

class UsuariosRepository : UsuariosDataSource {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("usuarios")

    override suspend fun registrarUsuario(usuario: Usuario): Result<Unit> {
        return try {
            // 1. Crear en Auth
            val result = auth.createUserWithEmailAndPassword(usuario.correo, usuario.clave).await()
            val uid = result.user?.uid ?: throw Exception("ID no encontrado")

            // 2. Guardar datos extras en Realtime Database
            database.child(uid).setValue(usuario).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(correo: String, clave: String): Result<Usuario> {
        return try {
            val result = auth.signInWithEmailAndPassword(correo, clave).await()
            val uid = result.user?.uid ?: throw Exception("Usuario no encontrado")

            val snapshot = database.child(uid).get().await()
            val usuario = snapshot.getValue(Usuario::class.java)

            if (usuario != null) Result.success(usuario)
            else Result.failure(Exception("Datos de perfil no encontrados"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun recuperarPassword(correo: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(correo).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}