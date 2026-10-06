package com.example.summery.uipages

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.summery.AppToast
import com.example.summery.CustomTextField
import com.example.summery.R
import com.example.summery.ScreenTransition
import com.example.summery.network.CartItemDTO
import com.example.summery.ui.components.yellow
import com.example.summery.white
import kotlin.math.sin


@Composable
fun CartScreen(
    modifier: Modifier = Modifier,
    CartViewModel: CartViewModel,
    navController: NavController,
    onNavigateToHome: () -> Unit,
    onNavigateToOrderScreen: () -> Unit
    ){

    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var isToastError by remember { mutableStateOf(false) }

    //cart
    val cart by CartViewModel.cart.collectAsState()
    val isLoading by CartViewModel.isCartLoading.collectAsState()

    val errorMessage by CartViewModel.errorMessage.collectAsState()
    val statusMessage by CartViewModel.statusMessage.collectAsState()

    var couponCode by remember { mutableStateOf("") }

    val isCouponLoading by CartViewModel.applyingCodeLoading.collectAsState()


    Box(
        modifier= Modifier
            .fillMaxSize()

    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFFFF89F)
        ) {
            //THE FIRST ONE
            //=>while loading inially -> fades in -> spinner sits there
            //WHEN timer hits -> IT IS TOGLLED TO FALSE
            //oh that's the exist

            ScreenTransition() {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    //1- LEMON CARBONATIONS
                    LemonFizzBubbles()

                    //pfft another container .. TODO remove some of the ssurfaces
                    Column(
                        modifier=Modifier.fillMaxSize()
                    ) {
                        //scrollable column i guess..  0.6f of the screen ?
                        LazyColumn(
                            modifier = Modifier
                                .statusBarsPadding()
                                .fillMaxHeight()
                                .padding(16.dp)

                        ) {
                            stickyHeader {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(yellow)
                                        .padding(bottom = 12.dp),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    //back icon ofc
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
                                        text = "Your Cart",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        fontFamily = FontFamily.SansSerif,
                                    )
                                }
                            }//end of header

                            //1-EMPTY CART
                            if(cart==null || cart?.items!!.isEmpty()){

                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.empty_lemon),
                                            contentDescription = "Empty Cart Lemon",
                                            tint = Color.Unspecified,
                                            modifier = Modifier.size(120.dp)
                                        )
                                        Spacer(modifier = Modifier.height(14.dp))

                                        Text(
                                            text = "Your cart is a bit dry...",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )

                                        Spacer(modifier = Modifier.height(20.dp))
                                        Button(
                                            modifier = Modifier
                                                .fillMaxWidth(0.65f)
                                                .height(40.dp),

                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.Black,
                                                contentColor = Color(0xFFE8D755),
                                                disabledContainerColor = Color(0xFF3A3939)
                                            ),

                                            onClick = {
                                               onNavigateToHome()
                                            }

                                        ) {
                                            Text(
                                                text = "Explore Drinks",
                                                color = Color.Yellow,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                            }else {
                                //2-cart NOT EMPTY
                                //BEST PEROFRMANCE -> one big item no
                                cart?.items?.let { cartItems ->
                                    items(
                                        cartItems,
                                        key = { it.id }
                                    ) { cartItem ->
                                        //aniamted visibility ??
                                        CartItemCard(
                                            item = cartItem,
                                            onIncrementQuantity = {
                                                CartViewModel.incrementItemQuantity(
                                                    cartItem.id,
                                                    cartItem.quantity
                                                )
                                            },
                                            modifier = Modifier.animateItem(),
                                            onDecrementQuantity = {
                                                CartViewModel.decrementItemQuantity(
                                                    cartItem.id,
                                                    cartItem.quantity
                                                )
                                            },
                                            onRemove = { CartViewModel.removeItem(cartItem.id) },
                                        )
                                    }
                                }
                                //then right after it idk
                                item {

                                    //time for the footer

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
                                                .padding(16.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()

                                            ) {
                                                Text(
                                                    text = "Order Summary",
                                                    //color = Color.Black,
                                                    //fontSize = 22.sp,
                                                    //fontWeight = FontWeight.SemiBold,
                                                    style = MaterialTheme.typography.headlineLarge,
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "Subtotal",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = Color.DarkGray,
                                                    //fontSize = 18.sp,
                                                    //fontWeight = FontWeight.SemiBold
                                                )
                                                Text(
                                                    text = String.format(
                                                        "%.2f DT",
                                                        cart?.totalCartPrice ?: 0f
                                                    ),
                                                    color = Color.Black,
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            //more fees
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    "Delivery",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = Color.DarkGray,
                                                    //fontSize = 18.sp,
                                                    //fontWeight = FontWeight.SemiBold
                                                )
                                                //umh we have pickuo -> TODO add the "i" thingy for that ? WITH the text yh yh
                                                Text(
                                                    text = String.format(
                                                        "%.2f DT",
                                                        7.0),
                                                    color = Color.Black,
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            //disclaimer SO COOL
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Info,
                                                    tint = Color.Gray,
                                                    contentDescription = "Pickup vs Delivery disclaimer",
                                                    modifier = Modifier
                                                        .size(15.dp)
                                                )
                                                //umh we have pickuo -> TODO add the "i" thingy for that ? WITH the text yh yh
                                                Text(
                                                    text = "Delivery fees don't apply for the pickup option",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color=Color.Gray
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))


                                            //APPLY COUPON TODO
                                            Box(
                                                modifier = Modifier
                                                    .wrapContentSize()
                                                    .clip(RoundedCornerShape(20.dp))
                                                    .background(white)
                                                    .padding(0.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    CustomTextField(
                                                        value = couponCode,
                                                        onValueChange = { couponCode = it },
                                                        placeholder = "Have a coupon ?",
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .zIndex(1f),
                                                        isPasswordField = false,
                                                        isSearchField = false,
                                                        maxLines = 1
                                                    )

                                                    Row(
                                                        modifier = Modifier
                                                            .padding(horizontal = 8.dp)
                                                            .clickable {
                                                                if (couponCode.isNotBlank()) {
                                                                    CartViewModel.applyCoupon(
                                                                        couponCode.trim()
                                                                    )
                                                                }
                                                            },
                                                        horizontalArrangement = Arrangement.spacedBy(
                                                            5.dp
                                                        ),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        if (isCouponLoading) {
                                                            CircularProgressIndicator(
                                                                color = Color.Black,
                                                                modifier = Modifier
                                                                    .size(24.dp),
                                                                strokeWidth = 4.5.dp
                                                            )

                                                        } else {

                                                            Text(
                                                                text = "Apply",
                                                                style = MaterialTheme.typography.bodyMedium,
                                                                color=Color.Black,
                                                                //color = Color.Black,
                                                                //fontSize = 15.sp,
                                                                fontWeight = FontWeight.SemiBold
                                                            )

                                                            Icon(
                                                                imageVector = Icons.Outlined.LocalOffer,
                                                                tint = Color.Black,
                                                                contentDescription = "Apply coupon code",
                                                                modifier = Modifier
                                                                    .size(22.dp)
                                                            )
                                                        }
                                                    }
                                                }//end of this row
                                            }

                                            //finally applied code + its value + remove button

                                            val hasCoupon = !cart?.appliedCouponCode.isNullOrBlank()

                                            //cart?.appliedCouponCode?.let { code ->
                                            androidx.compose.animation.AnimatedVisibility(
                                                visible = hasCoupon,
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
                                                    tween(
                                                        durationMillis = 250,
                                                        easing = LinearOutSlowInEasing
                                                    ), shrinkTowards = Alignment.Top
                                                )
                                                        + fadeOut(
                                                    animationSpec = tween(
                                                        durationMillis = 250
                                                    )
                                                )


                                            ) {
                                                val code = cart?.appliedCouponCode ?: ""
                                                val discount = cart?.discountAmount ?: "0"

                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(top = 6.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    val formattedDiscount =
                                                        String.format("%.3f", discount)
                                                    Text(
                                                        text = "Applied: ${code} (- ${formattedDiscount} DT)",
                                                        color = Color.Gray,
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.SemiBold
                                                    )

                                                    //right - remove

                                                    Text(
                                                        text = "Remove",
                                                        color = Color(0xFFE35555),
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        modifier = Modifier
                                                            .clickable {
                                                                CartViewModel.removeCoupon()

                                                            }
                                                            .padding(0.dp)
                                                    )


                                                }
                                            }

                                            //}//enf of coupon info row with ?.let

                                            //end of column ?

                                            HorizontalDivider(
                                                modifier = Modifier.padding(vertical = 8.dp),
                                                color = Color.Gray.copy(alpha = 0.7f)
                                            )

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    "Total",
                                                    style = MaterialTheme.typography.titleMedium,
                                                )
                                                val actualTotal = cart?.finalPrice?.plus(7.0) ?: 0.0
                                                //TODO check
                                                Column(
                                                ) {
                                                    val hasCoupon =
                                                        !cart?.appliedCouponCode.isNullOrBlank()

                                                    //the strikethrough for the discounted..
                                                    Text(
                                                        text = if (hasCoupon) String.format(
                                                            "%.2f DT",
                                                            actualTotal + cart?.discountAmount!!
                                                        ) else String.format(
                                                            "%.2f DT",
                                                            actualTotal
                                                        ),
                                                        color = if (hasCoupon) Color.Gray else Color.Black,
                                                        fontSize = if (hasCoupon) 15.sp else 20.sp,
                                                        textDecoration = if (hasCoupon) TextDecoration.LineThrough else TextDecoration.None,
                                                        fontWeight = FontWeight.ExtraBold
                                                    )
                                                    //real deal yuh
                                                    if (hasCoupon) {
                                                        Text(
                                                            text = String.format(
                                                                "%.2f DT",
                                                                actualTotal
                                                            ),
                                                            style = MaterialTheme.typography.titleMedium,

                                                        )
                                                    }
                                                }
                                            }//end of price row

                                            Spacer(modifier = Modifier.height(8.dp))

                                            val isCheckoutAllowed by CartViewModel.isCheckoutAllowed.collectAsState()
                                            val beanchId by CartViewModel.branchId.collectAsState()

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

                                                enabled = isCheckoutAllowed,
                                                onClick = {
                                                    //ITS NULL IF it's wrong soo. ?let ? it works with methods ?
                                                    beanchId?.let{branchId ->
                                                        onNavigateToOrderScreen()
                                                    }

                                                    //TODO RESTRICTION
                                                },
                                            ) {
                                                Text(
                                                    text = "Proceed to Checkout",
                                                    color = Color.Yellow,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }//enf of CTA button

                                            if (!isCheckoutAllowed) {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                    Text(
                                                        text ="Items must be from the same branch!",
                                                        fontFamily = FontFamily.SansSerif,
                                                        fontWeight = FontWeight.Medium,
                                                        color  = Color(0xFFE35555)
                                                    )
                                            }

                                        }//now end of column

                                    }//end of this surface

                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }


                        }//end of lazy column




                    }//end of main column

                }
            }
        }

        LaunchedEffect(statusMessage) {
            if(statusMessage.isNotBlank()) {
                toastMessage = statusMessage
                isToastError = false
                showToast = true

                CartViewModel.clearStatusMessage()
            }
        }

        LaunchedEffect(errorMessage) {
            if(errorMessage.isNotBlank()){
                toastMessage = errorMessage
                isToastError = true
                showToast = true

                CartViewModel.clearErrorMessage()
            }
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
    }//end of outer box
}

