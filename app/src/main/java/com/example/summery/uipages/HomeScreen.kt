package com.example.summery.uipages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.summery.BottomSheet
import com.example.summery.CustomTextField
import com.example.summery.ScreenTransition
import com.example.summery.navigation.PaginationBar
import com.example.summery.network.ProductResponseDTO
import com.example.summery.ui.components.ProductCard
import com.example.summery.ui.components.yellow


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel,
    onProductClick: (ProductResponseDTO) -> Unit
) {


    //get em
    LaunchedEffect(Unit) {
        homeViewModel.handleLoadingProducts()
        homeViewModel.handleFeaturedProducts()
    }

    //TODO just testing fetching the products...
    val isFetchingProducts by homeViewModel.isFetchingProducts.collectAsStateWithLifecycle()
    //var rawApiResult by remember { mutableStateOf("Fetching products ...") }

    //collectAsStateWithLifecycle() is more effiicnet! when app in background it PAUSES
    val productList by homeViewModel.productsList.collectAsStateWithLifecycle()

    val statusMessage by homeViewModel.statusMessage.collectAsStateWithLifecycle()

    var SearchValue by remember { mutableStateOf("") }
    //this is a UI thing... not viewModel

    val FeaturedProductsList by homeViewModel.featuredProductsList.collectAsStateWithLifecycle()

    // --- PAGINATION

    val currentPage by homeViewModel.currentPage.collectAsStateWithLifecycle()
    val totalPages by homeViewModel.totalPages.collectAsStateWithLifecycle()


    //thismight be pog
    var showBottomSheet by remember { mutableStateOf(false) }


    //search empty -> back to full products list
    LaunchedEffect(SearchValue) {
        if(SearchValue.isBlank()){
            homeViewModel.handleLoadingProducts(animate = false)
            //DO NOT trigger the animation lmaoo
            //wait get account DTO here ?
        }
    }

    val scrollState = rememberScrollState()


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFF89F)
    ) {

        ScreenTransition(
            isDataLoading = isFetchingProducts,
            delayMillis = 0,
            withSpinner = true
        ) {

            Column(
                modifier= Modifier
                    .fillMaxSize()
                    .padding(12.dp)// navbar, nvm
                    .statusBarsPadding()//aha...


            ) {

                Row(
                    modifier=Modifier
                        .fillMaxWidth()
                        .padding(vertical = 0.dp),
                    //Eh ? padding is the pro way to do it ?
                    // -> so it just wrap saround the icon OHH
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){

                    Text(
                        text="Welcome!",
                        color=Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 20.sp,
                        modifier= Modifier
                            .padding(start = 0.dp,bottom=12.dp)// was 10
                    )


                    //CustomDropDownMenu()
                }

                Row(
                    modifier=Modifier
                        .fillMaxWidth()
                        .padding(vertical = 0.dp),
                    //Eh ? padding is the pro way to do it ?
                    // -> so it just wrap saround the icon OHH
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    //search ? but before it a row -> top right for account + settings
                    CustomTextField(
                        value = SearchValue,
                        onValueChange = { SearchValue = it },
                        onSearchAction = {
                            homeViewModel.handleSearch(SearchValue)
                        },
                        placeholder = "a specific juice ?",
                        isSearchField = true,
                        modifier = Modifier.fillMaxWidth(0.9f),

                        )
                    //filters icon ?
                    Icon(
                        imageVector = Icons.Filled.FilterList,
                        contentDescription = "Filters icon",
                        tint=Color.Black.copy(0.7f),
                        modifier= Modifier
                            .size(24.dp)
                            .clickable{
                                //TODO filters
                                showBottomSheet=true
                            }

                    )

                    if(showBottomSheet){
                        BottomSheet(
                            onDismissRequest = {
                                showBottomSheet=false
                            }
                        )
                    }

                }
                //end of column 1 ;


                //3-Featured banner !! -> from here on scrollable imo
                Spacer(modifier.height(12.dp))
                /*
                if(FeaturedProductsList.size>0) {


                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        //.paddint(star)
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Text(
                            modifier = Modifier,
                            text = "On sale!",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black.copy(0.7f)
                        )
                        Spacer(modifier.width(3.dp))
                        Icon(
                            imageVector = Icons.Filled.ArrowForward,
                            contentDescription = "discounted items icon",
                            tint = Color.Black.copy(0.7f),
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    //TODO maybe just apply filter ?
                                }
                        )
                    }

                }
                */

                //should wrap this in a box lowkey..
                //NO DO NOT WRAP a lazy grid into a column :// -> GRID SPANNING! make whatever u want an elemnt of the grid itself


                ///MUST BE WRAPPED IN A 1F WEIGHT! otherwise noooor
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize()
                            .padding(vertical = 8.dp),
                        //contentPadding = PaddingValues(5.dp), // around the edges of the whole grid btw
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = PaddingValues(bottom=50.dp)
                    ) {
                        //SPANNING
                        item(
                            span = { GridItemSpan(maxLineSpan) }
                        ) {
                            Column(
                                modifier = Modifier
                                    .wrapContentSize()

                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    //.paddint(star)
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                ) {
                                    Text(
                                        modifier = Modifier,
                                        text = "On sale!",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black.copy(0.7f)
                                    )
                                    Spacer(modifier.width(3.dp))
                                    Icon(
                                        imageVector = Icons.Filled.ArrowForward,
                                        contentDescription = "discounted items icon",
                                        tint = Color.Black.copy(0.7f),
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clickable {
                                                //TODO maybe just apply filter ?
                                            }
                                    )
                                }

                                FeaturedCarousel(
                                    featuredProducts = FeaturedProductsList,
                                    homeViewModel = homeViewModel,
                                    onProductClick = onProductClick
                                )
                            }
                            //TODO discounted not featured..
                        }

                        items(productList) { Currentproduct ->
                            ProductCard(
                                product = Currentproduct,
                                onClick = {
                                    homeViewModel.updateStatusMessage("ClickedProduct: ${Currentproduct.name}")

                                    val imageUrl = Currentproduct.images.firstOrNull() ?: ""

                                    //TODO making this return a "" if null means more work on the placeholder!
                                    onProductClick(Currentproduct)
                                },

                                onAddClick = {
                                    homeViewModel.updateStatusMessage("Added product: ${Currentproduct.name} to cart")

                                },
                            )
                        }

                        if (totalPages > 1) {
                            item(
                                span = {
                                    GridItemSpan(maxLineSpan)
                                }
                            ) {
                                PaginationBar(
                                    totalPages = totalPages,
                                    currentPage = currentPage,
                                    onPageSelected = { targetPage ->
                                        homeViewModel.handleLoadingProducts(page = targetPage)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp)
                                )
                            }
                        }
                    }
                }


            }

            //end of screen transition container
        }
    }
}

