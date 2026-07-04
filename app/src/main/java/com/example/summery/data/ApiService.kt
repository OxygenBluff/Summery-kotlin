package com.example.summery.data

import AuthResponseDTO
import LoginRequestDTO
import RegisterRequestDTO
import com.example.summery.network.PageResponse
import com.example.summery.network.ProductResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET ("time") //suspend is the asyncn /time endpoint
    suspend fun getTime(): TimeResponse //returns an objcet! type of the one i just made huh
    //apparently to json automatically ?

    //so this takes the loginRequestDTO -> GSON makes it JSON text
    //THEN it passes the jSON to the http client + attaches THE CONTENT TYPE header
    //goes to the springBoto server AND parses the request  (200OK let's say) tojson again
    //THEN also wraps it to AuthResponseDTO bruh

    //LEMONAAADE actually!
    @POST("/api/auth/authenticate")
    suspend fun login(
        @Body request: LoginRequestDTO
    ):Response<AuthResponseDTO>

    @POST("/api/auth/register")
    suspend fun register(
        @Body request: RegisterRequestDTO
    ): Response<AuthResponseDTO>

    // PRODUCTS TODO, just getting all products
    @GET("api/products")
    suspend fun getAllProducts(
        @Query("q") query: String? = null,
        @Query("category") category: String? = null,
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null,
        @Query("minNote") minNote: Double? = null,
        @Query("sellerId") sellerId: Long? = null,
        @Query("promo") promo: Boolean? = null,
        // Spring Boot's page
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): Response<PageResponse<ProductResponseDTO>>
    //wrapped in Response = retrofit's type ://

    //oomf!
    @GET("/api/products/Featured")
    suspend fun getDiscountedProducts(): Response<List<ProductResponseDTO>>



}








