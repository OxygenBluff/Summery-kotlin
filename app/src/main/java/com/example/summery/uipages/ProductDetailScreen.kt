


import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.twotone.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.example.summery.AppToast
import com.example.summery.CustomChip
import com.example.summery.CustomTextField
import com.example.summery.R
import com.example.summery.ScreenTransition
import com.example.summery.network.CustomizationDTO
import com.example.summery.network.ReviewDTO
import com.example.summery.network.VariantDTO
import com.example.summery.productImagesCarousel
import com.example.summery.ui.components.yellow
import com.example.summery.ui.theme.descriptionText
import com.example.summery.uipages.ProductDetailsViewModel
import com.example.summery.white
import org.ocpsoft.prettytime.PrettyTime
import java.time.LocalDateTime
import java.util.Collections.emptyMap


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ProductDetailScreen(
    productId:Long,
    navController: NavController,
    viewModel: ProductDetailsViewModel,
    onBranchClick: (Long)-> Unit
){


    //TOAST YESSS
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var isToastError by remember { mutableStateOf(false) }

    //REVIEW STUFF
    val reviews by viewModel.reviews.collectAsState()
    val loadingReviews by viewModel.loadingReviews.collectAsState()

    //the product itself..
    val product by viewModel.product.collectAsStateWithLifecycle()
    val branchesMap by viewModel.branches.collectAsStateWithLifecycle()



    LaunchedEffect(productId) {
        viewModel.fetchProduct(productId)
        viewModel.fetchReviews(productId)
    }

    //oh this is it
    BackHandler(enabled=true) {
        navController.popBackStack()
    }



    Surface(
            modifier = Modifier
                .fillMaxSize(),
                //.padding(innerPadding),

            color = Color(0xFFFFF89F)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {


                ScreenTransition(
                    isDataLoading = product==null,
                    delayMillis = 0,// was 120
                    withSpinner = true
                ) {
                    val product=product//copy of it ?
                    if(product!=null) {
                        //prouduct might have a DISCOUNT naurrr  TODO
                        val basePrice =product.DiscountedPrice?: product.price


                        var variantPrice by remember(product) { mutableDoubleStateOf(basePrice) }
                        var customizationPriceDelta by remember { mutableDoubleStateOf(0.0) }
                        var currentStock: Int? by remember(product) { mutableStateOf(product.stock) }

                        var currentSelectedVariants by remember {
                            mutableStateOf<Map<String, VariantDTO?>>(emptyMap())
                        }

                        var currentSelectedCustomizations by remember {
                            mutableStateOf<Map<String, CustomizationDTO?>>(emptyMap())
                        }

                        var quantity by remember { mutableIntStateOf(1) } // these exist for primitve ints.. performance gains ??


                        val currentPrice by remember {
                            derivedStateOf { variantPrice + customizationPriceDelta }
                        }

                        val totalPrice by remember {
                            derivedStateOf { currentPrice * quantity }
                        }

                        LazyColumn(
                            modifier = Modifier
                                .statusBarsPadding()
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
                                    //back icon
                                    Icon(
                                        imageVector = Icons.Filled.ArrowBack,
                                        contentDescription = "Back icon",
                                        tint = Color.Black.copy(0.7f),
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clickable {
                                                navController.popBackStack()
                                                //pop back stack = go back ONE screen
                                            }
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = "Product Details",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        fontFamily = FontFamily.SansSerif,
                                    )
                                }

                            }

                            //TODO fixing the Items
                            item {
                                //TOOD PERFORMANCE PROBLEM GO OVER THE STUFF ITEM BY ITEM BLOC INSTEADDD
                                //images
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(shape = RoundedCornerShape(20.dp))
                                        .height(300.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    //horizontal page + wanna zoom into pics

                                    productImagesCarousel(
                                        productImages = product.images
                                    )
                                }
                            }


                            item {
                                //name + RATING
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    //name
                                    Text(
                                        text = "${product.name}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        fontFamily = FontFamily.SansSerif,
                                        modifier = Modifier
                                            //.padding(vertical = 6.dp)
                                            .alignByBaseline() // ? what
                                            .weight(1f),
                                        maxLines = 2 // Hmm ?
                                    )
                                    //rating

                                    //not elivs.. IF rating not null -> format it ELSE (the let one) -> ?: N/A
                                    val formattedRating = product.averageRating?.let {
                                        String.format("%.1f", it)
                                    } ?: "N/A"

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .alignByBaseline()
                                    ) {
                                        Text(
                                            text = "${formattedRating}",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.DarkGray,
                                            fontFamily = FontFamily.SansSerif,
                                        )
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_lemon),
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp),
                                        )
                                    }
                                }//end of name + rating

                                //categories FLOW ROW
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                ) {
                                    for (category in product.categories) {
                                        CustomChip(
                                            modifier = Modifier,
                                            label = category.name,
                                            selectable = false,
                                            selected = false,
                                            icon = null, //TODO map of icons ?
                                            ContainerColor = white,
                                            LabelColor = white,
                                            onClick = {}
                                        )
                                    }
                                }

                                //Store + icon ? -> should be clickable IN THE FUTURE store page ? TODO
                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Store,
                                        contentDescription = "branch",
                                        modifier = Modifier
                                            .size(22.dp)
                                            .padding(bottom = 2.dp),
                                        tint = Color.DarkGray
                                    )

                                    Text(
                                        text = branchesMap.get(product.sellerId)
                                            ?: "Unknown Branch",
                                        style = MaterialTheme.typography.descriptionText,
                                        textDecoration = TextDecoration.Underline,
                                        modifier = Modifier
                                            .padding(bottom = 6.dp)
                                            .clickable {
                                                //pft can be null ?.let my dear
                                                product.sellerId?.let { sellerId ->
                                                    onBranchClick(product.sellerId)
                                                }
                                            }
                                    )
                                }
                            }

                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                    //for the surface horizontal = 16.dp, vertical = 10.dp
                                ) {

                                    Text(
                                        text = "\"${product.description}\"",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.DarkGray,
                                        fontFamily = FontFamily.SansSerif,
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    )
                                }
                                //}

                            }

                            item {
                                if (product.variants.isNotEmpty()) {

                                    ProductVariantsHandlerV2(
                                        variants = product.variants,
                                        //basePrice = product.price,
                                        basePrice = product.DiscountedPrice ?: product.price,
                                        baseStock = product.stock
                                    ) { selectedVariants, price, stock ->
                                        currentStock = stock
                                        variantPrice = price
                                        currentSelectedVariants = selectedVariants

                                    }

                                }
                            }

                            item {
                                if (product.customizations.isNotEmpty()) {
                                    ProductCustomizationsHandler(
                                        customizations = product.customizations,

                                        ) { selectedCustomizations, customizationDelta ->
                                        currentSelectedCustomizations = selectedCustomizations
                                        customizationPriceDelta = customizationDelta
                                    }
                                }
                            }

                            //quantity
                            item {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = white.copy(0.7f),
                                    modifier = Modifier
                                        .padding(top = 8.dp,)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp
                                        )
                                    ) {

                                        Row(
                                            modifier =
                                                Modifier.fillMaxSize(),
                                            //.padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            //1-Price  to the far left
                                            Text(
                                                text = String.format(
                                                    "%.2f DT",
                                                    totalPrice
                                                ),
                                                fontSize = 26.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.Black,
                                                fontFamily = FontFamily.SansSerif,

                                                )
                                            //2- quantity selectors on the far right -> grouped
                                            Row(
                                                modifier = Modifier,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                //1- Minus
                                                IconButton(onClick = { if (quantity > 0) quantity-- }) {
                                                    Icon(
                                                        Icons.Filled.Remove,
                                                        contentDescription = null,
                                                        tint = Color.Black
                                                    )
                                                }

                                                //2-Indicator -> can input, basic = NO STYLING lmao not even a bg
                                                val focusManager = LocalFocusManager.current
                                                val keyboardController =
                                                    LocalSoftwareKeyboardController.current

                                                BasicTextField(
                                                    value = quantity.toString(),
                                                    onValueChange = {
                                                        it.toIntOrNull()?.let { parsed ->
                                                            quantity =
                                                                parsed.coerceIn(
                                                                    1,
                                                                    currentStock ?: 1
                                                                )
                                                        }
                                                    },
                                                    keyboardOptions = KeyboardOptions(
                                                        keyboardType = KeyboardType.Number,
                                                        imeAction = ImeAction.Done
                                                    ),
                                                    //the stupid cursor
                                                    keyboardActions = KeyboardActions(
                                                        onDone = {
                                                            keyboardController?.hide()
                                                            focusManager.clearFocus()
                                                        }
                                                    ),
                                                    textStyle = TextStyle(
                                                        color = Color.Black,
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        textAlign = TextAlign.Center
                                                    ),
                                                    modifier = Modifier
                                                        .width(40.dp)
                                                        .clip(RoundedCornerShape(16.dp))
                                                        .background(white)
                                                        .padding(
                                                            vertical = 12.dp,
                                                            horizontal = 8.dp
                                                        )
                                                        //OMG cursor not gone when you tap outside keyboard BRUHHH
                                                        .onFocusChanged { focusState ->
                                                            if (!focusState.isFocused) {
                                                                keyboardController?.hide()
                                                            }
                                                        },
                                                    singleLine = true
                                                )

                                                //3-Add
                                                IconButton(onClick = { if (quantity < currentStock!!) quantity++ }) {
                                                    Icon(
                                                        Icons.Filled.Add,
                                                        contentDescription = null,
                                                        tint = Color.Black
                                                    )
                                                }

                                            }

                                        }


                                        //new button and viewModel stuff
                                        val addToCartLoading by viewModel.addToCartLoading.collectAsState()
                                        val cartResult by viewModel.cartResult.collectAsState()
                                        //ooh if it's not null -> success !! -> ?.let
                                        val errorMessage by viewModel.errorMessage.collectAsState()

                                        LaunchedEffect(cartResult) {
                                            cartResult?.let {
                                                toastMessage = "Added to cart!"
                                                isToastError = false
                                                showToast = true

                                                viewModel.clearCartResult() // clear here ?
                                            }
                                        }

                                        LaunchedEffect(errorMessage) {
                                            if (errorMessage.isNotBlank()) {
                                                toastMessage = errorMessage
                                                isToastError = true
                                                showToast = true

                                                viewModel.clearErrorMessage()
                                            }
                                        }

                                        //STOCK HERE ??
                                        Button(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(65.dp)
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
                                                //need IDS from the maps! -> values -> ids,
                                                //mapNotNull !!! SKIPS THE NULLS automatically!
                                                val variantIds =
                                                    currentSelectedVariants.values.mapNotNull { it?.id }
                                                val customizationIds =
                                                    currentSelectedCustomizations.values.mapNotNull { it?.id }

                                                viewModel.addToCart(
                                                    productId = product.id,
                                                    variantIds = variantIds,
                                                    customizationIds = customizationIds,
                                                    quantity
                                                )

                                            },
                                            enabled = product.stock > 0 && !addToCartLoading
                                        ) {
                                            //loading spinner hmm maybe overkill..
                                            if (addToCartLoading) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(20.dp),
                                                    color = Color.Yellow,
                                                    strokeWidth = (4.5).dp
                                                )
                                            } else {
                                                val addButtonText = when (product.stock) {
                                                    0 -> "Out of Stock"
                                                    in 1..15 -> " Add to Cart (${currentStock} remaining)"
                                                    //RANGE NEEDS IN ketword lol
                                                    else -> "Add to Cart"
                                                }

                                                Text(
                                                    text = addButtonText,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.Yellow,
                                                )
                                                Spacer(Modifier.width(4.dp))
                                                Icon(
                                                    imageVector = Icons.Filled.ShoppingCart,
                                                    contentDescription = "Add to cart",
                                                    tint = Color.Yellow
                                                )
                                            }
                                        }
                                    }
                                }
                            }//end of item

                            item {
                                //YOUR review
                                val addReviewLoading by viewModel.addProductReviewLoading.collectAsState()

                                //add review result
                                val addReviewResult by viewModel.reviewResult.collectAsState()

                                AddReviewCard(
                                    isLoading = addReviewLoading,
                                ) { rating, comment ->// on submit
                                    viewModel.addProductReview(
                                        rating = rating,
                                        comment = comment,
                                        productId = product.id
                                    )
                                }


                                LaunchedEffect(addReviewResult) {
                                    addReviewResult?.let {
                                        toastMessage = "Review submitted, pending approval"
                                        isToastError = false
                                        showToast = true

                                        viewModel.clearReviewResult()
                                    }
                                }

                                //reviews !!

                                val visibleReviewsCount by viewModel.reviewCount.collectAsState()
                                //sliced
                                val displayedReviews = reviews.take(visibleReviewsCount)
                                //list -> .take (int) uhm

                                Box(
                                    modifier = Modifier.wrapContentSize()
                                ) {
                                    Column(

                                    ) {
                                        Text(
                                            text = "Check out what others have said about this product",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            modifier = Modifier
                                                .padding(vertical = 8.dp)
                                        )
                                        if (loadingReviews) {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator(
                                                    color = Color.Black,
                                                    modifier = Modifier
                                                        .size(32.dp),
                                                    strokeWidth = 4.5.dp
                                                )
                                            }
                                        } else {
                                            if (displayedReviews.isEmpty()) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center
                                                ) {
                                                    Text(
                                                        text = "No body has sipped on this yet!",
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = Color.Black,
                                                        modifier = Modifier
                                                            .padding(vertical = 8.dp)
                                                    )
                                                }
                                            } else {
                                                displayedReviews.forEach { review ->
                                                    reviewCard(
                                                        review = review
                                                    )
                                                    Spacer(Modifier.height(10.dp))
                                                }
                                            }
                                        }
                                        //load more button
                                        if (visibleReviewsCount < reviews.size) {
                                            TextButton(
                                                onClick = { viewModel.loadMoreReviews() },
                                                modifier = Modifier.align(Alignment.CenterHorizontally)
                                            ) {
                                                val remainingReviewCount =
                                                    reviews.size - visibleReviewsCount
                                                Text(
                                                    text = "Show more reviews(${remainingReviewCount} left)",
                                                    color = Color.Black,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    letterSpacing = 1.sp
                                                )
                                            }
                                        }

                                    }
                                }


                                //space
                                Spacer(Modifier.height(80.dp))
                            }//end of this item hmm

                        }//end of lazy column
                    }


                }

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
            } //end of the box
        }

}


