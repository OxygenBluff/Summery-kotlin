package com.example.summery.uipages

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.AddLocation
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.summery.AppToast
import com.example.summery.BottomSheet
import com.example.summery.CustomChip
import com.example.summery.CustomTextField
import com.example.summery.DialogMenu
import com.example.summery.R
import com.example.summery.ScreenTransition
import com.example.summery.network.AddressResponseDTO
import com.example.summery.ui.components.yellow
import com.example.summery.white
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrderScreen(
    modifier: Modifier=Modifier,
    orderViewModel: OrderViewModel,
    navController: NavController

){
    //toast stuff
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var isToastError by remember { mutableStateOf(false) }

    val  statusMessage by orderViewModel.statusMessage.collectAsState()
    val errorMessage by orderViewModel.errorMessage.collectAsState()
    val orderType by orderViewModel.selectedOrderType.collectAsState()

    val userAddresses by orderViewModel.addresses.collectAsState()

    val selectedAddress by orderViewModel.selectedAddress.collectAsState()

    val cart by orderViewModel.cartState.collectAsState()

    //copy your order code!!
    var showOrderDialog by remember { mutableStateOf(false) }
    val orderCode by orderViewModel.orderCode.collectAsStateWithLifecycle()


    Box(
        modifier= Modifier.fillMaxSize()
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFFFF89F)
        ) {
            ScreenTransition(
                withSpinner = false,
                delayMillis = 50// TODO change to when just done loading ?
            ) {
                LazyColumn(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxHeight()
                        .padding(16.dp)

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
                                        // back ONE screen popBackStack
                                    }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Checkout",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontFamily = FontFamily.SansSerif,
                            )
                        }

                    }//end of sticky header

                    // PICKUP VS DELIVERY TOGGLE YESS
                    item {

                        val toggleOptions = listOf(
                            toggleOption(
                                optionName = "Delivery",
                                onOptionSelected = {
                                    orderViewModel.ChangeOrderType("Delivery")
                                }
                            ),
                            toggleOption(
                                optionName = "Pickup",
                                onOptionSelected = {
                                    orderViewModel.ChangeOrderType("Pickup")
                                }
                            )
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            CustomToggle(
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .align(Alignment.CenterHorizontally),
                                options = toggleOptions,
                                selectedOptionName = orderType
                            )

                        }
                    }//end of toggle

                    item {
                        //Animated content ? hmm
                        AnimatedContent(
                            targetState = orderType,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(durationMillis = 200))
                                    .togetherWith(
                                        fadeOut(animationSpec = tween(durationMillis = 150))
                                    ) using SizeTransform(clip = false)
                                //it uses a size transform BY DEFAULT lol 
                                //together with lmao
                            },
                        ) { targetType -> // cannot use the targetState huh
                            when (targetType) {
                                "Delivery" -> {
                                    Column(
                                        modifier=Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            //shadowElevation = 1.dp,
                                            color = white.copy(0.7f),//Color(0xFFFDF489),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp)

                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .padding(0.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {

                                                /////////////
                                                var showBottomSheet by remember { mutableStateOf(false)}

                                                OutlinedTextField(
                                                    value = selectedAddress?.let { "${it.street}, ${it.city}" } ?: "Select an address",
                                                    //actually genius, .let on both to display both WOW
                                                    onValueChange = {},
                                                    readOnly = true,
                                                    enabled=false,
                                                    textStyle = TextStyle(
                                                        fontSize = 16.sp,
                                                        fontFamily = FontFamily.SansSerif,
                                                        fontWeight = FontWeight.Medium,
                                                        color = if (selectedAddress != null) Color(0xFF1E1E1E) else Color(0xFF8C8C8C)
                                                    ),

                                                    trailingIcon = {
                                                        Icon(
                                                            imageVector = Icons.Outlined.KeyboardArrowDown,
                                                            contentDescription = "Address selector"
                                                        )
                                                    },
                                                    shape = RoundedCornerShape(16.dp),
                                                    colors = OutlinedTextFieldDefaults.colors(
                                                        focusedBorderColor = Color.Transparent,
                                                        unfocusedBorderColor = Color.Transparent,
                                                        disabledBorderColor = Color.Transparent,
                                                        // container
                                                        focusedContainerColor = white.copy(0.7f),
                                                        unfocusedContainerColor = white.copy(0.7f),
                                                        // but disabeld
                                                        disabledContainerColor = white.copy(0.7f),
                                                        disabledTrailingIconColor = Color(0xFF1E1E1E)
                                                    ),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable{ showBottomSheet = true }
                                                        .padding(16.dp)
                                                )
                                                addressSelectorSheet(
                                                    addresses = userAddresses,
                                                    selectedAddress =selectedAddress,
                                                    onAddressSelected = { newAddress ->
                                                        orderViewModel.changeSelectedAddres(newAddress = newAddress)
                                                    },
                                                    modifier = Modifier,
                                                    showBottomSheet =  showBottomSheet,
                                                    onDismissRequest = {showBottomSheet=false}
                                                )



                                                //2-add address button
                                                var isFormVisible by remember {  mutableStateOf(false) }
                                                Row(
                                                    modifier = Modifier
                                                        .wrapContentSize()
                                                        .padding(start=16.dp,bottom=16.dp)
                                                        .clickable{
                                                            isFormVisible=!isFormVisible
                                                            },
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                ) {
                                                    Text(
                                                        text = "Add New Address",
                                                        color = Color.Black,
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        modifier = Modifier
                                                            //.padding(start=16.dp,)
                                                    )

                                                    Icon(
                                                        imageVector = Icons.Outlined.AddLocation,
                                                        tint = Color.Black,
                                                        contentDescription = "Add new delivery address",
                                                        modifier = Modifier
                                                            .size(22.dp)
                                                    )


                                                }//
                                                AnimatedVisibility(
                                                    visible = isFormVisible,

                                                    enter = expandVertically(
                                                        animationSpec = tween(
                                                            durationMillis = 250,
                                                            easing = LinearOutSlowInEasing
                                                        ), expandFrom = Alignment.Top
                                                    )
                                                            + fadeIn(
                                                        animationSpec = tween(
                                                            durationMillis = 250
                                                        )
                                                    ),
                                                    exit = shrinkVertically(
                                                        animationSpec = tween(
                                                            durationMillis = 150,
                                                            easing=LinearOutSlowInEasing
                                                        ), shrinkTowards = Alignment.Top
                                                    )
                                                            + fadeOut(
                                                                animationSpec = tween(
                                                                    durationMillis = 150
                                                                )
                                                    ),
                                                ){
                                                    AddNewAddressForm(
                                                        viewModel = orderViewModel
                                                    )
                                                }


                                            }
                                        }

                                        OrderBriefSummary(
                                            viewModel = orderViewModel,
                                            orderType = orderType,
                                            selectedAddress = selectedAddress,
                                            selectedOption = null,
                                            couponCode =cart?.appliedCouponCode ,
                                            pickupTime = null
                                        )
                                    }
                                }

                                /// P I C K U P
                                "Pickup" -> {

                                    val selectedTimeOption by orderViewModel.selectedPickupOption.collectAsStateWithLifecycle()

                                    val pickupTimeIso by orderViewModel.pickupTimeIsoString.collectAsStateWithLifecycle()
                                    Column(
                                        modifier=Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {

                                        TimePicker(
                                            selectedOption = selectedTimeOption,
                                            onOptionSelected ={ option ->
                                                orderViewModel.updatePickupOption(option)
                                            },
                                            modifier = Modifier
                                        )

                                        OrderBriefSummary(
                                            viewModel = orderViewModel,
                                            orderType = orderType,
                                            selectedAddress = null,
                                            couponCode = cart?.appliedCouponCode,
                                            selectedOption = selectedTimeOption,
                                            pickupTime =  pickupTimeIso
                                        )
                                    }
                                }
                            }
                        }
                    }


                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }//end of lazy column



            }

        }//end of main surface
        LaunchedEffect(statusMessage) {
            if(statusMessage.isNotBlank()) {
                toastMessage = statusMessage
                isToastError = false
                showToast = true

                orderViewModel.clearStatusMessage()
            }
        }

        LaunchedEffect(errorMessage) {
            if(errorMessage.isNotBlank()){
                toastMessage = errorMessage
                isToastError = true
                showToast = true

                orderViewModel.clearErrorMessage()
            }
        }

        //ORDER DIALOG TIME
        //TODO what if you get an order with somehow orderCode = null ??

        orderCode?.let {
            orderDialog(
                modifier = Modifier,
                orderCode = it,
                onDismissRequest = {
                    showOrderDialog = false
                    orderViewModel.resetOrderCode()
                }
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
    }

}

