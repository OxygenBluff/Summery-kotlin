package com.example.summery.uipages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.summery.data.OrderRepository
import com.example.summery.network.AddressResponseDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


//many features delete + add which is a form.
//NESTED ?
data class AddressesUiState(
    val addressesList: AddressListState = AddressListState(),
    val deleteDialog: DeleteAddressState= DeleteAddressState(),
    val addForm: AddNewAddressFormState= AddNewAddressFormState()
)

data class AddressListState(
    val isLoading: Boolean = false,
    val addresses: List<AddressResponseDTO> = emptyList(),
    val errorMessage: String? = null
)
data class DeleteAddressState(
    val selectedAddress: AddressResponseDTO? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val statusMessage: String? = null
)

data class AddNewAddressFormState(
    val street: String = "",
    val city: String = "",
    val postalCode: String = "",
    val country: String="Tunisia",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val statusMessage: String? = null
)


//ad or delete that's it
class MyAddressesViewModel(
    private val repository: OrderRepository //don't judge
): ViewModel() {

    private val _uiState = MutableStateFlow(
        AddressesUiState()
    )
    val uiState: StateFlow<AddressesUiState> = _uiState.asStateFlow()

    //1-Fetch addresses
    fun fetchAddresses(){
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    addressesList = it.addressesList.copy(
                        isLoading = true,
                        errorMessage = null,
                    )
                )//list itself still empty
            }
            repository.getMyAddresses()
                .onSuccess { list ->
                    _uiState.update {
                        it.copy(
                            addressesList = it.addressesList.copy(
                                addresses = list
                            )
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            addressesList = it.addressesList.copy(
                                errorMessage = error.message ?: "Failed to load addresses"
                            )
                        )
                    }
                }
            _uiState.update {
                it.copy(
                    addressesList = it.addressesList.copy(
                        isLoading = false
                    )
                )
            }

        }
    }

    init{
        fetchAddresses()
    }

    //2-add new address -> fields data from the add address form state !!
    fun addAddress() {
        //no doubles
        if (_uiState.value.addForm.isSubmitting) return;

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    addForm = it.addForm.copy(
                        isSubmitting = true,
                        errorMessage = null,
                        statusMessage = null
                    )
                )
            }
            val form = _uiState.value.addForm
            val requestDTO = AddressResponseDTO(
                id = null,
                street = form.street.trim(),
                city = form.city.trim(),
                postalCode = form.postalCode.trim(),
                country = form.country,
                principal = false,
            )

            repository.addAddress(requestDTO)
                .onSuccess { newAddress ->
                    _uiState.update {
                        it.copy(
                            //NEW ONE ENTIRELY just to clear to default values wow
                            addForm = AddNewAddressFormState(
                                statusMessage = "Address added."
                            ),
                            //add add add
                            addressesList= it.addressesList.copy(
                                addresses = it.addressesList.addresses + newAddress
                            )
                            //.. a mess
                        )
                    }
                }
                .onFailure {  error ->
                    _uiState.update {
                        it.copy(
                            addForm = it.addForm.copy(
                                errorMessage = error.message ?:"Failed to add address."
                            )
                        )
                    }
                }
            _uiState.update {
                it.copy(
                    addForm = it.addForm.copy(
                        isSubmitting = false
                    )
                )
            }
        }


    }

    //3-delete address
    fun deleteAddress(){
        val address=_uiState.value.deleteDialog.selectedAddress ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    deleteDialog = it.deleteDialog.copy(
                        isLoading = true,
                        selectedAddress =address
                    )
                )
            }
            repository.deleteAddress(address.id!!)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            deleteDialog = it.deleteDialog.copy(
                                statusMessage = "Address deleted.",
                                selectedAddress = null
                            ),
                            addressesList = it.addressesList.copy(
                                addresses = it.addressesList.addresses.filter{
                                    a-> a.id != address.id
                                }
                                //so.. a = CURRENT item as filter goes through them all
                                //address.id = the one you're filtering WITH (or against i guess)
                                //don't make them the same ffs lmao
                            )
                        )
                    }
                }
                .onFailure { error->
                    _uiState.update {
                        it.copy(
                            deleteDialog = it.deleteDialog.copy(
                                errorMessage = error.message ?:"Failed to delete the address",
                                selectedAddress = null
                            )
                        )
                    }
                }
            //either way
            _uiState.update {
                it.copy(
                    deleteDialog = it.deleteDialog.copy(
                       isLoading = false
                    )
                )
            }

        }
    }

    //tapping delete address -> selected not null -> Dialog
    fun onDeleteClicked(address: AddressResponseDTO){
        _uiState.update {
            it.copy(
                deleteDialog = it.deleteDialog.copy(
                    selectedAddress = address
                )
            )
        }
    }
    //the opposite
    fun dismissDeleteDialog(){
        _uiState.update {
            it.copy(
                deleteDialog = it.deleteDialog.copy(
                    selectedAddress = null
                )
            )
        }
    }

    //the onChanged for the stupid form fileds..
    fun onStreetChanged(value:String){
        _uiState.update {
            it.copy(
                addForm = it.addForm.copy(
                    street=value
                )
            )
        }
    }
    fun onCityChanged(value:String){
        _uiState.update {
            it.copy(
                addForm = it.addForm.copy(
                    city=value
                )
            )
        }
    }
    fun onZipCodeChanged(value:String){
        _uiState.update {
            it.copy(
                addForm = it.addForm.copy(
                    postalCode = value
                )
            )
        }
    }
    fun onCountryChanged(value:String){
        _uiState.update {
            it.copy(
                addForm = it.addForm.copy(
                    country = value
                )
            )
        }
    }
    //oh noo...
    fun clearListErrorMessage() = _uiState.update { it.copy(addressesList = it.addressesList.copy(errorMessage = null)) }
    fun clearDeleteStatusMessage() = _uiState.update { it.copy(deleteDialog = it.deleteDialog.copy(statusMessage = null)) }
    fun clearDeleteErrorMessage() = _uiState.update { it.copy(deleteDialog = it.deleteDialog.copy(errorMessage = null)) }
    fun clearAddFormStatusMessage() = _uiState.update { it.copy(addForm = it.addForm.copy(statusMessage = null)) }
    fun clearAddFormErrorMessage() = _uiState.update { it.copy(addForm = it.addForm.copy(errorMessage = null)) }

}

