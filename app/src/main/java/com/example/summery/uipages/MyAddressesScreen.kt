package com.example.summery.uipages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.summery.AppToast
import com.example.summery.CustomTextField
import com.example.summery.DialogMenu
import com.example.summery.ScreenTransition
import com.example.summery.network.AddressResponseDTO
import com.example.summery.ui.components.yellow
import com.example.summery.white


@Composable
fun MyAddressesScreen(
    viewModel: MyAddressesViewModel,
    navController: NavController
){
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var isToastError by remember { mutableStateOf(false) }


    val uiState by viewModel.uiState.collectAsStateWithLifecycle()



    Box(
        modifier= Modifier.fillMaxSize()
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFFFF89F)
        ) {
            ScreenTransition(
                withSpinner = false,
                delayMillis = 0,
                isDataLoading = false,
            ) {
                LazyColumn(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxHeight()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)

                ) {
                    //Checkout <-
                    stickyHeader {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(yellow)
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back icon",
                                tint = Color.Black.copy(0.7f),
                                modifier = Modifier
                                    .size(26.dp)
                                    .clickable {
                                        navController.popBackStack()
                                    }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Saved Addresses",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontFamily = FontFamily.SansSerif,
                            )
                        }

                    }//end of sticky header

                    item{
                        Text(
                            text = "Add or remove delivery addresses associated with your account",
                            style= MaterialTheme.typography.bodyMedium
                        )
                    }

                    items(
                        items=uiState.addressesList.addresses,
                        key = { address -> address.id!! },
                        //TODO hmm ? why would id ever be null bruh

                    ){address->
                        AddressCard(
                            modifier = Modifier.animateItem(),
                            address = address,
                            onDeleteClick = {
                                viewModel.onDeleteClicked(address)
                            }
                        )
                    }

                    //finally add new address, hmm just under
                    item {
                        AddAddressForm(
                            formState = uiState.addForm,
                            onStreetChanged = { viewModel.onStreetChanged(it) },//wow it really has it
                            onCityChanged = { viewModel.onCityChanged(it) },
                            onZipCodeChanged = { viewModel.onZipCodeChanged(it) },
                            onCountryChanged = { viewModel.onCountryChanged(it) },
                            onSubmit = { viewModel.addAddress() }
                        )
                    }

                }
            }
        }

        //dialog trigger
        if(uiState.deleteDialog.selectedAddress!=null){
            ConfirmDeletionDialog(
                onDismissRequest ={ viewModel.dismissDeleteDialog()},
                onDeleteClick = {viewModel.deleteAddress()},
                onCancelClick = {viewModel.dismissDeleteDialog()}
            )
        }


        //toast last
        AppToast(
            message = toastMessage,
            isVisible = showToast,
            isError = isToastError,
            onDismiss = {
                showToast = false

            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 80.dp)
        )
        LaunchedEffect(uiState.addressesList.errorMessage) {
            if (uiState.addressesList.errorMessage?.isNotBlank() == true) {
                toastMessage = uiState.addressesList.errorMessage!!
                isToastError = true
                showToast = true
                viewModel.clearListErrorMessage()
            }
        }


        LaunchedEffect(uiState.deleteDialog.statusMessage) {
            if (uiState.deleteDialog.statusMessage?.isNotBlank() == true) {
                toastMessage = uiState.deleteDialog.statusMessage!!
                isToastError = false
                showToast = true
                viewModel.clearDeleteStatusMessage()
            }
        }
        LaunchedEffect(uiState.deleteDialog.errorMessage) {
            if (uiState.deleteDialog.errorMessage?.isNotBlank() == true) {
                toastMessage = uiState.deleteDialog.errorMessage!!
                isToastError = true
                showToast = true
                viewModel.clearDeleteErrorMessage()
            }
        }


        LaunchedEffect(uiState.addForm.statusMessage) {
            if (uiState.addForm.statusMessage?.isNotBlank() == true) {
                toastMessage = uiState.addForm.statusMessage!!
                isToastError = false
                showToast = true
                viewModel.clearAddFormErrorMessage()
            }
        }

    }//end of box
}

