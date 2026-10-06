package com.example.summery.uipages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.summery.data.CartRepository
import com.example.summery.network.CartDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel:ViewModel(){
    private val _cart= MutableStateFlow<CartDTO?>(null)
    val cart = _cart.asStateFlow()

    private val _isCartLoading = MutableStateFlow(true)
    val isCartLoading = _isCartLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()

    private val _statusMessage= MutableStateFlow("")
    val statusMessage = _statusMessage.asStateFlow()

    private val _applyingCodeLoading = MutableStateFlow(false)
    val applyingCodeLoading = _applyingCodeLoading.asStateFlow()

    //ALRIGHT restriction.. ONE BRANCH ID only otherwise IT'S A MESS
    //but using a giant @Transactonal in the future could be cool

    //TODO how tf do i even access that 💀
    val branchId: StateFlow<Long?> = _cart
        .map { cart -> //DTO? transforms it
            cart?.items?.map { item ->
                item.branchId
            }?.distinct()?.singleOrNull()
            //SINGLE OR NULL -> returns a single value IF only one disctinct value in the list
            //OTHERWISE RETURNS NULL which is WEIRD
        }
        .stateIn(//TODO KINDA LIKE CACHE: holds ONE 1 value in memory
            scope=viewModelScope,//viewmodel gone = this gone
            started = SharingStarted.WhileSubscribed(5000),
            //battery saving! ONLY recalcualtes when ui is on screen ? eh
            //5 seconds -> gone from screen under it = RE USES the last calcualted value
            initialValue = null
        )

    //RESTRICTIONNN
    val isCheckoutAllowed: StateFlow<Boolean> = branchId
        .map { id -> id != null }// again null if no disctinct single id (SingleOrNull)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )



    //get
    fun fetchCart(){
        viewModelScope.launch {
            _isCartLoading.value = true

            CartRepository.getCart()
                .onSuccess { _cart.value= it  }
                .onFailure {
                    _errorMessage.value = it.message ?:"Failed to load the cart"
                }
            _isCartLoading.value=false
        }
    }

    //update cart item
    fun incrementItemQuantity(
        itemId: Long,
        currentQuantity: Int
    ){
        viewModelScope.launch {
            CartRepository.changeItemQuantity(
                itemId = itemId,
                request = currentQuantity + 1

            ).onSuccess {
                //cart again i forgorrr
                fetchCart()
            }.onFailure {
                _errorMessage.value = it.message ?:"Failed to update Quantity"

            }
        }
    }

    fun decrementItemQuantity(
        itemId: Long,
        currentQuantity: Int
    ){
        //WAIT if current quantity = 1 remove i frogorr AISDE the delete button yh
        if(currentQuantity<=1){
            viewModelScope.launch {
                CartRepository.removeItem(itemId)
                    .onSuccess {
                        fetchCart()
                    }.onFailure {
                        _errorMessage.value = it.message ?: "Failed to update Quantity"
                    }
            }
        }else {
            viewModelScope.launch {
                CartRepository.changeItemQuantity(
                    itemId = itemId,
                    request = currentQuantity - 1

                ).onSuccess {
                    fetchCart()
                }.onFailure {
                    _errorMessage.value = it.message ?: "Failed to update Quantity"
                }
            }
        }
    }

    fun removeItem(itemId: Long){
        viewModelScope.launch {

            CartRepository.removeItem(itemId)
                .onSuccess {
                    fetchCart()
                }.onFailure {
            _errorMessage.value = it.message ?: "Failed to remove item"
            }

        }
    }


    init{
        fetchCart()

    }

    fun clearErrorMessage(){
        _errorMessage.value =""
    }

    fun clearStatusMessage(){
        _statusMessage.value=""
    }

    fun applyCoupon(coupon:String){
        viewModelScope.launch {
            _applyingCodeLoading.value = true
            CartRepository.applyCoupon(coupon)
                .onSuccess { updatedCart ->
                    _cart.value= updatedCart
                    _statusMessage.value = "Code applied!"
                }
                .onFailure {
                    _errorMessage.value = it.message ?: "Failed to apply coupon"
                }
            _applyingCodeLoading.value = false
        }
    }

    fun removeCoupon(){
        viewModelScope.launch {
            CartRepository.removeCoupon()
                .onSuccess { updatedCart ->
                    _cart.value=updatedCart
                    //too many messages
                }
                .onFailure {
                    _errorMessage.value = it.message ?: "Failed to remove coupon"
                }
        }
    }

}