//customizationss
@Composable
fun ProductCustomizationsHandler(
    customizations: List<CustomizationDTO>,

    //reacting to the map of selecte customiations changing
    onCustomiationSelectionChanged: (
            selectedVustomiations: Map<String, CustomizationDTO?>,
            delta: Double,
            ) -> Unit
){

    //grouping by key, nvm
    val selectedCustomizations = remember { mutableStateMapOf<String, CustomizationDTO?>() }


    //react
    LaunchedEffect(selectedCustomizations.toMap()) {
        val delta =  selectedCustomizations.values.sumOf { it?.extraPrice ?:0.0 }

        onCustomiationSelectionChanged(
            selectedCustomizations,
            delta // NO MORE SENDING FULL PRICE
        )
    }

    //display

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier=Modifier.padding(vertical=8.dp)
    ){
        Text(
            text="Select Customizations",
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            color = Color.Black
        )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ){
                customizations.forEach {customization ->
                    val isSelected = selectedCustomizations[customization.name] ==customization

                    CustomChip(
                        label=customization.name,
                        selectable = customization.available,
                        selected = isSelected,
                        onClick = {
                            if (isSelected){
                                selectedCustomizations.remove(customization.name)//needs key
                            }else {
                                selectedCustomizations += customization.name to customization
                            }
                        },
                        //design
                        ContainerColor = white.copy(0.7f),
                        SelectedContainerColor = Color.Black.copy(0.9f),
                        SelectedLabelColor = Color.Yellow,
                        )
                }
            }
        }
}


