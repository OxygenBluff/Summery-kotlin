package com.example.summery.uipages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.summery.data.BranchesRepository
import com.example.summery.network.BranchResponseDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BranchesViewModel(
): ViewModel() {

    private val _isFetchingBranches = MutableStateFlow(false)
    val isFetchingBranches = _isFetchingBranches.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()

    //from repooo
    val branches:StateFlow<List<BranchResponseDTO>> = BranchesRepository.branchesList


    init {
        fetchBranches()
    }

    fun fetchBranches(forceRefresh: Boolean = false){
        viewModelScope.launch {
            if(branches.value.isNotEmpty() && !forceRefresh){
                return@launch
                //Okay wtf is that ? also TEALLL is such a GOATED COLOR
                // that's ow to exit a viewModelScope bloc
                //regular return doesn't work for some reason
            }
            //otherwise..
            _isFetchingBranches.value=true
            _errorMessage.value=""//reset i FORGORRRRRR

            val result = BranchesRepository.getBranches()
            result.onFailure {
                val error=result.exceptionOrNull()
                _errorMessage.update { error?.message ?: "Failed to load branches" }

            }

            _isFetchingBranches.value=false

        }
    }


    fun clearErrorMessage(){
        _errorMessage.value=""
    }

}