package com.example.summery.uipages

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AddLocation
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.DeliveryDining
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.summery.DialogMenu
import com.example.summery.R
import com.example.summery.ScreenTransition
import com.example.summery.data.remote.dto.OrderResponseDTO
import com.example.summery.data.remote.dto.OrderType
import com.example.summery.network.CustomizationDTO
import com.example.summery.network.VariantDTO
import com.example.summery.ui.components.yellow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun OrderHistoryScreen(
    navController: NavController,
    viewModel: OrderHistoryViewModel

){
    //TOAST again
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var isToastError by remember { mutableStateOf(false) }

    //PAGING 3 MODERN PAGINATION YESS
    val lazyOrderItems = viewModel.ordersPagingFlow.collectAsLazyPagingItems()

    val selectedSortingOption by viewModel.selectedSort.collectAsStateWithLifecycle()

    val currentSelectedOrder by viewModel.selectedOrder.collectAsStateWithLifecycle()
    Box(
        modifier= Modifier.fillMaxSize()
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFFFF89F)
        ) {
            ScreenTransition(
                withSpinner = false,
                delayMillis = 0//mhm..
            ) {
                LazyColumn(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxHeight()
                        .padding(16.dp),
                    contentPadding= PaddingValues(bottom=60.dp)

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
                                text = "Order History",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontFamily = FontFamily.SansSerif,
                            )
                        }

                    }//end of sticky header


                    item{
                        OrderTable(
                            lazyOrderItems,
                            currentSort = selectedSortingOption,
                            onColumnClick = { sortingOption ->
                                viewModel.onSortChanged(sortingOption)
                            },
                            onItemClicked = { order ->
                                viewModel.onOrderClicked(order)
                            }
                        )
                    }

                    item{
                        Text(
                            text= "* All prices are displayed in TND",
                            style = MaterialTheme.typography.labelSmall,
                            color=Color.Black,
                            modifier = Modifier.padding(vertical = 12.dp)
                            )
                    }
                }
            }
        }
        if(currentSelectedOrder!=null){
            OrderHistoryItem(
                modifier = Modifier,
                onDismissRequest = {viewModel.OndismissOrderDetails()},
                order = currentSelectedOrder!!
            )
        }
    }//end of outer box
}

//ALL OPTIONS have the same paramters -> Enum less boilerplate than sealed class
enum class OrderSortOptions(
    val label:String,
    val parameter: String,
    @DrawableRes val icon: Int
){
    //Date desc, price, status, type
    DATE("Date","dateCommande,desc",R.drawable.calendar),
    PRICE("Total","totalTTC,desc",R.drawable.bill),
    STATUS("Status","statut,desc",R.drawable.order_delivery),
    TYPE("Type","OrderType,desc",R.drawable.delivery_truck)
}


