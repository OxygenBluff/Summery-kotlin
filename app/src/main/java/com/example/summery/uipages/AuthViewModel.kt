package com.example.summery.uipages

import LoginRequestDTO
import RegisterRequestDTO
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.summery.data.AuthRepository
import com.example.summery.local.EncryptedTokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository,
    private val tokenManager: EncryptedTokenManager
    //why private val ?
    //-> ONLY code inside this viewmodel file can see these tools
    //so authViewModel.tokenManager.clearTokens()
    //something like that C A N N O T be called from say home screen or ..
): ViewModel (){
    //wth is going on..

    //the states from the Uis ?
    //ONLY viewmodel should be able to change these ?
    //but UI must READ them as well
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    //_ one = MUTABLE -> private secret inside viewmodel
    //without _ = READ ONLY -> compose UI reads this

    private val _statusMessage = MutableStateFlow("")
    val statusMessage : StateFlow<String> = _statusMessage

    private val _isSuccessMessage = MutableStateFlow(false)
    val isSuccessMessage: StateFlow<Boolean> = _isSuccessMessage


    //transitioning to the home screen
    private val _navigateToHome = MutableStateFlow(false)
    val navigateToHome: StateFlow<Boolean> = _navigateToHome.asStateFlow()
    //wait why asStateFlow ? as = casting btw
    //-> Read only = state flow, UI can lok only/ observe
    //asStateFlow = MORE SECURE, creates a new wrapper object !


    fun handleLogin(
        request: LoginRequestDTO
    ){
        //!! detail!
        //coroutine to make the network call
        //kotlin forbids network calls on the main loop ?

        //checks here now.. not in UI anymore
        if (request.email.isEmpty() || request.password.isEmpty()) {
            _statusMessage.value = "All fields are required!"
            _isSuccessMessage.value = false
            return
        }

        viewModelScope.launch {
            _isLoading.value=true
            _statusMessage.value=""

            repository.loginUser(request)

                .onSuccess {
                    //
                    val token= tokenManager.getAccessToken()

                    //-> Home
                    if(!token.isNullOrBlank()){
                        _isSuccessMessage.value=true
                        _statusMessage.value="Welcome Back!"
                        _navigateToHome.value=true //!
                    }else{
                        _statusMessage.value = "Security error: Access verification failed."
                        _isSuccessMessage.value = false
                    }

            }
                .onFailure { exception ->
                //ApiCall already returns it..
                _statusMessage.value=exception.message ?: "An Unexpcted Error occurred"
                    //still wants me to handle the null oh kotlin u..
                //aha the ?: nice!
                _isSuccessMessage.value=false
            }
            _isLoading.value=false



        }
    }

    //restting navigation flag after transitioning
    fun onNavigationHandled(){
        _navigateToHome.value=false
    }
    //singup screen! same treatment!
    fun handleSignUp(
        request: RegisterRequestDTO
    ){
        //1-checks
        if (request.email.isEmpty() || request.password.isEmpty()
            || request.nom.isEmpty() || request.prenom.isEmpty()
            ) {
            _statusMessage.value = "All fields are required!"
            _isSuccessMessage.value = false
            return
        }

        viewModelScope.launch {
            _isLoading.value=true
            _statusMessage.value=""

            val result = repository.registerUser(request)

            result.onSuccess {
                _isSuccessMessage.value=true
                _statusMessage.value="Welcome!"

            }.onFailure { exception ->
                _isSuccessMessage.value=false
                _statusMessage.value=exception.message?: "An error occurred"
            }

            _isLoading.value=false

        }


    }

}