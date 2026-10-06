package com.example.summery.uipages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.summery.data.ApiService
import com.example.summery.data.CartRepository
import com.example.summery.data.ProductRepository
import com.example.summery.network.AddToCartRequestDTO
import com.example.summery.network.CartDTO
import com.example.summery.network.ProductResponseDTO
import com.example.summery.network.ReviewDTO
import com.example.summery.network.ReviewRequestDTO
import com.example.summery.network.ReviewResponseDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

//for the review mainly


class ProductDetailsViewModel(
    private val apiService: ApiService ): ViewModel () {

    private val _reviews = MutableStateFlow<List<ReviewDTO>> (emptyList())
    val reviews = _reviews.asStateFlow()

    private val _loadingReviews = MutableStateFlow(true) // initially
    val loadingReviews = _loadingReviews.asStateFlow()

    //error
    private val _errorMessage = MutableStateFlow<String>("")
    val errorMessage = _errorMessage.asStateFlow()

    //cart itself result DTO

    private val _cartResult = MutableStateFlow<CartDTO?>(null)
    val cartResult = _cartResult.asStateFlow()

    //cart add
    private val _addToCartLoading = MutableStateFlow(false)
    val addToCartLoading = _addToCartLoading.asStateFlow()

    //product review add
    private val _addProductReviewLoading = MutableStateFlow(false)
    val addProductReviewLoading = _addProductReviewLoading.asStateFlow()

    //returns a response dto with creationDate and user name
    private val _reviewResult = MutableStateFlow<ReviewResponseDTO?>(null)
    val  reviewResult = _reviewResult.asStateFlow()

    //pagniating reviews.. 5 at a time then click -> Load more

    private val _reviewCount = MutableStateFlow(5)
    val reviewCount = _reviewCount.asStateFlow()

    //also ALL branches
    private val _branches = MutableStateFlow<Map<Long,String>>(emptyMap())
    val branches = _branches.asStateFlow()
    //TODO branch repo is better! this is redundant home view model already fetches it .. maybe later

    private val _product = MutableStateFlow<ProductResponseDTO?>(null)
    val product = _product.asStateFlow()

    //mhm
    fun fetchProduct(productId:Long){
        viewModelScope.launch {
            ProductRepository.getProductById(productId)
                .onSuccess { product->
                    _product.value=product
                }
                .onFailure { error->
                    _errorMessage.value=error?.message ?:"Failed to fetch this product."
                }
        }
    }
    fun fetchReviews(productId: Long){
        viewModelScope.launch {
            _loadingReviews.value=true

            //resettt please
            _reviewCount.value=5

            val result = ProductRepository.getProductReviews(productId = productId)

            result.onSuccess { reviews ->
                _reviews.value= reviews
            }
                .onFailure {
                    _reviews.value= emptyList()
                    val error=result.exceptionOrNull()
                    _errorMessage.update { error?.message ?: "an unexpected error occurred while loading reviews." }

                }

            _loadingReviews.value = false

        }
    }

    //load more reviews
    fun loadMoreReviews() {
        _reviewCount.update { currentCount -> currentCount +5 }
    }

    //adding product to cart
    fun addToCart(
        productId: Long,
        variantIds: List<Long>,
        customizationIds: List<Long>,
        quantity: Int =1 //TODO quantity customizer up down
    ){
        viewModelScope.launch {
            _addToCartLoading.value= true
            val result = CartRepository.addToCart(
                AddToCartRequestDTO(
                    productId = productId,
                    variantIds= variantIds,
                    customizationIds = customizationIds,
                    quantity = quantity
                )
            )

            //it returns a cartDTO
            result.onSuccess { cart ->
                _cartResult.value = cart
            }
                .onFailure {
                    val error=result.exceptionOrNull()
                    _errorMessage.update { error?.message ?: "Failed to add to cart" }
                }

            _addToCartLoading.value=false
        }
    }

    fun addProductReview(
         rating: Int,
         comment: String,
         productId: Long
    ){
        viewModelScope.launch {
            _addProductReviewLoading.value = true
            val result = ProductRepository.addReview(
                ReviewRequestDTO(
                    rating = rating,
                    comment = comment,
                    productId = productId
                )
            )

            result.onSuccess { response ->
                _reviewResult.value= response
            }
                .onFailure {
                    val error=result.exceptionOrNull()
                    _errorMessage.update { error?.message ?: "Failed to submit review" }
                }

            _addProductReviewLoading.value = false

        }
    }

    fun fetchBranches(){
        viewModelScope.launch {
            ProductRepository.getBranches()
                .onSuccess { _branches.value= it  }
        }
    }
    init {
        fetchBranches()// TODO again redundant..
    }

    //clearing stuff: cartLoading state + error oof
    fun clearCartResult(){
        _cartResult.value=null
    }

    fun clearErrorMessage(){
        _errorMessage.value=""
    }

    fun clearReviewResult () { _reviewResult.value=null}



}
