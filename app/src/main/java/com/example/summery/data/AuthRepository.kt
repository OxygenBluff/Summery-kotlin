package com.example.summery.data

import AuthResponseDTO
import LoginRequestDTO
import RegisterRequestDTO
import com.example.summery.local.EncryptedTokenManager
import com.example.summery.network.ApiCall
import com.example.summery.network.RetrofitInstance.parseErrorMessage
import retrofit2.Response

class AuthRepository(
    //injection ooh! service + that token manager
    private val apiService: ApiService,
    private val tokenManager: EncryptedTokenManager

){
    suspend fun loginUser(request: LoginRequestDTO): Result<AuthResponseDTO>{
        return ApiCall {
            val response= apiService.login(request)

            if(response.isSuccessful && response.body() !=null){
                val authData = response.body()!!
                tokenManager.saveTokens(authData.accessToken,authData.refreshToken)
            }
            response
        }


    }

    //signup same same
    suspend fun registerUser(request: RegisterRequestDTO): Result<AuthResponseDTO> {
        return try {
            val response: Response<AuthResponseDTO> = apiService.register(request)

            if (response.isSuccessful && response.body() != null) {
                val authData = response.body()!!
                tokenManager.saveTokens(authData.accessToken, authData.refreshToken)

                Result.success(authData)

            } else {
                val errorMessage =parseErrorMessage(response)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    }
}