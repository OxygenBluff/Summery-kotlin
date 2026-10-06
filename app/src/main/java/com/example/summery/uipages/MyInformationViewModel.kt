package com.example.summery.uipages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.summery.data.AccountRepository
import com.example.summery.data.remote.dto.UpdateUserRequestDTO
import com.example.summery.local.EncryptedTokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class EditInfoUiState(
    //actually.. the repo does ..  + unpacking fields.. yikes
    val firstName: String="",
    val lastName: String="",
    val email:String= "",
    val isLoading: Boolean = false,
    val errorMessage:String?=null,
    val statusMessage: String?=null
)

class MyInformationViewModel(
    private val repository: AccountRepository,
    private val tokenManager: EncryptedTokenManager
): ViewModel() {

    private val _uiState = MutableStateFlow(
        EditInfoUiState()
    )
    val uiState: StateFlow<EditInfoUiState> = _uiState.asStateFlow()

    //get it from repo
    init{
        viewModelScope.launch{
            repository.userState.collect{ userState ->
                _uiState.update{
                    it.copy(
                        firstName = userState?.firstName ?:"",
                        lastName = userState?.lastName ?:"",
                        email=userState?.email ?:""
                    )
                }
            }
        }
    }

    //all good, text fields are FILLED automatically from the start
    //then what.. get the onChanged -> update the _uiState ?

    fun onFirstNameChanged(newFirstName:String){
        _uiState.update{
            it.copy(
                firstName = newFirstName
            )
        }
    }

    fun onLastNameChanged(newLastName:String){
        _uiState.update{
            it.copy(
                lastName = newLastName
            )
        }
    }
    fun onEmailChanged(newEmail:String){
        _uiState.update{
            it.copy(
                email = newEmail
            )
        }
    }



    fun saveChanges(){
        //double ones..
        if(_uiState.value.isLoading) return


        viewModelScope.launch {
            //loading reset error WHY I ALWAYS FORGET THIS
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            //request dto
            val updateDto = UpdateUserRequestDTO(
                firstName = _uiState.value.firstName,
                lastName = _uiState.value.lastName,
                email = _uiState.value.email
            )
            repository.updateMyAccountInformation(updateDto)
                .onSuccess { newUserDTO->
                    //SO HERE sucessful request -> backend returns the
                    //user state done !!!!!
                    _uiState.update { it.copy(
                        isLoading = false,
                        statusMessage = "changes saved successfully!"
                    ) }
                    //NEW TOKENSSS
                    tokenManager.saveTokens(
                        accessToken = newUserDTO.accessToken ,
                        refreshToken =newUserDTO.refreshToken
                    )

                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error?.message ?: "Failed to save changes"
                        )
                    }
                }


        }
    }

    fun clearErrorMessage(){
        _uiState.update {
            it.copy(
                errorMessage = null
            )
        }
    }

    fun clearStatusMessage(){
        _uiState.update {
            it.copy(
                statusMessage = null
            )
        }
    }


}