//Table header item
//THE HELL ?  cannot do weight outslide of row so.. do this
@Composable
fun RowScope.TableHeaderItem(
    modifier:Modifier=Modifier,
    weight:Float,
    text:String,
    sortingOption: OrderSortOptions,
    currentSort: OrderSortOptions?,
    onClick:(OrderSortOptions) -> Unit,
    ){

    val isSelected = currentSort == sortingOption

    val headerColor by animateColorAsState(
        targetValue = if(isSelected) Color.Black.copy(0.2f) else Color(0xffE4DC91),
        animationSpec = tween(durationMillis = 100),
        label=""
    )

    Box(
        modifier=Modifier
            .weight(weight)
            .fillMaxHeight()
            .background(headerColor)
            .clickable{ onClick(sortingOption) }
            .padding(horizontal = 4.dp)
            .then(modifier), //id at this point honestly
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,

            ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.weight(weight),
                maxLines = 1

            )
            //arrow down, pinky down
            if (isSelected) {
                Icon(
                    imageVector = Icons.Outlined.ArrowDropDown,
                    tint = Color.Black,
                    contentDescription = "sorting by Order type descending",
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

//tapping a table item.. Dialog for into i guess, it's a lot of info..
@Composable
fun OrderHistoryRow(
    modifier:Modifier=Modifier,
    orderItemName:String,
    weightRepartition: List<Float>,
    orderItemCustomization:List<CustomizationDTO>,
    orderItemVariants:List<VariantDTO>,
    orderItemQuantity:Int,
    orderItemPrice:Double
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical=5.dp)
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        //1-Name
        Text(
            text = orderItemName,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .weight(weightRepartition[0])
        )

        //2- Customization + variants idk
        val customizationNames = orderItemCustomization.map{it.name}
        val variantNames = orderItemVariants.map{"${it.attribute}: ${it.value}"}
        //damn.. damnnn...

        val addOnsList = customizationNames+variantNames
        val addOns=addOnsList
            .joinToString(separator = " | ")
            .ifEmpty { "-" }
        Text(
            text =addOns ,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .weight(weightRepartition[0])

        )

        //3-Quantity
        Text(
            text = "x$orderItemQuantity",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .weight(weightRepartition[1]),
            textAlign = TextAlign.Center
        )
        //4-Price
        Text(
            text = "${orderItemPrice}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .weight(weightRepartition[2]),
            textAlign = TextAlign.End
        )

    }
}
@Composable
fun OrderHistoryItem(
    modifier:Modifier =Modifier,
    onDismissRequest:() -> Unit,
    order: OrderResponseDTO
){

    //currently it's the full DATE T TIME
    //OH SHIT -> MMM (3 not 4) -> gives that Sep, Oct niceee
    //d or dd -> day number
    //hh:mm a -> 12 hours (HH:mm for 24 hour)
    //can even insert a string.. what a good API

    fun formatDate(dateTimeString:String?): String{
        if(dateTimeString.isNullOrEmpty()) return ""
        return try{
            val parsedString = LocalDateTime.parse(dateTimeString)
            val formatter= DateTimeFormatter.ofPattern("MMM d, yyyy 'at' hh:mm a")
            parsedString.format(formatter)
        }catch(e: Exception){
            dateTimeString.take(10).replace ("-",":")
            //oh wow .replace exists
        }
    }

    val weightRepartition=listOf(
        0.35f,
        0.33f,
        0.13f,
        0.20f
    )

    DialogMenu(
        modifier = modifier,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
            ,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.Start
        ){

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //order as title
                Text(
                    text = "#${order.orderCode}",
                    style = MaterialTheme.typography.titleMedium
                )

                //pill for status with Colors ? TODO
                Text(
                    text = "${order.status}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            //Pickup -> pickupTime + branch
            //Delivery -> branch + address

            if (order.orderType== OrderType.PICKUP) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    order.branchName?.let {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(
                                0.dp
                            )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,

                                ) {
                                Icon(
                                    imageVector = Icons.Outlined.Storefront,
                                    tint = Color.Black,
                                    contentDescription = "Branch",
                                    modifier = Modifier
                                        .size(15.dp)
                                )
                                Text(
                                    text = "Branch: ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black,
                                    modifier = Modifier.padding(bottom = 0.dp)
                                )
                            }

                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelSmall,
                                )
                        }
                    }

                    order.pickupTime?.let {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(
                                0.dp
                            )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,

                                ) {
                                Icon(
                                    imageVector = Icons.Outlined.AccessTime,
                                    tint = Color.Black,
                                    contentDescription = "Pickup Time",
                                    modifier = Modifier
                                        .size(15.dp)
                                )
                                Text(
                                    text = "Pickup Time: ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black,
                                    modifier = Modifier.padding(bottom = 0.dp)
                                )
                            }

                            Text(
                                text = formatDate(it),
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }

                    }
                }

            }else  if(order.orderType== OrderType.DELIVERY){
                    order.deliveryAddress?.let{
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(
                                0.dp
                            )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,

                                ) {
                                Icon(
                                    imageVector = Icons.Outlined.AddLocation,
                                    tint = Color.Black,
                                    contentDescription = "Delivery address",
                                    modifier = Modifier
                                        .size(15.dp)
                                )
                                Text(
                                    text = "Delivery Address: ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black,
                                    modifier = Modifier.padding(bottom = 0.dp)
                                )
                            }

                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelSmall,

                            )
                        }

                    }

                    order.branchName?.let{
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(
                                0.dp
                            )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,

                                ) {
                                Icon(
                                    imageVector = Icons.Outlined.Storefront,
                                    tint = Color.Black,
                                    contentDescription = "Branch",
                                    modifier = Modifier
                                        .size(15.dp)
                                )
                                Text(
                                    text = "Branch: ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black,
                                    modifier = Modifier.padding(bottom = 0.dp)
                                )
                            }

                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
            }


            //items -> table items again
            //val scrollState = rememberScrollState()
            val  maxScreenHeight = LocalConfiguration.current.screenHeightDp.dp
            LazyColumn (
                modifier=Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxScreenHeight*0.3f)
                    .clip(RoundedCornerShape(15.dp))
                    .background((Color(0xFFD3CF91).copy(0.5f)))
                ,
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
                //TODO constraint height
            ) {
                //header
                stickyHeader {
                    Row(
                        modifier=Modifier
                            .fillMaxWidth()
                                .height(30.dp)
                                .clip(
                                    RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
                                )
                                .background(Color(0xFFD3CF91))
                                .padding(horizontal = 5.dp)
                        ,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,

                        ) {
                        Text(
                            text = "Item",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.weight(weightRepartition[0]),
                            maxLines = 1
                        )

                        Text(
                            text = "Details",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .weight(weightRepartition[1]),
                            maxLines = 1
                        )

                        Text(
                            text = "Qty",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.weight(weightRepartition[2]),
                            textAlign = TextAlign.End,
                            maxLines = 1
                        )

                        Text(
                            text = "Price",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.weight(weightRepartition[3]),
                            textAlign = TextAlign.End,
                            maxLines = 1
                        )
                    }

                }

                itemsIndexed(order.items){ index,item ->
                    OrderHistoryRow(
                        modifier = Modifier,
                        weightRepartition=weightRepartition,
                        orderItemName = item.productName,
                        orderItemQuantity = item.quantity,
                        orderItemPrice = item.unitPrice,
                        orderItemVariants = item.variants,
                        orderItemCustomization = item.customizations
                    )
                    //divider
                    if(index < order.items.lastIndex){
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            thickness = 0.8.dp,
                            color = (Color(0xFFD5CD81).copy(0.6f))
                        )
                    }
                }

            }//end of lazy column

                HorizontalDivider(
                    modifier = Modifier.padding(bottom = 5.dp),
                    color = Color.Black
                )
                //summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Amount",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${order.totalAmount} TND",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }

        }//end of column
    }
}



