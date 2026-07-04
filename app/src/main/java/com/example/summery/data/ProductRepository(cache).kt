package com.example.summery.data

import com.example.summery.network.ApiCall
import com.example.summery.network.PageResponse
import com.example.summery.network.ProductResponseDTO
import com.example.summery.network.RetrofitInstance

object ProductRepository{
    suspend fun getProducts(page:Int): Result<PageResponse<ProductResponseDTO>>{
        return ApiCall{
            RetrofitInstance.api.getAllProducts(page=page)
        }//.map {pageResponse ->
           // pageResponse.content?: emptyList()

    }

}







