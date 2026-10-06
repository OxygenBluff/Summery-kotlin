package com.example.summery.data


import com.example.summery.data.remote.api.ApiCall
import com.example.summery.network.BranchResponseDTO
import com.example.summery.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object BranchesRepository {

    private val _branchesList = MutableStateFlow<List<BranchResponseDTO>> (emptyList())
    val branchesList = _branchesList.asStateFlow()
    //CACHEE
    //objcet = SINGELTON so actually
    //THIS STAYS across all screens



    suspend fun getBranches(refresh: Boolean=false): Result<List<BranchResponseDTO>>{
        if (!refresh && _branchesList.value.isNotEmpty()) {
            return Result.success(_branchesList.value)
        }

        val res = ApiCall {
            RetrofitInstance.api.getBranches()
        }
        res.onSuccess { result->
            _branchesList.value = result
        }
        return res
    }

    //by id

    suspend fun getBranchById(id:Long ): Result<BranchResponseDTO>{
        val res =ApiCall {
            RetrofitInstance.api.getBranchById(id)
        }
        return res;

    }
}