//order dialog + copiable
@Composable
fun orderDialog(
    modifier:Modifier=Modifier,
    orderCode: String,
    onDismissRequest:() -> Unit, //same as okay button lambda anyways
){
    val context = LocalContext.current
    DialogMenu(
        modifier = modifier
            .fillMaxWidth(0.9f)
            , //DEFAULT = WRAP CONTENT HEIGHT for column or box
        onDismissRequest =  onDismissRequest,

        ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Order placed!",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Congratulations on placing your order! make sure to save your order code as shown below.",
                style = MaterialTheme.typography.bodyMedium
            )

            //show it + our signature copy
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val fallbackOrderCode = "#N/A"
                Text(
                    text = orderCode,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black.copy(0.2f))
                        .padding(4.dp)
                        .clickable {
                            if (!orderCode.isNullOrBlank() || orderCode.equals(fallbackOrderCode)) {
                                val clipboard =
                                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                                val clipData = ClipData.newPlainText("Order Code", orderCode)
                                clipboard.setPrimaryClip(clipData)
                            }
                        },
                    contentAlignment = Alignment.Center,

                    ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        tint = Color.Black,
                        contentDescription = "Copy order code",
                    )
                }
            }


            //JUST to remove the external padding WTF GOOGLE
            CompositionLocalProvider(
                LocalMinimumInteractiveComponentSize provides Dp.Unspecified
            ) {

                Button(
                    onClick = onDismissRequest,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.Yellow
                    ),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        "Okay",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

        }

    }//end of dialog
}

