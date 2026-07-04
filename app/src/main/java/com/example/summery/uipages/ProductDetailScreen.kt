


import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.summery.ScreenTransition
import com.example.summery.network.ProductResponseDTO


//Horizontal pager
@Composable
fun productImagesCarousel(productImages: List<String>){
    if(productImages.isEmpty()) return

    val pagerState = rememberPagerState( pageCount = {productImages.size})
    Box(
        modifier= Modifier
            .fillMaxWidth()
            //.padding(16.dp)
            .height(400.dp)
    ){
        HorizontalPager(
            state=pagerState,
            modifier= Modifier.fillMaxSize()
        ) { page ->
            AsyncImage(
                model=productImages[page], // page is the index bruhh
                contentDescription = "Product image number $page",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        //that dots indicating thing at the bottom..
        //so draw all dots -> current page index -> stronger tint ?
        //active one = page (now from pagerState.currentPage..) same as
        //iteration
        Row (
            modifier= Modifier
                .align(Alignment.BottomCenter)
                .padding(12.dp)
                .wrapContentSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            repeat (productImages.size){ iteration ->

            val isSelected = pagerState.currentPage == iteration

                Box(
                    modifier = Modifier
                        .background(
                            color=if(isSelected) Color.Black.copy(0.8f) else Color.Black.copy(0.3f),
                            shape= CircleShape
                        )
                        .size(6.dp)
                )
            }
        }
    }
}
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ProductDetailScreen(
    //TODO PASS IN THE DTO ?
    product: ProductResponseDTO,
    navController: NavController
){

    //oh this is it
    BackHandler(enabled=true) {
        navController.popBackStack()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),

        color = Color(0xFFFFF89F)
    ){
        ScreenTransition(
            isDataLoading = false,
            delayMillis = 120
        ) {
        //just glide the image lil bro
        Column(
            modifier=Modifier
                .statusBarsPadding()
                .padding(16.dp)

        ) {
            Row(
                modifier=Modifier
                    .fillMaxWidth()
                    .padding(0.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //back icon
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back icon",
                    tint=Color.Black.copy(0.7f),
                    modifier= Modifier
                        .size(26.dp)
                        .clickable{
                            navController.popBackStack()
                            //pop back stack = go back ONE screen
                        }
                )
                Spacer(modifier=Modifier.width(12.dp))

                Text(
                    text = "Product Details",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = FontFamily.SansSerif,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(20.dp))
                    .height(300.dp)
                    .background(Color.Transparent, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                //horizontal page + wanna zoom into pics

                productImagesCarousel(
                    productImages = product.images
                )
            }
        }


        }
    }
}