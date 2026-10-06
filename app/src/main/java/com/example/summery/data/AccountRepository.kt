package com.example.summery.data


import com.example.summery.data.remote.api.ApiCall
import com.example.summery.data.remote.dto.UpdateUserRequestDTO
import com.example.summery.data.remote.dto.UserResponseDTO
import com.example.summery.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AccountRepository{
    //shared again.. user info

    private val _userState = MutableStateFlow<UserResponseDTO?>(null)
    val userState: StateFlow<UserResponseDTO?> =_userState.asStateFlow()


    //get my user info > the intials
    suspend fun getMyAccountInformation(): Result<UserResponseDTO>{
        return ApiCall {
            RetrofitInstance.api.getMyAccountInformation()
        }.onSuccess { data ->
            _userState.value=(data)
        }
    }

    //update them
    suspend fun updateMyAccountInformation(
        request: UpdateUserRequestDTO
    ): Result<UserResponseDTO>{
        return ApiCall {
            RetrofitInstance.api.updateMyAccountInformation(
                request = request
            )
        }.onSuccess { updatedUser ->
            _userState.value=updatedUser
        }

    }

    //SHARED = DELETE IT when logs out
    fun clearUserState () {
        _userState.value=null
    }

}

