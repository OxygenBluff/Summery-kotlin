package com.example.summery.uipages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.example.summery.BottomSheet
import com.example.summery.CustomSlider
import com.example.summery.CustomTextField
import com.example.summery.ScreenTransition
import com.example.summery.network.ProductResponseDTO
import com.example.summery.slider
import com.example.summery.ui.components.ProductCard
import com.example.summery.ui.components.yellow
import kotlin.math.roundToInt


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel,
    onProductClick: (Long) -> Unit
) {


    //get em
    LaunchedEffect(Unit) {
        homeViewModel.handleFeaturedProducts()
    }

    //TODO just testing fetching the products...

    val productList by homeViewModel.productsList.collectAsStateWithLifecycle()

    val statusMessage by homeViewModel.statusMessage.collectAsStateWithLifecycle()

    val SearchValue by  homeViewModel.searchQuery.collectAsStateWithLifecycle()

    //this is a UI thing... not viewModel, well not anymore

    val FeaturedProductsList by homeViewModel.featuredProductsList.collectAsStateWithLifecycle()


    var showBottomSheet by remember { mutableStateOf(false) }

    //categories
    val selectedCategory by homeViewModel.selectedCategory.collectAsState()

    //all of em
    val categories by homeViewModel.categories.collectAsState()

    //branches em
    val branches by homeViewModel.branches.collectAsState()

    val selectedBranchId by homeViewModel.selectedBranchId.collectAsState()

    //FINALLY PAGING PRODUCTS
    val products: LazyPagingItems<ProductResponseDTO> = homeViewModel.productPagingFlow.collectAsLazyPagingItems()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFF89F)
    ) {

        ScreenTransition(
            isDataLoading = products.loadState.refresh is LoadState.Loading,
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
                        onValueChange = { homeViewModel.onSearchQueryChanged(it) },
                        onSearchAction = {

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
                                showBottomSheet = false
                            },

                            content= {
                                //HOW NOT TO PASS THE REST OF THE SCROLL DELTA UP ?
                                //TODO wth they thought of this

                                val stopNestedDrag = remember {
                                    object: NestedScrollConnection{
                                        override fun onPostScroll(
                                            consumed: Offset,
                                            available: Offset,
                                            source: NestedScrollSource
                                        ): Offset =available //TODO THIS IS HOW TO CONUME LEFTOVER SCROLL
                                    }
                                }
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .nestedScroll(stopNestedDrag)
                                        .verticalScroll(rememberScrollState())
                                        .padding(horizontal = 16.dp)
                                ) {

                                    //1-Categories
                                    Text(
                                        text = "Select a filter",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    //pew -> radio buttons ? might be not so good looking tho..
                                    categories.forEach { category ->
                                        Row(
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                        ) {
                                            RadioButton(
                                                selected = selectedCategory == category.name,
                                                colors = RadioButtonDefaults.colors(
                                                    selectedColor = Color.Black,
                                                    unselectedColor = Color.Black
                                                ),
                                                onClick = {
                                                    homeViewModel.selectCategory(category.name)
                                                }
                                            )
                                            //oh text here lol THAT'S WHY ROW..
                                            Text(
                                                text = category.name,
                                                color = Color.Black,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                    HorizontalDivider(
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp
                                        ),
                                        thickness = 1.dp,
                                        color = Color.LightGray.copy(alpha = 0.4f)
                                    )

                                    //2-Price slider ? -> needs bounds.. fetch most expeensive and least expensive product or realsitic bounds ?
                                    Text(
                                        text = "Adjust price",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    val SliderMin = HomeViewModel.MIN_PRICE
                                    val SliderMax = HomeViewModel.MAX_PRICE
                                    val SliderSelectedRange by homeViewModel.selectedPriceRange.collectAsState()


                                    slider(
                                        selectedRange = SliderSelectedRange,
                                        min = SliderMin,
                                        max = SliderMax,

                                        //MANUALLY nooo
                                        onRangeChangeFinished = { newRange ->
                                            homeViewModel.onPriceRangeChanged(newRange)
                                        },


                                        //TODO HOLD UP WHY NEEDS TO BE LAMDBDA otherwise executed
                                        //right AWAY OR WHAT ?

                                        //TODO:
                                        // { } = hold onto the code BUT DO NOT run only when you let go of the slider
                                        // BUT onValueChange = myViewModel.function () -> RUN IT NOW whrn BUILDING THE UI
                                        //+ whatever result it gives -> pass that onto the paramter
                                    )

                                    HorizontalDivider(
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp
                                        ),
                                        thickness = 1.dp,
                                        color = Color.LightGray.copy(alpha = 0.4f)
                                    )

                                    //3-Branch selection (seller id -> seller name)
                                    Text(
                                        text = "Filter by branch",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        modifier=Modifier.padding(bottom=5.dp)
                                    )
                                    //val chunkedBranches = branches.toList().chunked(3)
                                    //flow row exists...

                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        maxItemsInEachRow = 3,
                                        //horizontalArrangement = Arrangement.spacedBy(10.dp,Alignment.Start),
                                        horizontalArrangement = Arrangement.spacedBy (10.dp,
                                            Alignment.Start),
                                        //?? that is a thing ?
                                        verticalArrangement = Arrangement.spacedBy ( 10.dp )
                                    ) {
                                        branches.forEach { (branchId, branchName) ->
                                            //newer cooler better
                                            BranchItem(
                                                branchId = branchId,
                                                branchName = branchName,
                                                isSelected = selectedBranchId==branchId,
                                                onSelected = {
                                                    homeViewModel.selectBranch(branchId)
                                                },
                                                modifier = Modifier.fillMaxWidth(0.3f)
                                                //weigh(1f) strikes again
                                                //1f = EVENTLY distributed

                                            )
                                        }
                                    }


                                    HorizontalDivider(
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp
                                        ),
                                        thickness = 1.dp,
                                        color = Color.LightGray.copy(alpha = 0.4f)
                                    )

                                    //4-Min rating slider fitler
                                    val minRating by homeViewModel.minRating.collectAsState() // viewmodel + repo =< use Double?
                                    //conversion ?? TODO
                                    Text(
                                        text = "Filter by Rating",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    CustomSlider(
                                        rating = minRating?.toFloat(),
                                        onRatingChange = { newRating ->
                                            val cleanedDouble = (newRating * 10).roundToInt() / 10.0
                                            homeViewModel.selectMinRating(cleanedDouble.toDouble())
                                        },
                                        onRatingReset = {
                                            homeViewModel.selectMinRating(null)
                                        }
                                    )

                                    //5-Reset all
                                    Row(
                                        modifier=Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        TextButton(onClick = {
                                            homeViewModel.resetAllFilters()
                                        }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Close,
                                                tint = Color.Black,
                                                contentDescription = "Clear minimum rating filter for products",
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Text(
                                                text = "Reset All filters",
                                                color = Color.Black,
                                                fontWeight = FontWeight.ExtraBold,
                                                letterSpacing = 1.sp
                                            )
                                        }
                                    }



                                }
                            }//end of content for sheet
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

                        items(
                            count=products.itemCount,
                            key=products.itemKey{ it.id!!}
                        ){index->
                            val product=products[index]
                            if(product!=null){
                                ProductCard(
                                    product = product,
                                    onClick = {
                                        onProductClick(product.id)//yh never doing that again
                                    },
                                    onAddClick = {},
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

//Ooh branch selector
@Composable
fun BranchItem(
    branchId: Long,
    branchName: String,
    isSelected: Boolean,
    onSelected:() -> Unit,
    modifier:Modifier=Modifier
){
    val bgColor = if(isSelected) Color.Black.copy(0.2f) else Color.Transparent

    Surface(
        onClick ={onSelected()},
        shape=RoundedCornerShape(20.dp),
        color=bgColor,
        modifier= modifier
            .fillMaxWidth()
            .height(120.dp)
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier=Modifier.padding(6.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Store,
                contentDescription = branchName,
                tint = Color.Black,
                modifier = Modifier.size(55.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = branchName,
                color = Color.Black,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,

            )
        }
    }
}

@Composable
//TODO it's discounted
fun FeaturedCarousel(
    featuredProducts: List<ProductResponseDTO>,
    homeViewModel: HomeViewModel,
    onProductClick: (Long) -> Unit
) {
    val white =  Color(0xFFF6F6F2).copy(0.7f)

    val pagerState = rememberPagerState(pageCount = {featuredProducts.size })

    val shadowGradient = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            Color.Black.copy(alpha = 0.8f)
        )
    )
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
                                    onProductClick(featuredProducts[page].id)
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

