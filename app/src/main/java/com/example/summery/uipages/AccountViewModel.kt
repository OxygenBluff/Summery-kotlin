package com.example.summery.uipages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.summery.data.AccountRepository
import com.example.summery.data.remote.dto.UserResponseDTO
import com.example.summery.local.EncryptedTokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


//OPTIMISTIC UI YAAY, but with uiState ?
data class AccountInformationUiState(
    //CANNOT initialize here it will stay static
    val user: UserResponseDTO? =null,
    val isLoading: Boolean = false,
    val errorMessage:String? =null
)


//uhm factory better
class AccountViewModel(
    private val repository: AccountRepository,
    private val tokenManager: EncryptedTokenManager

): ViewModel (){

    //Optimistic
    private val _uiState = MutableStateFlow(
        AccountInformationUiState(
            user= AccountRepository.userState.value
        )
    )

    //instant
    val uiState: StateFlow<AccountInformationUiState> = _uiState.asStateFlow()

    //get it from repo:

    init{
        //updates only when repository.userState changes ?
        //so a COROUTINE that listens FOREVER to the repo's flow..
        //every time repo emits -> put it into the UiState (copy)
        viewModelScope.launch {
            AccountRepository.userState.collect{updatedUser ->
                _uiState.update { it.copy(user=updatedUser) }
            }
        }
        //collect -> no lifecycle awareness just.. works as long as the coroutine is there
        //collectAsState is compose ONLY, flow to State but still works in the background
        //


        //then this is redundant no ? welp add a check
        if(AccountRepository.userState.value==null) {
            fetchAccountInformation()
        }


    }


    fun fetchAccountInformation(){
        viewModelScope.launch {
            //so FIRST = everything null again EXCEPT loading, data cass has .copy()
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            //get
            //BUT no need to touch the user here.. the collect already does it
            repository.getMyAccountInformation()
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false
                        )
                    }
                    //that's it
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage?: "Failed to load profile"
                        )
                    }
                }
        }
    }

    fun clearErrorMessage(){
        _uiState.update {
            it.copy(errorMessage = null)
            //specify one -> others untouched right ?
        }
    }

    fun logout(
        onSuccess: () -> Unit
    ){
        viewModelScope.launch {
            tokenManager.clearTokens()
        }
    }

    //SUBSCRRENS (except orders)



}