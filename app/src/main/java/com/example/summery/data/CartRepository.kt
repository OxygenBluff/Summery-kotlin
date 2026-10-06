package com.example.summery.data

import com.example.summery.data.remote.api.ApiCall
import com.example.summery.network.AddToCartRequestDTO

import com.example.summery.network.CartDTO
import com.example.summery.network.CouponRequestDTO
import com.example.summery.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


object CartRepository {

    //SHARED STUFF WEE
    private val _cartState = MutableStateFlow<CartDTO?> (null)
    val cartState = _cartState.asStateFlow()

    //add item -> cart
    suspend fun addToCart(addToCartRequestDTO: AddToCartRequestDTO): Result<CartDTO>{
        val result = ApiCall {
            RetrofitInstance.api.addItemToCart(
                addToCartRequestDTO
            )
        }

        result.onSuccess { cart->
            _cartState.value=cart
        }
        return result
    }

    //get my cart
    //TODO this same method JUST saves it to cartState ooh kinda cache
    suspend fun getCart(): Result<CartDTO>{
        val result = ApiCall { RetrofitInstance.api.getCart() }

        result.onSuccess { cart->
            _cartState.value=cart
        }
        return result

    }
    //change quantity of an item
    suspend fun changeItemQuantity(
        itemId: Long,
        request: Int
    ): Result<CartDTO>{
        val result = ApiCall {
            RetrofitInstance.api.changeProductQuantity(
                itemId = itemId,
                request = request
            )
        }
        result.onSuccess { cart->
            _cartState.value=cart
        }
        return result
    }

    //delete
    suspend fun removeItem(
        itemId: Long
    ): Result<CartDTO>{
        val result = ApiCall {
            RetrofitInstance.api.removeItem(
                itemId = itemId
            )
        }
        result.onSuccess { cart->
            _cartState.value=cart
        }
        return result
    }

    //coupon
    suspend fun applyCoupon(
        code: String
    ): Result<CartDTO>{
        val result = ApiCall {
            RetrofitInstance.api.applyCoupon(
                CouponRequestDTO(code)
            )
        }
        result.onSuccess { cart->
            _cartState.value=cart
        }
        return result
    }

    //remove
    suspend fun removeCoupon(): Result<CartDTO>{
        val result = ApiCall {
            RetrofitInstance.api.removeCoupon()
        }
        result.onSuccess { cart->
            _cartState.value=cart
        }
        return result
    }

}