@Composable
fun AddressCard(
    modifier:Modifier=Modifier,
    address: AddressResponseDTO,
    onDeleteClick:(Long) -> Unit
){
    Surface(
        onClick = {
           //ehh
        },
        shape = RoundedCornerShape(20.dp),
        color = white.copy(0.7f),
        border = BorderStroke(
            width = 0.dp,
            color = Color(0xFFEEEEEE)
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = address.street,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        color = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${address.city}, ${address.postalCode}",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.SansSerif,
                        color = Color.Gray
                    )
                )
            }

            if (address.principal) {
                Surface(
                    color = Color.Black,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = "Default",
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 8.dp
                        ),
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Yellow
                        )
                    )
                }
            }

            //wait delete
            Icon(
                imageVector = Icons.Outlined.DeleteOutline,
                tint = Color.Black,
                contentDescription = "delete this saved address",
                modifier = Modifier
                    .size(28.dp)
                    .clickable{
                        onDeleteClick(address.id!!)
                    }
            )

        }//end of row
    }
}

@Composable
fun ConfirmDeletionDialog(
    onDismissRequest:()-> Unit,
    onDeleteClick: () -> Unit,//just viewModel + pass it from above
    onCancelClick: ()-> Unit
) {
    DialogMenu(
        modifier = Modifier
            .fillMaxWidth(0.9f)
        ,//wrap content height
        onDismissRequest =  onDismissRequest,

        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Delete",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Are you sure you want to delete this address? Deleted addresses cannot be restored but you can add them again anytime.",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        onDeleteClick()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE35555),
                        contentColor = White
                    ),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        "Delete",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick =  onCancelClick ,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.Yellow
                    ),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        "Cancel",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

        }
    }
}

@Composable
fun AddAddressForm(
    formState: AddNewAddressFormState,
    onStreetChanged:(String) -> Unit,
    onCityChanged:(String) -> Unit,
    onZipCodeChanged:(String) -> Unit,
    onCountryChanged:(String) -> Unit,
    onSubmit:() -> Unit
){
    Surface(
        //sshape = RoundedCornerShape(20.dp),
        //shadowElevation = 1.dp,
        color = Color.Transparent,//Color(0xFFFDF489), was copy0.7f
        modifier = Modifier
            .padding(top = 8.dp)
            .wrapContentHeight()

    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .navigationBarsPadding()
                .padding(0.dp),
            verticalArrangement = Arrangement.spacedBy ( 8.dp )
        ) {
            Text(
                text = "Add a delivery address",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            //1-Street
            CustomTextField(
                value = formState.street,
                onValueChange = { newStreet ->
                    onStreetChanged(newStreet)
                },
                onSearchAction = {},
                placeholder = "Street Address",
                modifier = Modifier,
                isPasswordField = false,
                isSearchField = false,
                maxLines = 1,
                //color=Color(0xFFF6F6F2)//original white ? yep
            )
            //city
            CustomTextField(
                value = formState.city,
                onValueChange = { newCity ->
                    onCityChanged(newCity)
                },
                onSearchAction = {},
                placeholder = "City",
                modifier = Modifier,
                isPasswordField = false,
                isSearchField = false,
                maxLines = 1
            )
            //ZipCode
            CustomTextField(
                value = formState.postalCode,
                onValueChange = {newCode ->
                    onZipCodeChanged(newCode)
                },
                onSearchAction = {},
                placeholder = "Zip Code ####",
                modifier = Modifier,
                isPasswordField = false,
                isSearchField = false,
                maxLines = 1
            )
            //Country
            CustomTextField(
                value = formState.country,
                onValueChange = {newCountry ->
                    onCountryChanged(newCountry)
                },
                onSearchAction = {},
                placeholder = "Country",
                modifier = Modifier,
                isPasswordField = false,
                isSearchField = false,
                maxLines = 1
            )

            val isFormValid= listOf(
                formState.street,formState.city,formState.postalCode,formState.country
            ).all {
                it.isNotBlank()
            }


            //add
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(8.dp),

                contentPadding = PaddingValues(
                    horizontal = 11.dp,
                    vertical = 4.dp
                ),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color(0xFFE8D755),
                    disabledContainerColor = Color(0xFF3A3939)
                ),
                enabled = !formState.isSubmitting && isFormValid,

                onClick = {
                    onSubmit()
                },
            ) {
                if(formState.isSubmitting){
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.Yellow,
                        strokeWidth = (4.5).dp
                    )
                }else {
                    Text(
                        text = "Add address",
                        color = Color.Yellow,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

        }
    }


}