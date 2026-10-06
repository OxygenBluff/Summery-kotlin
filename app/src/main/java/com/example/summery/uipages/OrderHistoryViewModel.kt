package com.example.summery.uipages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.summery.data.OrderRepository
import com.example.summery.data.remote.OrdersPagingSource
import com.example.summery.data.remote.dto.OrderResponseDTO
import com.example.summery.network.PageResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch


//UI STATE YESS
sealed interface OrderHistoryUiState{
    //loading suree...
    data object Loading: OrderHistoryUiState

    //Success state -> always has ACTUAL DATA
    data class Success(
        val orders: PageResponse<OrderResponseDTO>
    ): OrderHistoryUiState

    //Error state
    data class Error(
        val message: String
    ): OrderHistoryUiState
}


class OrderHistoryViewModel(
): ViewModel(){

    //paging 3 syntax is WEIRDDD
    //-> it's a cold flow type, uses the Pager constructor?


    //sorting i guess
    private val _selectedSort = MutableStateFlow<OrderSortOptions?>(null)
    val selectedSort = _selectedSort.asStateFlow()


    //selected item for the dialog
    private val _selectedOrder = MutableStateFlow<OrderResponseDTO?>(null)
    val selectedOrder = _selectedOrder.asStateFlow()


    //omg paging 3 needs flat map to apply the sorting whyyy

    val ordersPagingFlow: Flow<PagingData<OrderResponseDTO>> = _selectedSort
        .flatMapLatest {  sortOption ->
            Pager(
                config = PagingConfig(
                pageSize = 8,
                prefetchDistance = 2,
                enablePlaceholders = false,
                initialLoadSize = 8 // let's do 2x not 3x TODO later
                 )
            ){
                //then here it is passed, this mess
                OrdersPagingSource(
                    orderRepo = OrderRepository,
                    sortingOption = sortOption?.parameter ?: OrderSortOptions.DATE.parameter
                )
            }.flow
        }
        .cachedIn(viewModelScope)

    //flatmap -> transforms a value in a NEW FLOW , + flattens all nested flows into
    //ONE single output stream

    //why latestMap ?
    //with sortingOption : changing a STATE flow to a PAGING FLOW
    //cancels the previous pager flow .. + switches to a new flow




    //alright, get them
    //the loading one first
    private val _uiState = MutableStateFlow<OrderHistoryUiState>(OrderHistoryUiState.Loading)

    //public, ui read
    val uiState: StateFlow<OrderHistoryUiState> = _uiState.asStateFlow()



    //TODO paging 3 has its own ones..
    fun loadOrders(){
        viewModelScope.launch {
            //loading first
            _uiState.value= OrderHistoryUiState.Loading

            OrderRepository.getMyOrders(
                sortingOption = _selectedSort.value?.parameter ?: OrderSortOptions.DATE.parameter
            )
                .onSuccess { orders ->
                    //give it to the Success constructor !!!
                    _uiState.value= OrderHistoryUiState.Success(
                        orders = orders
                    )
                }
                .onFailure { error ->
                    _uiState.value = OrderHistoryUiState.Error(
                        message=error.message ?:"An unexpected error occurred"
                    )
                }
        }
    }


    fun onSortChanged(newSortingOption: OrderSortOptions){
        if(_selectedSort.value == newSortingOption) {//same one
            _selectedSort.value = null
        }else{
            _selectedSort.value=newSortingOption

        }
    }

    //selected an order
    fun onOrderClicked(order: OrderResponseDTO){
        _selectedOrder.value = order
    }

    fun OndismissOrderDetails(){
        _selectedOrder.value=null
    }





}