@Composable
fun ProductVariantsHandlerV2(
    variants: List<VariantDTO>,
    basePrice: Double,
    baseStock: Int,
    onVariantSelectionChanged: (
        selectedVariants: Map<String, VariantDTO?>,
        totalPrice: Double,
        stock:Int?,
        //allMandatorySelected: Boolean  //pre selecting ;)
            ) -> Unit
){

    //IAMGESSs not mine for now
    /*
    val variantImages = mapOf(
        "small" to R.drawable.cup_image_small,
        "medium" to R.drawable.cup_image_medium,
        "large" to R.drawable.cup_img_large,
        "low" to R.drawable.ice_low,
        "regular" to R.drawable.ice_normal,
        "high" to R.drawable.ice_more
    )

     */
    //TODO each variant should have its image bruh part of the DTO

    //1- grouping varitants, groupBy -> Map of key to group by, so all size together..
    val groupedVariants = variants.groupBy { it.attribute }
    //none selected at first BUT might pre selected .. NEED to preselect bruh
    val selectedVariants = remember {
        mutableStateMapOf<String,VariantDTO?>().also{ map->
            //not all drinks are cold.. hmm
            //NAHH isnteaad caclualte the lowest cost variant -> add to map boom

            //TODO this .also lets you also initliaze the map + fill in the same bloc..
            groupedVariants.forEach { (attribute,variants)->
                //cheapest
                val cheapestVariant = variants.minByOrNull { it.priceDelta ?: 0.0 }
                map[attribute] = cheapestVariant
            }

        }
    }

    //price + stock ?
    val price = basePrice + selectedVariants.values.sumOf {it?.priceDelta?: 0.0}

    val stock = selectedVariants.values
        .mapNotNull { it?.Stock }
        .minOrNull() ?: baseStock


    //WHAT if selected ones change ?
    LaunchedEffect(selectedVariants.toMap()) {

        //re calcualte HERE the stock omg this screen..
        val freshStock = selectedVariants.values
            .mapNotNull { it?.Stock }
            .minOrNull() ?: baseStock

        onVariantSelectionChanged(
            selectedVariants,
            price,
            stock
        )
    }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            groupedVariants.forEach { (attribute, variants) ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = white.copy(0.7f),
                    modifier = Modifier
                        .padding( vertical = 4.dp,)
                ) {
                    Column(
                        modifier=Modifier.padding(horizontal = 16.dp, vertical=8.dp)
                    ) {

                        Text(
                            text ="${attribute} Options",
                            style = MaterialTheme.typography.titleMedium,
                            //fontWeight = FontWeight.SemiBold,
                            //fontSize = 22.sp,
                            //color = Color.Black,
                            modifier=Modifier.padding(bottom=4.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            variants.forEach { variant ->
                                //fetch from map thennn
                                //val image = variantImages[variant.value.lowercase()]
                                 //   ?: R.drawable.ic_launcher_background //TODO FALLBACK IMG

                                val isSelected = selectedVariants[attribute] == variant

                                Box(
                                    modifier = Modifier
                                        .weight(1f)//gosh this thing..
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(if (isSelected) Color.Black.copy(0.2f) else Color.Transparent)
                                        .clickable {
                                            selectedVariants[attribute] = variant
                                        }
                                        .padding(10.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        SubcomposeAsyncImage(
                                            model = variant.imageUrl,
                                            contentDescription = variant.value,
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier
                                                .width(80.dp)
                                                .height(115.dp)
                                                .clip(
                                                    RoundedCornerShape(
                                                        20.dp
                                                    )
                                                ),
                                            loading = {
                                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                    CircularProgressIndicator(
                                                        strokeWidth = 2.5.dp,
                                                        modifier = Modifier
                                                            .size(24.dp),
                                                        color = Color.Black
                                                    )
                                                }
                                            },
                                            //FALLBACK inside the error bloc..
                                            error = {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.LocalDrink,
                                                        contentDescription = "fallback variant Image",
                                                        tint = Color.Gray.copy(0.5f),
                                                        modifier = Modifier.size(56.dp)
                                                    )
                                                }
                                            }

                                        )
                                        Text(
                                            text = variant.value,
                                            fontSize = 12.sp,
                                            color=Color.Black,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
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


//add YOUR review !
@Composable
fun AddReviewCard(
    isLoading: Boolean,
    onSubmitReview:(
        rating: Int,
        comment: String,
    ) -> Unit,
) {
    var selectedRating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = white.copy(0.7f),
        modifier = Modifier
        .padding( top = 8.dp,)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Add a review",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.Black
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (i in 1..5) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_lemon),
                        contentDescription = "Rate $i",
                        modifier = Modifier
                            .size(26.dp)
                            .clickable { selectedRating = i },
                        colorFilter = ColorFilter.tint(
                            if (i <= selectedRating) Color(0xFFE8D755)
                            else Color.LightGray,
                            blendMode = BlendMode.SrcIn
                        )
                    )
                }
            }

            //TODO change to the new design
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
                        value = comment,
                        onValueChange = { comment = it },
                        placeholder = "What a lemon...",
                        modifier = Modifier
                            .weight(1f)
                            .zIndex(1f),
                        isPasswordField = false,
                        isSearchField = false,
                        maxLines = 5
                    )
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clickable {
                                if (selectedRating > 0 && comment.isNotBlank()) {
                                    onSubmitReview(
                                        selectedRating,
                                        comment
                                    )
                                    selectedRating = 0
                                    comment = ""
                                }
                            },
                        horizontalArrangement = Arrangement.spacedBy(
                            5.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.Black,
                                modifier = Modifier
                                    .size(24.dp),
                                strokeWidth = 4.5.dp
                            )

                        } else {

                            Text(
                                text = "Submit",
                                color = Color.Black,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Icon(
                                imageVector = Icons.Outlined.Send,
                                tint = Color.Black,
                                contentDescription = "Submit product review",
                                modifier = Modifier
                                    .size(22.dp)
                            )
                        }
                    }

                    /*
                    Button(
                    modifier = Modifier
                        //.fillMaxWidth()
                        //.weight(0.3f)
                        .height(60.dp)
                        .widthIn(min = 130.dp, max = 250.dp)
                        .padding(8.dp),

                    contentPadding = PaddingValues(horizontal = 11.dp, vertical = 4.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color(0xFFE8D755),
                        disabledContainerColor = Color(0xFF3A3939)
                    ),

                    onClick = {
                        //mhm send this up
                        if (selectedRating > 0 && comment.isNotBlank()) {
                            onSubmitReview(
                                selectedRating,
                                comment
                            )
                            selectedRating = 0
                            comment = ""
                        }
                        //woops clear it

                    },
                    enabled = selectedRating > 0 && comment.isNotBlank()
                ) {

                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.Yellow,
                            strokeWidth = (4.5).dp
                        )
                    } else {
                        Text(
                            text = " Submit",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Yellow,
                            letterSpacing = 1.sp
                        )

                        Spacer(Modifier.width(4.dp))

                        Icon(
                            imageVector = Icons.Filled.Send, //TODO ?
                            contentDescription = "Add to cart",
                            tint = Color.Yellow
                        )
                    }

                }

                         */


                }//end of row
            }
        }
    }
}



