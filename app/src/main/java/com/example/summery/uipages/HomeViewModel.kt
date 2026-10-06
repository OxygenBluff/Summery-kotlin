package com.example.summery.uipages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.summery.data.ApiService
import com.example.summery.data.ProductRepository
import com.example.summery.data.remote.ProductsPagingSource
import com.example.summery.data.remote.api.ApiCall
import com.example.summery.network.CategoryDTO
import com.example.summery.network.ProductResponseDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductFilters(
    val category: String? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val branchId: Long? = null,
    val discounted: Boolean? = false,
    val minRating: Double? = null,
    val query:String?=null
)

class HomeViewModel(
    private val apiService: ApiService
    //BUT now android doesn't know how to create the viewmodel itself in navhost
    //-> must put THIS:
    //override fun <T : ViewModel> create(modelClass: Class<T>): T {
    //return HomeViewModel(RetrofitInstance.api) as T
    //this is insane, overriding a 'create" function

): ViewModel () {

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

    //categories
    private val _categories = MutableStateFlow<List<CategoryDTO>>(emptyList())
    val categories =_categories.asStateFlow()

    //selected categories map i think or SET ? nooo it just accepsts one STRING ?
    private val  _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    //price

    //so.. the 7 sins..
    //comparion object = attach the constants to the CLASS
    //not a particular instance = STATIC ?
    companion object{
        const val MIN_PRICE = 0.0f
        const val MAX_PRICE = 500.0f
    }

    private val _selectedPriceRange = MutableStateFlow(MIN_PRICE..MAX_PRICE)
    val selectedPriceRange: StateFlow<ClosedFloatingPointRange<Float>> = _selectedPriceRange.asStateFlow()

    //branch, also one..
    private val _selectedBranchId = MutableStateFlow<Long?>(null)
    val selectedBranchId =_selectedBranchId.asStateFlow()

    //also ALL branches
    private val _branches = MutableStateFlow<Map<Long,String>>(emptyMap())
    val branches = _branches.asStateFlow()

    //min note -> normal slider not range one
    private val _minRating = MutableStateFlow<Double?>(null)
    val minRating = _minRating.asStateFlow()


    private val _discounted = MutableStateFlow(false)

    //branch id + selected category + min rating up
    //+ search bar value
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()


    //PRODUCTS PAGING
    //TODO it needs all 7 filters how tf do you combine those ??
    //combining 5 flows.. wow
    private val filtersFlow: Flow<ProductFilters> = combine(
        _selectedCategory,
        _selectedPriceRange,
        _selectedBranchId,
        _discounted,
        _minRating
    ){category, priceRange, branchId, discounted, minRating->
        ProductFilters(
            category = category,
            minPrice = priceRange.start.takeIf { it != MIN_PRICE }?.toDouble(),
            maxPrice = priceRange.endInclusive.takeIf { it != MAX_PRICE }?.toDouble(),
            branchId = branchId,
            discounted = discounted,
            minRating = minRating
        )

    }.combine(_searchQuery){ filters, query ->
        val finalFilters =
        filters.copy(
            query=query.takeIf { it.isNotBlank() }
        )
        finalFilters
    }

    val productPagingFlow: Flow<PagingData<ProductResponseDTO>> = filtersFlow
        .debounce (300) // delay between flow firings..
        .flatMapLatest { filters->
            Pager(
                config= PagingConfig(pageSize = 10),
                pagingSourceFactory = {
                    ProductsPagingSource(
                        productRepo = ProductRepository,
                        filters= filters
                    )
                }
            ).flow
        }
        .cachedIn(viewModelScope)




    //categories for bottom sheet
    fun fetchCategories(){
        viewModelScope.launch {
            ProductRepository.getCategories()
                .onSuccess { _categories.value = it }
        }
    }

    fun fetchBranches(){
        viewModelScope.launch {
            ProductRepository.getBranches()
                .onSuccess { _branches.value= it  }
        }
    }

    init{ //TODO hmm ?
        fetchCategories()
        //also branches
        fetchBranches()
    }

    //change selected category
    fun selectCategory(category: String?){
        val newCat = if(_selectedCategory.value == category) null else category
        _selectedCategory.value=newCat
    }

    fun selectBranch(branch: Long?){
        val newBranchId = if(_selectedBranchId.value==branch) null else branch
        _selectedBranchId.value=newBranchId
    }
    //price range filter
    fun onPriceRangeChanged(newRange: ClosedFloatingPointRange<Float>){
        _selectedPriceRange.value=newRange
    }
    //min rating filter
    fun selectMinRating(newMin: Double?){
        _minRating.value = newMin
    }

    //and finally RESET ALL
    fun resetAllFilters(){
        _minRating.value = null
        _selectedCategory.value=null
        _selectedPriceRange.value = MIN_PRICE..MAX_PRICE
        _selectedBranchId.value=null
    }


    fun updateStatusMessage(msg: String){
        _statusMessage.update { msg }
    }

    fun onSearchQueryChanged(newValue: String){
        _searchQuery.value=newValue
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