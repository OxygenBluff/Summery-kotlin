package com.example.summery.data

import AuthResponseDTO
import LoginRequestDTO
import RegisterRequestDTO
import com.example.summery.data.remote.dto.OrderRequestDTO
import com.example.summery.data.remote.dto.OrderResponseDTO
import com.example.summery.data.remote.dto.UpdateUserRequestDTO
import com.example.summery.data.remote.dto.UserResponseDTO
import com.example.summery.network.AddToCartRequestDTO
import com.example.summery.network.AddressResponseDTO
import com.example.summery.network.BranchResponseDTO
import com.example.summery.network.CartDTO
import com.example.summery.network.CategoryDTO
import com.example.summery.network.CouponRequestDTO
import com.example.summery.network.PageResponse
import com.example.summery.network.ProductResponseDTO
import com.example.summery.network.ReviewDTO
import com.example.summery.network.ReviewRequestDTO
import com.example.summery.network.ReviewResponseDTO
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

    //LEMONAAADE actually!
    @POST("/api/auth/authenticate")
    suspend fun login(
        @Body request: LoginRequestDTO
    ):Response<AuthResponseDTO>

    @POST("/api/auth/register")
    suspend fun register(
        @Body request: RegisterRequestDTO
    ): Response<AuthResponseDTO>

    //refresh token
    @POST("/api/auth/refresh-token")
    suspend fun refreshToken(
        @Header("Authorization") refreshToken: String
    ): Response<AuthResponseDTO>

    //oh wow.. retrofit wants a SYNCHORONOUS one for itself ..
    @POST("/api/auth/refresh-token")
    fun refreshTokenSync(
        @Header("Authorization") refreshToken: String
    ): Call<AuthResponseDTO>


    @Headers("Cache-Control: no-cache")
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

    //product by id
    @GET("/api/products/{id}")
    suspend fun getProductById(
        @Path("id") productId: Long
    ): Response<ProductResponseDTO>

    //categories -> for the bottom sheet filtering thingy
    @GET ("/api/categories") // MOSTLY root categories tho
    suspend fun getAllCategories(): Response<List<CategoryDTO>>

    ///also filtering -> by seelerid mapped into seller name (branch)
    @GET ("/api/branches")
    //ohh key is string -> must convert to int and use it gson DOES IT
    //AUTOMATICALLY ?
    suspend fun getAllBranches(): Response<Map<Long,String>> // wow


    //oomf!
    @GET("/api/products/Featured")
    suspend fun getDiscountedProducts(): Response<List<ProductResponseDTO>>

    //review
    @GET("/api/reviews/product/{productId}")
    suspend fun getProductReviews(
        @Path("productId") productId: Long //TODO that's how to inject it !!
    ): Response<List<ReviewDTO>>

    //add item to cart
    @POST ("/api/cart/items")
    suspend fun addItemToCart(
        @Body request: AddToCartRequestDTO
    ): Response<CartDTO>


    //review add
    @POST("/api/reviews")
    suspend fun addReview(
        @Body request: ReviewRequestDTO
    ): Response<ReviewResponseDTO>

    //give cart
    @GET ("api/cart")
    suspend fun getCart(): Response<CartDTO>

    // cart quantity +1 /-1 put
    @Headers("Content-Type: application/json")
    @PUT ("/api/cart/items/{itemId}")
    suspend fun changeProductQuantity(
        @Path("itemId") itemId: Long,
        @Body request: Int
    ): Response<CartDTO>

    //remove it
    @DELETE ("/api/cart/items/{itemId}")
    suspend fun removeItem(
        @Path("itemId") itemId: Long,
    ): Response<CartDTO>

    //Coupon
    @POST ("/api/cart/coupon")
    suspend fun applyCoupon(
        @Body request: CouponRequestDTO
    ): Response<CartDTO>

    //remove it
    @DELETE ("/api/cart/coupon")
    suspend fun removeCoupon(): Response<CartDTO>

    //Order officially
    @POST ("/api/orders")
    suspend fun placeOrder(
        @Body request: OrderRequestDTO
    ): Response<OrderResponseDTO>

    //list of my orders
    //@GET ("/api/orders/my")
    //suspend fun getMyOrders(): Response<List<OrderResponseDTO>>

    //order by id
    @GET ("/api/orders/{id}")
    suspend fun getOrderDetails(
        @Path("id") id: Long
    ): Response<OrderResponseDTO>
    // in backend -> " Insecure Direct objcet Reference " VULNERABILITY::
    // MUST check the id correpsonds to the current user's order Id!!
    //what if someone types a random id here ? DONE

    //Addresses
    @GET ("/api/addresses/me")
    suspend fun getMyAddresses(): Response<List<AddressResponseDTO>>

    @POST ("/api/addresses")
    suspend fun addAddress(
        @Body address: AddressResponseDTO // technically not a response but pff
    ): Response<AddressResponseDTO>


    //part 2
    @GET ("/api/branches/all")
    suspend fun getBranches(): Response<List<BranchResponseDTO>>

    //by if
    @GET ("/api/branches/{id}")
    suspend fun getBranchById(
        @Path("id") id: Long
    ):Response<BranchResponseDTO>

    //MY ORDERS ACCOUNT SCREEN OFFICIALLY -> + sort paramter
    @GET ("/api/orders/my")
    suspend fun getMyOrders(
        @Query("page") page: Int =0,
        @Query("size") size: Int =10,
        @Query("sort") sort: String ="daeCommande"
    ): Response<PageResponse<OrderResponseDTO>>

    //my user info
    @GET ("/api/users/me")
    suspend fun getMyAccountInformation(): Response<UserResponseDTO>

    //update my user info
    @PATCH ("/api/users/me")
    suspend fun updateMyAccountInformation(
         @Body request: UpdateUserRequestDTO
    ): Response<UserResponseDTO>

    //delete an address
    @DELETE ("/api/addresses/me/{id}")
    suspend fun deleteAddress(
        @Path("id") id: Long
    ): Response<Unit>


}








