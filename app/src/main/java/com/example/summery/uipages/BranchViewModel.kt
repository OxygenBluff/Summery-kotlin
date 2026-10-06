package com.example.summery.uipages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.summery.data.BranchesRepository
import com.example.summery.network.BranchResponseDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


//oof i actually HAVE to make a UI state now..
sealed interface BranchUiState{
    data object Loading : BranchUiState
    data class Success(val branch: BranchResponseDTO): BranchUiState
    data class Error (val message:String): BranchUiState
}

//why sealed ? -> ONLY these (3) options WILL EVER EXIST
//when(uiState) -> don't have to make an ELSE branch yess

//Interface why not class ?
//-> lighter than abstract class, data object Loading doesn't have to hold anything
class BranchViewModel(): ViewModel() {

   private val _uiState = MutableStateFlow<BranchUiState>(BranchUiState.Loading)
   val uiState = _uiState.asStateFlow()


    fun fetchBranch(id:Long){
        viewModelScope.launch {
            _uiState.value= BranchUiState.Loading

            val result = BranchesRepository.getBranchById(id)
            result.onSuccess { res->
                _uiState.value= BranchUiState.Success(res) // HUUUH
            }
                .onFailure {error ->
                    _uiState.value= BranchUiState.Error(error.message ?: "Failed to load branch")
                }
        }
    }




    //TODO NOT EVEN A CLEAR ERROR MESSAGE FUNCTION IS NEEDED ANYMORE ?

}