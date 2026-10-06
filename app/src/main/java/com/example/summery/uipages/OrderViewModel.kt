package com.example.summery.uipages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.summery.data.CartRepository
import com.example.summery.data.OrderRepository
import com.example.summery.data.remote.dto.OrderRequestDTO
import com.example.summery.data.remote.dto.OrderType
import com.example.summery.network.AddressResponseDTO
import com.example.summery.network.CartDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderViewModel(
) : ViewModel() {
    private val _selectedOrderType = MutableStateFlow("Delivery")
    val selectedOrderType = _selectedOrderType.asStateFlow()

    private val _statusMessage = MutableStateFlow("")
    val statusMessage = _statusMessage.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()

    //addresses
    private val _addresses = MutableStateFlow<List<AddressResponseDTO>>(emptyList())
    val addresses = _addresses.asStateFlow()

    private val _selectedAddress =MutableStateFlow<AddressResponseDTO?>(null)
    val selectedAddress = _selectedAddress.asStateFlow()

    //adding address form
    private val _addressForm = MutableStateFlow(AddAddressFormState())
    val addressForm = _addressForm.asStateFlow()

    private val _addingAddressLoading= MutableStateFlow(false)
    val addingAddressLoading = _addingAddressLoading.asStateFlow()

    private val _addingAddressErrorMessage= MutableStateFlow("")
    val addingAddressErrorMessage = _addingAddressErrorMessage.asStateFlow()

    private val _placeOderLoading = MutableStateFlow(false)
    val placeOrderLoading = _placeOderLoading.asStateFlow()


    private val _selectedPickupOption = MutableStateFlow<PickupTimeOption?>(null)
    val selectedPickupOption = _selectedPickupOption.asStateFlow()

    //THE PICKUP TIME ISO FINAL STRING
    private val _pickupTimeIsoString = MutableStateFlow<String?>(null)
    val pickupTimeIsoString = _pickupTimeIsoString.asStateFlow()

    fun updatePickupOption(option: PickupTimeOption) {
            _selectedPickupOption.value=option // directly changing the object hmmm is this good practice ?
            //THEN the final string from this
            _pickupTimeIsoString.value= option.getIsoTimeStamp()
    }


    //order code
    private val _orderCode = MutableStateFlow<String?>(null)
    val orderCode = _orderCode.asStateFlow()


    //SHARED
    val cartState :StateFlow<CartDTO?> = CartRepository.cartState

    //TODO RAM IS EMPTY THEN WHAT ?
    //wait but i can be null without restarting tho..
    init{
        viewModelScope.launch {
            CartRepository.getCart()
        }
        //ALWAYS NEW ONE buggg coupon wasn't applied


        //also addresses
        fetchUserAddresses()
    }



    fun updateAddressForm(transform: (AddAddressFormState) -> AddAddressFormState) {
        _addressForm.value = transform(_addressForm.value)
    }
    //TODO transform ?
    //THREAD SAFETY -> makes sure latest field values

    fun ChangeOrderType(newType:String){
        _selectedOrderType.value = newType
    }

    fun fetchUserAddresses(){
        viewModelScope.launch {
            OrderRepository.getMyAddresses()
                .onSuccess { list->
                    _addresses.value=list
                    //what IF one of them in the list is the principal ? preselect FEATURE ?
                    _selectedAddress.value= list.firstOrNull{it.principal} ?: list.firstOrNull()
                }
                .onFailure {
                    _errorMessage.value = it.message ?:"Failed to load the cart"
                }
        }
    }
    
    fun changeSelectedAddres(newAddress: AddressResponseDTO){
        _selectedAddress.value=newAddress
    }

    fun addAddress(address: AddressResponseDTO){
        if(!_addressForm.value.isFormValid) {
            _addingAddressErrorMessage.value ="All fields are required"
            return
        }
        //BOTH
        viewModelScope.launch {
            _addingAddressLoading.value=true
            OrderRepository.addAddress(address)
                .onSuccess {
                    _statusMessage.value="Address added successfully"
                    //fetch again..
                    fetchUserAddresses()
                }
                .onFailure {
                    _errorMessage.value = it.message ?:"Failed to add address"
                }
            _addingAddressLoading.value=false
        }

    }

    //this returns the dto with the code so here ig
    fun placeOrder(
        address: AddressResponseDTO?,
        couponCode: String?,
        //branchId: Long ,
        orderType: String,
        pickupTime: String? = null
    ){

        /*
        val validBranchId = branchId.value ?: run {
            _errorMessage.value = "Invalid or mixed seller branch in cart!"
            return
        }

         */
        //TODO RUN ??

        //aw hell nah get me the cart
        val cart = CartRepository.cartState.value
        val branches = cart?.items?.mapNotNull { it.branchId }?.distinct().orEmpty()

        val calculatedBranchId = if(branches.size==1) branches.first() else null //NULL if mixed or error

        if(calculatedBranchId==null){
            //only trigger when cart has something otherwise triggers on empty cart
            _errorMessage.value="Invalid of mixed seller branch in cart"
            return
        }

        viewModelScope.launch {
            _placeOderLoading.value=true

            val actualOrderType = when(orderType.lowercase()){
                "delivery" -> OrderType.DELIVERY
                "pickup" -> OrderType.PICKUP
                else -> throw IllegalStateException("Unexpected order type: $orderType")
            }

            val request = OrderRequestDTO(
                addressId = if(actualOrderType== OrderType.DELIVERY)address?.id else null,
                couponCode=couponCode,
                branchId=calculatedBranchId,
                orderType = actualOrderType,
                pickupTime = if(actualOrderType == OrderType.PICKUP)pickupTime else null
            )

            OrderRepository.placeOrder(request)
                .onSuccess { orderResponse ->
                    //_statusMessage.value="Your order has been placed!"
                    // dialog does it now

                    //codeee
                    val code = if(orderResponse.orderCode.isNullOrBlank()){
                        "#N/A"
                    }else{
                        orderResponse.orderCode
                    }
                    //null = ONLY not show, not UNKNOWN code

                    _orderCode.value=code


                    //sheesh REfetch cart.. for the order summary to disappear thingy
                    CartRepository.getCart()
                        .onFailure {
                            _errorMessage.value= it.message ?:"An internal error has occurred"
                        }

                }
                .onFailure {
                    _errorMessage.value= it.message ?:"Failed to place order"
                }
            _placeOderLoading.value=false
        }
    }

    fun updateAddressForm (field: MutableStateFlow<String>, newValue:String){
        field.value= newValue
    }

    fun clearStatusMessage(){
        _statusMessage.value=""
    }

    fun clearErrorMessage(){
        _errorMessage.value=""
    }

    fun clearAddingAddressErrorMessage(){
        _addingAddressErrorMessage.value=""
    }

    fun resetOrderCode(){
        _orderCode.value=null
    }


}