//reviews finally FIXED

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun reviewCard(review: ReviewDTO) {
        //simpler maybe ? REDESIGN
        Box(
            modifier = Modifier
                .wrapContentSize()
                .clip(RoundedCornerShape(20.dp))
                .background(white.copy(0.7f))// maybe keep it white only ?
                .padding(vertical = 5.dp, horizontal = 12.dp)

        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),

                ) {
                //username + rating + date as well ?
                //outer row ->spacedBetween
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //row 1 -> name + rating bolder color
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        //1-name + also img would be good.. //TODO review user image ?
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier.wrapContentWidth()
                        ) {
                            Icon(
                                imageVector = Icons.TwoTone.Person,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = review.customerName ?: "",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                        //2-rating, starts ? -> repeat inside a spaceby(2.dp) row ?

                        Box(

                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                repeat(5) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_lemon),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        colorFilter = ColorFilter.tint(
                                            Color.LightGray,
                                            blendMode = BlendMode.SrcIn
                                        )
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                repeat(review.rating ?: 0) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_lemon),
                                        contentDescription = "rating: ${review.rating}",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                        }
                        //end of stars row
                    }
                    //end of first row

                    //date
                    //TODO change to "ago" date
                    Text(
                        text = dateAgo(review.dateCreation),
                        //the hel is take 10 -> YYYY-MM-DD lool that's.. 8 fields tho, counts the - .. oh
                        //fontSize = 16.sp,
                        //fontWeight = FontWeight.Normal
                        style = MaterialTheme.typography.bodySmall,
                        color=Color.Gray
                    )


                }
                Spacer(Modifier.height(4.dp))
                //okay..
                //i like this pattern remmeber the ?.let fires only when not null!
                review.comment?.let {
                    Text(
                        text = it, // this pattern also makes it.. called as it
                        style = MaterialTheme.typography.bodyMedium

                    )
                }

                /*
            HorizontalDivider(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(50.dp)),

                thickness = 3.dp,
                color = Color.Black.copy(0.8f)

            )

             */


            }
        }
    }


    //date ago
    @RequiresApi(Build.VERSION_CODES.O)
    fun dateAgo(date: String?): String {
        if (date.isNullOrBlank()) return ""

        val cleanedString = date.trim()
        val localDateTime = LocalDateTime.parse(cleanedString)

        //now lib does it
        //crahes ? try then bruh hel nah
        try {
            return PrettyTime().format(localDateTime)
        } catch (e: Exception) {
            return ""
        }
    }













