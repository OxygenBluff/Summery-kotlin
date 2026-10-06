package com.example.summery.data.remote.dto

import com.example.summery.network.CustomizationDTO
import com.example.summery.network.VariantDTO
import com.google.gson.annotations.SerializedName

//alright CHECKOUT! or order ?
enum class OrderType {
    PICKUP, DELIVERY
}
data class OrderRequestDTO(
    val addressId: Long?= null, // pickup = nulllll
    val couponCode: String?=null,

    val branchId: Long,
    val orderType: OrderType,
    val pickupTime: String?= null
)

enum class OrderStatus{
    PENDING, //= waiting for PAYMENT!
    PAID, // then we go prepare the juice! LEMONAAAADE
    PREPARING, // no more processing :( JUICE BEING MADE NOW :)
    READY,
    DELIVERED,
    CANCELLED
}
data class OrderResponseDTO(
    val orderId: Long,
    val orderDate: String,
    val status: OrderStatus,

    @SerializedName("numeroCommande")
    val orderCode: String,
    val totalAmount: Double,
    val items: List<OrderItemResponseDTO>,

    val orderType: OrderType,
    val pickupTime: String?=null,
    val branchId: Long?=null,
    val branchName: String?=null,
    //+ address
    val deliveryAddress: String? = null,
)

data class OrderItemResponseDTO(
    val id: Long,
    val productName: String,
    val variants: List<VariantDTO>,
    val unitPrice: Double,
    val customizationCost: Double,
    @SerializedName("quantite")
    val quantity: Int,
    val subTotal: Double,
    val imageUrl: String,
    val customizations: List<CustomizationDTO>
)