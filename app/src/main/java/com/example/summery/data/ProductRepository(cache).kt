package com.example.summery.data


import com.example.summery.data.remote.api.ApiCall
import com.example.summery.network.CategoryDTO
import com.example.summery.network.PageResponse
import com.example.summery.network.ProductResponseDTO
import com.example.summery.network.RetrofitInstance
import com.example.summery.network.ReviewDTO
import com.example.summery.network.ReviewRequestDTO
import com.example.summery.network.ReviewResponseDTO

object ProductRepository{ // include all params .. Int: page
    suspend fun getProducts(
        query: String? = null,
        category: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        minNote: Double? = null,
        sellerId: Long? = null,
        promo: Boolean? = null,
        // Spring Boot's page
        //this stays
        size: Int? = null,

        //not you tho
        page: Int,

    ): Result<PageResponse<ProductResponseDTO>>{
        return ApiCall{
            RetrofitInstance.api.getAllProducts(
                query=query,
                page=page,
                category = category,
                minPrice = minPrice,
                maxPrice = maxPrice,
                promo = promo,
                size = size,
                sellerId = sellerId,
                minNote = minNote
                )
        }//.map {pageResponse ->
           // pageResponse.content?: emptyList()

    }

    //all categories for bottom sheet
    suspend fun getCategories(): Result<List<CategoryDTO>>{
        return ApiCall {
            RetrofitInstance.api.getAllCategories()
        }
    }

    suspend fun getBranches(): Result<Map<Long,String>>{
        return ApiCall {
            RetrofitInstance.api.getAllBranches()
        }
    }

    //woops, getProduct does NOT load its reviews hehe thankfully got an endpoint for that


    suspend fun getProductReviews(productId: Long): Result<List<ReviewDTO>> {
        return ApiCall {
            RetrofitInstance.api.getProductReviews(productId= productId)
        }
    }

    //review add
    suspend fun addReview(request: ReviewRequestDTO): Result<ReviewResponseDTO>{
        return ApiCall {
            RetrofitInstance.api.addReview(request)
        }
    }

    suspend fun getProductById(
        productId: Long
    ): Result<ProductResponseDTO>{
        return ApiCall {
            RetrofitInstance.api.getProductById(productId)
        }
    }

}







