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
            val profile = Profile(
                id = response.user.id,
                email = email,
                name = name
            )
            val authorization = "Bearer ${response.accessToken}"
            authApiService.createProfile(apiKey, authorization, profile = profile)
        } catch (e: Exception) {
            // Si falla crear el perfil, no bloqueamos el registro
            // pero podrías manejarlo de otra forma
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

