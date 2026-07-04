package com.example.summery.uipages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.summery.data.ApiService
import com.example.summery.data.ProductRepository
import com.example.summery.network.ApiCall
import com.example.summery.network.ProductResponseDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val apiService: ApiService
    //BUT now android doesn't know how to create the viewmodel itself in navhost
    //-> must put THIS:
    //override fun <T : ViewModel> create(modelClass: Class<T>): T {
    //return HomeViewModel(RetrofitInstance.api) as T
    //this is insane, overriding a 'create" function

): ViewModel () {
    //the UI vars
    private val _isFetchingProducts = MutableStateFlow(false)
    val isFetchingProducts: StateFlow<Boolean> = _isFetchingProducts.asStateFlow()

    private val _productsList = MutableStateFlow<List<ProductResponseDTO>>(emptyList()) // aha that's flow with list
    val productsList: StateFlow<List<ProductResponseDTO>> =_productsList.asStateFlow()

    private val _searchValue = MutableStateFlow<String>("")
    val searchValue: StateFlow<String> = _searchValue.asStateFlow()

    //error holder ?
    private val _statusMessage = MutableStateFlow("")
    val statusMessage: StateFlow<String> =_statusMessage.asStateFlow()

    //discounted
    private val _featuredProductsList = MutableStateFlow<List<ProductResponseDTO>>(emptyList())
    val featuredProductsList = _featuredProductsList.asStateFlow()

    //pagination
    private val _currentPage = MutableStateFlow(0)
    val currentPage = _currentPage.asStateFlow()

    private val _totalPages = MutableStateFlow(1)
    val totalPages = _totalPages.asStateFlow()


    //laoding the prducts
    //but the wrapper ApiCall -> retunrs Result<T> !!
    //and suspend -> scope
    fun handleLoadingProducts(
        animate: Boolean = true,
        page: Int =0
    ) {
        viewModelScope.launch {
            if(animate) {
                _isFetchingProducts.update { true }
            }
            //that triggered the transitions scren lmaoo

            val result= ProductRepository.getProducts(page=page)
            //UNWRAP
            val pageData = result.getOrNull()

            if(result.isSuccess){
                //now a pageResponse not list
                // .update = transactional! atomic !
                val newProducts = pageData?.content ?: emptyList()
                _productsList.update { newProducts
                }
                //pagination ones
                _totalPages.update{ pageData?.totalPages ?: 1 }
                _currentPage.update { page }
            }else{
                val error=result.exceptionOrNull()
                _statusMessage.update { error?.message ?: "an unexpected error occurred" }

            }
            _isFetchingProducts.update { false }
        }

    }

    fun updateStatusMessage(msg: String){
        _statusMessage.update { msg }
    }

    fun handleSearch(q:String){
        viewModelScope.launch {
            _isFetchingProducts.update { true }

            //list already exists wait
            _productsList.update { emptyList() }

            val result = ApiCall {
                apiService.getAllProducts(
                    query=q.ifBlank { null }, // shi.. it should be null
                    //.ifBlank that's cool
                    page=0,
                    size=20
                )
            }
            if(result.isSuccess){
                val pagedData = result.getOrNull()
                _productsList.update { pagedData?.content ?: emptyList() }
            }else{
                val error=result.exceptionOrNull()
                _statusMessage.update { error?.message ?: "Search failed, try again later" }
            }
        }
        _isFetchingProducts.update { false }
    }

    //featured products
    fun handleFeaturedProducts(){
        viewModelScope.launch {
            //no spinner
            ApiCall{
                apiService.getDiscountedProducts()
            }
                .onSuccess { products ->
                    _featuredProductsList.update { products }

                }
                .onFailure { error ->
                    //TODO error message accepted by the carousel ?

                }

        }
    }



}