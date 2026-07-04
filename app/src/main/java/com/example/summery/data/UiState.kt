package com.example.summery.data

sealed class UiState {
    object Loading : UiState()
    data class Success(val time: String) : UiState()
    data class Error(val message: String) : UiState()
}