//ENUMS -> values() exists already ..

data class PickupTimeOption (
    val label: String, //ASAP
    val timeToAdd: Long, //15 Mins
    //on tapped -> viewModel change selected time U CAN DO = {} for the FUNCTION YES
    //JUST RETURN A READY STRING FOR SPRINGBOOT YES YES EYS
    val getIsoTimeStamp:() -> String = {
        LocalDateTime.now().plusMinutes(timeToAdd)
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimePicker(
    selectedOption: PickupTimeOption?,
    onOptionSelected: (PickupTimeOption) -> Unit,
    modifier: Modifier = Modifier
){

    //options
    val pickupTimeOptions= listOf(
        PickupTimeOption(label="ASAP", timeToAdd = 15L),
        PickupTimeOption(label="In 30 Mins", timeToAdd = 30L),
        PickupTimeOption(label="In 45 Mins", timeToAdd = 45L),
        PickupTimeOption(label="In 1 Hour", timeToAdd = 60L),
        PickupTimeOption(label="In 2 Hours", timeToAdd = 120L),
        PickupTimeOption(label="In 4 Hours", timeToAdd = 240L),
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        //shadowElevation = 1.dp,
        color = white.copy(0.7f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(12.dp),// was 16
        ) {
            Text(
                text="Select a pickup time",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
            )

            FlowRow(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                //verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                pickupTimeOptions.forEach { option ->
                    //HOUR MINUTE only derived from the options's timeToAdd simple
                    val displayTime = LocalDateTime.now()
                        .plusMinutes(option.timeToAdd)
                        .format(DateTimeFormatter.ofPattern("HH:mm"))


                    val isSelected = option.label == selectedOption?.label
                    //TODO objcet eqaulity sutff but a lmabda tho hmm
                    
                    CustomChip(
                        modifier = Modifier,
                        label = "${option.label} ($displayTime)",
                        selectable = true,
                        selected = isSelected,
                        icon =Icons.Outlined.Timer,//iconTint default alreadt yellow else gray
                        ContainerColor = white.copy(0.9f),
                        LabelColor = Color.Black,
                        SelectedContainerColor = Color.Black,
                        SelectedIconColor = Color.Yellow,
                        SelectedLabelColor = Color.Yellow,
                        onClick = {
                            onOptionSelected(option)
                        }
                    )
                }


            }
        }
    }
}

data class AddAddressFormState(
    val street: String= "",
    val city: String = "",
    val zipCode: String = "",
    val country: String = "Tunisia"
)
{
    //TODO i aint' wriging 4 .isNotBlank.. so LOOK AT THISSS  helper function
    //TODO using .all on the LIST WOW hen passing it.isNotBlank
    val isFormValid: Boolean
        get() = listOf(street,city,zipCode,country).all{it.isNotBlank()}
    //get() = custom GETTER
    //this also makes it dynamically calcualte it anytime u aceess state.isFormValid .. h

    //TODO basically COMPOUND property = get() USE IT
}

//DATA CLASS = IMMUTABLE  !! cannot just do .field =.. 
//.copy ->  then just speicify field = ...  otherwise WRITE EVERY FIELD




@Composable
fun addressSelectorSheet(
    addresses: List<AddressResponseDTO>,
    selectedAddress: AddressResponseDTO?,
    onAddressSelected: (AddressResponseDTO) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    showBottomSheet:Boolean
){
    //nope bottom sheet better


    if(showBottomSheet) {
        BottomSheet(
            onDismissRequest = { onDismissRequest() },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Select an address",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    if (addresses.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp, horizontal = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_lemon),
                                        contentDescription = "No address found",
                                        colorFilter = ColorFilter.tint(Color.Black),
                                        modifier = Modifier.size(30.dp)
                                    )
                                    Spacer(Modifier.width(2.dp))
                                    Icon(
                                        imageVector = Icons.Outlined.QuestionMark,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            Text(
                                text = "No addresses found",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF222222)
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "The lemons appear to be a bit lost..",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.SansSerif,
                                    color = Color(0xFF222222)
                                ),
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                    }else{
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(addresses) { address ->
                                val isSelected = address.id == selectedAddress?.id

                                Surface(
                                    onClick = {
                                        onAddressSelected(address)
                                        onDismissRequest()
                                    },
                                    shape = RoundedCornerShape(20.dp),
                                    color = white.copy(0.7f),
                                    border = BorderStroke(
                                        width = 0.dp,
                                        color = Color(0xFFEEEEEE)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
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

                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Outlined.Check,
                                                contentDescription = null,
                                                tint = Color.Black
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
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}
//Dialog betterrr still no..



@Composable
fun AddNewAddressForm(
    viewModel: OrderViewModel
){
    val formState by viewModel.addressForm.collectAsState()

    val addAddressLoading by viewModel.addingAddressLoading.collectAsState()

    val addingAddressError by viewModel.addingAddressErrorMessage.collectAsState()
    //the add new address only



    Surface(
        shape = RoundedCornerShape(20.dp),
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
                .padding(start = 12.dp,top=12.dp,end=12.dp),// was 16
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
                    viewModel.updateAddressForm { it.copy(street = newStreet) }
                    viewModel.clearAddingAddressErrorMessage()
                },
                onSearchAction = {},
                placeholder = "Street Address",
                modifier = Modifier,
                isPasswordField = false,
                isSearchField = false,
                maxLines = 1,
                color=Color(0xFFF6F6F2)//original white ? yep
            )
            //city
            CustomTextField(
                value = formState.city,
                onValueChange = { newCity ->
                    viewModel.updateAddressForm { it.copy(city=newCity) }
                    viewModel.clearAddingAddressErrorMessage()
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
                value = formState.zipCode,
                onValueChange = {newCode ->
                    viewModel.updateAddressForm { it.copy(zipCode = newCode) }
                    viewModel.clearAddingAddressErrorMessage()
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
                    viewModel.updateAddressForm { it.copy(country=newCountry) }
                    viewModel.clearAddingAddressErrorMessage()
                },
                onSearchAction = {},
                placeholder = "Country",
                modifier = Modifier,
                isPasswordField = false,
                isSearchField = false,
                maxLines = 1
            )
            //error message.. aniamted Visibility again ? voerkill nah
            if(addingAddressError.isNotBlank()){
                Text(
                    text=addingAddressError,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
                    color  = Color(0xFFE35555),
                    modifier = Modifier.padding(5.dp)
                )
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

                onClick = {
                    viewModel.addAddress(
                        address = AddressResponseDTO(
                            id = null,
                            street = formState.street.trim(),
                            city = formState.city.trim(),
                            postalCode = formState.zipCode.trim(),
                            country = formState.country.trim(),
                            principal = false,
                        )
                    )
                },
            ) {
                if(addAddressLoading){
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

@Composable
fun OrderBriefSummary(
    viewModel: OrderViewModel,
    orderType: String, // should react to it no delivery
    //for ordering
    selectedAddress: AddressResponseDTO?,
    selectedOption: PickupTimeOption?,
    couponCode: String?,
    pickupTime: String?
){
    //i think i will need that shared repo thingy.. no more passing in navhost whaat a bummer
    val cart by viewModel.cartState.collectAsState()

    val placeOrderLoading by viewModel.placeOrderLoading.collectAsState()
    //disabled if cart is empty +

    Surface(
        shape = RoundedCornerShape(20.dp),
        //shadowElevation = 1.dp,
        color = white.copy(0.7f),//Color(0xFFFDF489),
        modifier = Modifier
            .padding(top = 8.dp)
            .wrapContentHeight()

    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Order Summary",
                color = Color.Black,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Number of items: ${cart!!.items.size}",
                color = Color.Gray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Subtotal",
                    color = Color.DarkGray,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = String.format(
                        "%.2f DT",
                        cart?.finalPrice ?: 0f //final NOT TOTAL
                    ),
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }//
            //pickup = no delivery feeess
            val deliveryFees = if(orderType.equals("Pickup",ignoreCase = true)) 0.0
               else 7.0

            //or animated visbility hehe
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Delivery",
                    color = Color.DarkGray,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = String.format("%.2f DT",deliveryFees),
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }//

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.Gray.copy(alpha = 0.7f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )

                val actualTotal = cart?.finalPrice!! + deliveryFees
                //i mean cart should never be null at this point uhum uhum
                Text(
                    text = String.format("%.2f DT",actualTotal),//FINALL TODO again
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )

            }//

            //CTA E A E A
            //WAIT if orderType pickup and it's still null pickup time also disable
            val enabledForPickup = if (orderType.equals("Pickup", ignoreCase = true)) {
                selectedOption != null
            } else {
                true
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(8.dp),

                contentPadding = PaddingValues(
                    horizontal = 11.dp,
                    vertical = 4.dp
                ),
                enabled = !placeOrderLoading && cart?.items?.isNotEmpty()==true && enabledForPickup == true,//nice one, null==true IS FALSE


                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color(0xFFE8D755),
                    disabledContainerColor = Color(0xFF3A3939)
                ),

                onClick ={
                    //FINALLY

                    viewModel.placeOrder(
                        address = selectedAddress,
                        couponCode = couponCode,
                        orderType = orderType,
                        pickupTime = pickupTime
                    )
                },
            ){
                if (placeOrderLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFE8D755),
                            modifier = Modifier
                                .size(32.dp),

                            strokeWidth = 4.5.dp
                        )
                    }
                }else {
                    Text(
                        text = "Confirm & Place Order",
                        color = Color.Yellow,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

        }
    }

}

//TOGGLE
data class toggleOption(
    val optionName: String,
    val onOptionSelected: () -> Unit,
)
@Composable //TODO make it aprt of the custom stuff library ?? O: pogg
fun CustomToggle(
    options: List<toggleOption>,
    selectedOptionName: String, //parent better..
    modifier:Modifier=Modifier
) {
    //row -> slight darker bg -> takes elements as a LIST ? ooh spicy to each one its own state and on clicked select it
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.LightGray.copy(0.2f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        options.forEach { option ->
            val isSelected = option.optionName.equals(
                selectedOptionName,
                ignoreCase = true
            ) // case insenstiive how ?

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) Color.Gray.copy(0.3f) else Color.Transparent// make em params TODO
                    )
                    .clickable {
                        option.onOptionSelected()
                    }
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option.optionName,
                    color = Color.Black,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

