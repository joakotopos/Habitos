package com.example.habitos.data

import com.example.habitos.data.model.AuthResponse
import com.example.habitos.data.model.Profile
import com.example.habitos.data.model.SignInRequest
import com.example.habitos.data.model.SignUpOptions
import com.example.habitos.data.model.SignUpRequest
import com.example.habitos.data.model.UserMetadata
import com.example.habitos.data.network.SupabaseClient

class AuthRepository {
    
    private val authApiService = SupabaseClient.authApiService
    private val apiKey = SupabaseClient.supabaseKey
    
    suspend fun signUp(email: String, password: String, name: String): AuthResponse {
        val request = SignUpRequest(
            email = email,
            password = password,
            options = SignUpOptions(
                data = UserMetadata(name = name)
            )
        )
        val response = authApiService.signUp(apiKey = apiKey, request = request)
        
        // Crear perfil en la tabla profiles
        try {
            android.util.Log.d("AuthRepository", "Intentando crear perfil: userId=${response.user.id}, email=$email, name=$name")
            val profile = Profile(
                id = response.user.id,
                email = email,
                name = name
            )
            val authorization = "Bearer ${response.accessToken}"
            val createdProfile = authApiService.createProfile(apiKey, authorization, profile = profile)
            android.util.Log.d("AuthRepository", "Perfil creado exitosamente: $createdProfile")
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            android.util.Log.e("AuthRepository", "Error HTTP ${e.code()} al crear perfil: $errorBody", e)
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Error al crear perfil: ${e.message}", e)
            e.printStackTrace()
        }
        
        return response
    }
    
    suspend fun signIn(email: String, password: String): AuthResponse {
        val request = SignInRequest(email, password)
        return authApiService.signIn(apiKey = apiKey, request = request)
    }
    
    suspend fun getProfile(userId: String, accessToken: String): Profile? {
        val authorization = "Bearer $accessToken"
        val profiles = authApiService.getProfile(apiKey, authorization, "eq.$userId")
        return profiles.firstOrNull()
    }
}

