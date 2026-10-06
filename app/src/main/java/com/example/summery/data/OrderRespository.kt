package com.example.summery.data

import com.example.summery.data.remote.api.ApiCall
import com.example.summery.data.remote.dto.OrderRequestDTO
import com.example.summery.data.remote.dto.OrderResponseDTO
import com.example.summery.network.AddressResponseDTO
import com.example.summery.network.PageResponse

import com.example.summery.network.RetrofitInstance


object OrderRepository{
    suspend fun placeOrder(request: OrderRequestDTO): Result<OrderResponseDTO>{
        return ApiCall {
            RetrofitInstance.api.placeOrder(
                request
            )
        }
    }


    //all my orders -> paged -> paging 3
    suspend fun getMyOrders(
        page:Int =0,
        size: Int=8,
        sortingOption: String="dateCommande"

    ): Result<PageResponse<OrderResponseDTO>>{
        return ApiCall {
            RetrofitInstance.api.getMyOrders(
                page=page,
                size=size,
                sort=sortingOption
            )
        }
    }

    suspend fun getOrderById(id: Long): Result<OrderResponseDTO>{
        return ApiCall {
            RetrofitInstance.api.getOrderDetails(id)
        }
    }

    //addresses stuff
    suspend fun getMyAddresses(): Result<List<AddressResponseDTO>>{
        return ApiCall {
            RetrofitInstance.api.getMyAddresses()
        }
    }

    suspend fun addAddress(request: AddressResponseDTO): Result<AddressResponseDTO>{
        return ApiCall {
            RetrofitInstance.api.addAddress(request)
        }
    }

    //del address
    suspend fun deleteAddress(AddressId: Long):Result<Unit>{
        return ApiCall {
            RetrofitInstance.api.deleteAddress(id=AddressId)
        }
    }




}