//TABLE IDEA IS SO BACK, but responsive
//response in kotlin = .. Box with horizontal scroll
//THEN add a min width restriction to its children .. that's it ?
@Composable
fun OrderTable(
    items:  LazyPagingItems<OrderResponseDTO>,
    onItemClicked:(OrderResponseDTO) -> Unit,
    currentSort: OrderSortOptions?,
    onColumnClick: (OrderSortOptions) -> Unit

){
    val horizontalScrollState = rememberScrollState()

    //Nested drag again.. yaay someone needs to make this a REUSABLE FUNCTION ??
    val stopNestedDrag = remember {
        object: NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset = available

        }
    }

    Box(
        modifier=Modifier
            .fillMaxWidth()
            .horizontalScroll(horizontalScrollState)
    ) {
        //screen size ?

        val configuration = LocalConfiguration.current

        val tableWidth = remember(configuration.screenWidthDp) {
            maxOf(300.dp,configuration.screenWidthDp.dp -32.dp)
        }

        //BUTTTT what if tablet or big phone ?
        //-> MAX

        //max height ?
        // min is 200.dp -> coerce?
        val tableHeight= remember(configuration.screenHeightDp) {
            (configuration.screenHeightDp.dp - 60.dp - 72.dp - 50.dp - 55.dp).coerceAtLeast(200.dp)
        }


        LazyColumn(
            modifier = Modifier
                //.fillMaxWidth()
                .requiredWidth(tableWidth)//THIS is the one!
                //fun fact for MY PHONE -> sweet size is 350dp here lmaoo
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(0.06f))//0.08f black
                //.height(500.dp) //mhm
                .height(tableHeight)
                .nestedScroll(stopNestedDrag)
        ) {
            //header
            stickyHeader {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .clip(
                            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                        .background(Color(0xffE4DC91)),//Color(0xffE4DC91)
                        //.padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon | Order code trimmed | date | Status | cost

                    //CLICKABLE MAKE IT SORT!
                    //smooth color changing ?
                    TableHeaderItem(
                        modifier=Modifier.padding(start=6.dp),
                        weight = 0.14f,
                        text = "Type",
                        sortingOption = OrderSortOptions.TYPE,
                        currentSort = currentSort
                    ) {
                        onColumnClick(OrderSortOptions.TYPE)
                    }


                    Text(
                        "Order",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.weight(0.26f)
                    )

                    TableHeaderItem(
                        weight = 0.22f,
                        text = "Date",
                        sortingOption = OrderSortOptions.DATE,
                        currentSort = currentSort
                    ) {
                        onColumnClick(OrderSortOptions.DATE)
                    }

                    TableHeaderItem(
                        weight = 0.22f,
                        text = "Status",
                        sortingOption = OrderSortOptions.STATUS,
                        currentSort = currentSort
                    ) {
                        onColumnClick(OrderSortOptions.STATUS)
                    }
                    TableHeaderItem(
                        weight = 0.16f,
                        text = "Total *",
                        sortingOption = OrderSortOptions.PRICE,
                        currentSort = currentSort
                    ) {
                        onColumnClick(OrderSortOptions.PRICE)
                    }

                }
            }
            //items
            items(
                count = items.itemCount,
                key = items.itemKey { it.orderId }
            ) { index ->
                val order = items[index]
                if (order != null) {
                    OrderTableItem(
                        orderItem = order,
                        onClick = {
                            onItemClicked(order)
                        },//TODO probably expand.. u need the details anyways..
                    )

                    //divider.. except the last item wehee.. how cool..
                    if (index < items.itemCount - 1) {
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            thickness = 0.8.dp,
                            color = (Color(0xFFD5CD81).copy(0.6f))
                        )
                    }
                }
            }



        }
    }

}


