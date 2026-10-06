package com.example.summery.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserResponseDTO(
    val firstName: String,
    val lastName: String,
    val email: String,

    val active: Boolean,
    val createdAt: String,
    //well well well

    val accessToken :String,
    val refreshToken: String
)

@Serializable
data class UpdateUserRequestDTO(
    //NULLABLESSS
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null
)