@Composable
fun CartItemCard(
    item: CartItemDTO,
    //THIS better than viewModel passing
    onIncrementQuantity: () -> Unit,
    onDecrementQuantity: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier

){
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = white.copy(0.7f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ){
            //image
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.productName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(85.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            //place holder details
            //TODO rehall

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.productName,
                        style = MaterialTheme.typography.headlineLarge,
                        //fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom=5.dp)
                    )
                    //hmm -> chips better
                    if (item.variantNames.isNotEmpty()) {
                        //item.variant name
                        //TOOD also customizations !!

                        val chipsList= mutableListOf<String>()

                        chipsList+=(item.variantNames) +(item.customizations.map{it.name})

                        //.map {it.name} WAY BETTER THAN ALOOP

                        Text(
                            text = (chipsList).joinToString(" · "),
                            //fontSize = 14.sp,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray
                        )
                    }

                Row(
                    modifier=Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                    ) {
                        Text(
                            text = "Quantity: ${item.quantity}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray
                        )
                        Row(
                            modifier = Modifier.wrapContentSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = String.format("%.2f DT", item.subTotal),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = Color.Black
                            )

                            Icon(
                                imageVector = Icons.Filled.Delete,
                                tint = Color.Black,
                                contentDescription = "Remove item from the cart",
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable {
                                        onRemove()
                                    }
                            )
                        }
                    }


                    //right -> quantity + delete
                    // -

                    Row(
                        modifier = Modifier.wrapContentSize(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)

                        ) {
                        //- FIRSR apparently..
                        IconButton(
                            modifier = Modifier.size(22.dp),
                            onClick = onDecrementQuantity
                        ) {
                            Box(
                                modifier=Modifier
                                    .size(22.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFFDF489).copy(0.5f))
                            ){
                                Icon(Icons.Filled.Remove, contentDescription = null, tint = Color.Black)
                            }
                        }


                        //just a number lmao
                        Text(
                            text = "${item.quantity}",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center

                        )

                        //-
                        IconButton(
                            modifier = Modifier
                                .size(22.dp),
                            onClick = onIncrementQuantity
                        ) {
                            Box(
                                modifier=Modifier
                                    .size(22.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFFDF489).copy(0.5f))
                            ){
                                Icon(Icons.Filled.Add, contentDescription = null, tint = Color.Black)
                            }
                        }
                    }
                }

            }





        }
    }
}