@Composable
fun OrderTableItem(
    orderItem: OrderResponseDTO,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
){

    val weightRepartition= listOf(
        0.14f,0.26f,0.24f,0.22f,0.14f
    )

    fun formatDate(dateTimeString:String?): String{
        if(dateTimeString.isNullOrEmpty()) return ""
        return try{
            val parsedString = LocalDateTime.parse(dateTimeString)
            val formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd")
            parsedString.format(formatter)
        }catch(e: Exception){
            dateTimeString.take(10).replace ("-",":")
            //oh wow .replace exists
        }
    }

    fun formatOrderCode(code:String?):String{
        if(code.isNullOrBlank()) return "ORD"
        return try{
            //after the second hyphen -> 3rd item
            code.split("-")[2]

        }catch(e:Exception){
            code
        }
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clickable{onClick()},

        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        //colors = CardDefaults.cardColors(containerColor = Color.Black.copy(0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {

        //outermost Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical=5.dp)
            ,
            verticalAlignment = Alignment.CenterVertically,
            ) {
            //1- Order or Pickup Icon
            Box(
                modifier = Modifier
                    .weight(weightRepartition[0]) // CLAIM the size first
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    //DO NOT STRETCHH for it tho this was the problem
                    .size(25.dp)
                    .clip(
                        RoundedCornerShape(
                            16.dp
                        )
                    ).background(Color.Transparent)
                    .padding(4.dp),

                contentAlignment = Alignment.Center,

                ) {
                if (orderItem.orderType == OrderType.DELIVERY) {

                    Icon(
                        imageVector = Icons.Outlined.DeliveryDining,
                        contentDescription = "Order type: Delivery",
                        tint = Color.Black,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (orderItem.orderType == OrderType.PICKUP) {
                    Icon(
                        imageVector = Icons.Outlined.Storefront,
                        contentDescription = "Order type: Pickup at a physical store",
                        tint = Color.Black,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }//end of icon box

            //still inside the row
                //2nd row -> Order code TRIMMED
                    Text(
                        text = "#${formatOrderCode(orderItem.orderCode)}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .weight(weightRepartition[1])
                    )

            //3rd row -> Date
                    Text(
                        text = formatDate(orderItem.orderDate),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .weight(weightRepartition[2])
                    )

                //4th row -> Status TODO more easily spottable, colors ?

                    Text(
                        text = "${orderItem.status}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .weight(weightRepartition[3])
                    )

                //5th -> total
                    Text(
                        text = "${orderItem.totalAmount}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .weight(weightRepartition[4])

                    )



        }
    }
}