@Composable
//TODO it's discounted
fun FeaturedCarousel(
    featuredProducts: List<ProductResponseDTO>,
    homeViewModel: HomeViewModel,
    onProductClick: (ProductResponseDTO) -> Unit
) {
    val white =  Color(0xFFF6F6F2)

    val pagerState = rememberPagerState(pageCount = {featuredProducts.size })

    val shadowGradient = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            Color.Black.copy(alpha = 0.8f)
        )
    )

        //what do we send for it..
        //like product images, so a productResponseDTO
        //extract image onyl -> inside a box with its discount % ?
        //this should be an admin thing..
        //TODO admin endpoint feed this
    // what about.. disabled if nothing, pops up modern if they suddently do lol
    AnimatedVisibility(
        visible = featuredProducts.size >0,

    ) {
        Box(
            modifier=Modifier
                .wrapContentSize()
                .background(Color.Transparent)
        ) {


            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(white)
            ) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    //WHEN u put it alone like this -> as if a bg

                    //dicount % yellow bg ?

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(0.6f)

                        ) {
                            //discount %
                            val percentage =
                                (1 - (featuredProducts[page].DiscountedPrice!! / featuredProducts[page].price) * 100).toInt()
                            //i mean it SHOULD be discounted if it exists in this list lmao

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(36.dp))
                                    .wrapContentSize()
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                    .background(yellow)
                            ) {
                                Text(
                                    text = "${percentage}%",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                )
                            }
                            Spacer(Modifier.height(12.dp))

                            Text(
                                text = "${featuredProducts[page].name}",
                                fontSize = 28.sp,
                                modifier=Modifier.clickable(){
                                    onProductClick(featuredProducts[page])
                                },
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                style = TextStyle(
                                    textDecoration = TextDecoration.Underline
                                )

                                //TODO clickable !!
                            )
                        }

                        AsyncImage(
                            model = featuredProducts[page].images.firstOrNull(),
                            contentDescription = "Discounted product image number $page",
                            contentScale = ContentScale.None,
                            modifier = Modifier.fillMaxSize()
                        )

                    }


                }


            }
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
                    .wrapContentSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(featuredProducts.size) { iteration ->

                    val isSelected = pagerState.currentPage == iteration

                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSelected) Color.Black.copy(0.8f) else Color.Black.copy(
                                    0.3f
                                ),
                                shape = CircleShape
                            )
                            .size(6.dp)
                    )
                }
            }
        }
    }

}