//LEMON CARBONATION FIZZZ
data class BubbleData(
    val xFraction: Float,
    val sizeDp: Float,
    val durationMs: Int,
    val delayMs: Int
)

@Composable
fun LemonFizzBubbles(){
    //why list of stuff inside remember ?
    //TODO -> to portec from recompositions again
    val bubbles= remember {
        listOf(
            BubbleData(0.10f, 6f, 4000, 0),
            BubbleData(0.25f, 10f, 3000, 300),
            BubbleData(0.40f, 7f, 5000, 600),
            BubbleData(0.60f, 5f, 3500, 900),
            BubbleData(0.75f, 9f, 4500, 1200),
            BubbleData(0.85f, 6f, 3200, 1500),
            BubbleData(0.50f, 8f, 4800, 1800),
            BubbleData(0.15f, 11f, 5500, 2100)
        )
    }

    val infiniteTransition = rememberInfiniteTransition()

    val timeState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100000f, //clock ?
        animationSpec = infiniteRepeatable(
            animation = tween(100000,easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    //one canvas = more efficient pls
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val clock = timeState.value

        bubbles.forEach { bubble ->
            val totalLoop = bubble.durationMs.toFloat()
            val adjustedTime = (clock - bubble.delayMs).coerceAtLeast(0f)
            val progress = (adjustedTime % totalLoop) / totalLoop
            //modulo -> a stnadard animation = from 0 to 1 then stop
            //- bubbles looping on their independant time ?

            //-> Clock = ENDLESS TICK counter 0 to 100K

            //->clock - bubble.delayMs = offset for each bubble

            // % -> chops the endless time into REPEATING CHUNKS
            //Ex: this bubble has loop duration = 3000ms
            //-> time % 3000 counts from 0 to 3000 then back again forever

            //DIVIDE BY TOTAL LOOP ==> converts to a normalized progress fraction
            //0 = bottom of screen  1 = top

            //FADE OUT AT TOP ?
            val alpha = if (progress > 0.85f) {
                (1f - progress) / 0.15f * 0.25f
            } else {
                0.5f
            }


            //SINE FOR WOBBLE WHOA
            val wobblePx = 10.dp.toPx()
            val x =
                (size.width * bubble.xFraction) + (sin(progress * 4 * Math.PI.toFloat()) * wobblePx)
            val y = size.height * (1f - progress)
            val radius = bubble.sizeDp.dp.toPx()

            //FILL THEN CIRCLE okay fill ..
            drawCircle(
                color = Color(0xFFFFD700).copy(alpha*0.4f),
                radius = radius,
                center = Offset(x, y)
            )
            drawCircle(
                color = Color(0xFFFFAA00).copy(alpha*0.5f),
                radius = radius,
                center = Offset(x, y),
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }

}






