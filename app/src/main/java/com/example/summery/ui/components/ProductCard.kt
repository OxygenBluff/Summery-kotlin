package com.example.summery.ui.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.summery.network.ProductResponseDTO
import com.example.summery.network.VariantDTO


val yellow =  Color(0xFFFFF89F)
val white =  Color(0xFFF6F6F2)

//DISCOUNT HANDLER!
fun calculateLowestConfigurationPrice(
    basePrice:Double,
    variants:List<VariantDTO>
):Double {
    //again cheapest combination of variants
    val cheapestDelta = variants
        .groupBy { it.attribute }//GIVES MAP -> each key = the same attribute,different price deltas
        .values
        .sumOf {//sum -> each min in a local group values based on prix delta same yeppie yeppie
                variantGroup ->
            variantGroup.minOf { it.priceDelta ?: 0.0 }
        }
    return basePrice + cheapestDelta
}



@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ProductCard(
    product: ProductResponseDTO,
    onClick: () -> Unit,
    onAddClick:()->Unit,
    modifier: Modifier= Modifier
){
    val isDiscounted=product.DiscountedPrice!=null

    //TODO completley rehalling this ..
    //first 65% -> image filled in bleeding edge all width
    //next -> name + maybe remove description
    //-> last = prices + on the right add to cart button otherwise clicking everywhere on it -> produdct screen

    val shadowGradient = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            Color.Black.copy(alpha = 0.8f)
        )
    )

    Card(
        onClick= onClick,
        modifier=modifier
            .fillMaxWidth()
            .padding(4.dp)// was 4
            .height(230.dp), // TODO with or without this ??
        shape= RoundedCornerShape(20.dp),
        colors= CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        //border = Border
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ){
        Column(modifier = Modifier.fillMaxSize()) {

            //1-Image, padding at top a bit -> bleeding edge to edge no padding
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(137.dp)
                    .background(Color.Black.copy(0.15f)),
                contentAlignment = Alignment.Center
            ) {
                if (product.images.isNotEmpty()) {
                    SubcomposeAsyncImage ( //?
                        model = product.images.first(), // first one -> others in the product screen
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,// originally fit
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(0.dp),//was 8
                        //GLIDE!

                        loading = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = Color.Black.copy(alpha = 0.8f),
                                    strokeWidth = 4.5.dp,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        },
                        error = {
                            Text(
                                text = "Couldn't find product image.. ",
                                fontSize = 16.sp
                            )
                        }
                    )
                } else {
                    Text(text = "🧃", fontSize = 32.sp)
                }
            }
            //Spacer(modifier = Modifier.height(3.dp))

            //2-name -> no padding so wrap in a box -> add padidng and all right ?
            //also last one price + button -> row


        Box(
            modifier = Modifier
                .fillMaxWidth()
                //.fillMaxHeight(0.5f)
                //.align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.95f)
                        )
                    )
                )
        ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    //1- name
                    Text(
                        modifier= Modifier.padding(start=8.dp,end=8.dp,top=2.dp, bottom = 0.dp),
                        text = product.name,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2, //OOH! -> creates height problems!!
                        minLines = 2, //THIS FIXED IT!
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )
                    //3-strikethough for discounted price, else regular
                    //+ the button on the right so -> row -> sapce between ?
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp,),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        //1-prices
                        Column(
                            modifier = Modifier
                                .padding(start = 8.dp)

                        ) {
                            val lowestBasePrice = remember(product) {
                                calculateLowestConfigurationPrice(product.price,product.variants)
                            }

                            if (isDiscounted && product.DiscountedPrice != null) {

                                //BACKEND GIVES THE LOWEST ONE for the discounted case ONLY
                                Text(
                                    text = String.format("%.2f DT", lowestBasePrice), // Original
                                    //String.format! nice one huh the % is the string itself ? nice
                                    color = white,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    textDecoration = TextDecoration.LineThrough,
                                    style = LocalTextStyle.current.copy(
                                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                                    ),

                                    //stupid hidden padding
                                )

                                val lowestDiscountedPrice = remember(product) {
                                    product.lowestPrice ?: null
                                }

                                Text(
                                    text = String.format(
                                        "%.2f DT",
                                        lowestDiscountedPrice
                                    ), // Promo Price
                                    color = Color.Yellow,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.offset(y = (-2).dp),//TODO hmm ?
                                    style = LocalTextStyle.current.copy(
                                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                                    )
                                )
                                //Spacer(modifier.height(12.dp))
                            } else {

                                Spacer(modifier = Modifier.height(17.dp))
                                //OCCUPY SAME SPACE

                                Text(
                                    text = String.format("%.2f DT",lowestBasePrice),
                                    color = Color.Yellow,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    style = LocalTextStyle.current.copy(
                                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                                    )
                                )
                            }
                        }
                        //2-Button

                        //NAHHH nope

                        Button(
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(8.dp)

                                .defaultMinSize(minHeight = 1.dp, minWidth = 1.dp),
                            contentPadding = PaddingValues(horizontal = 11.dp, vertical = 4.dp),
                            //TODO this is very smart!
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color(0xFFE8D755),

                                ),
                            onClick = onClick,//pff
                            enabled = product.stock > 0
                        ) {

                            Text(
                                text = "Offer",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Yellow,
                                letterSpacing = 1.sp,
                                modifier=Modifier.padding(horizontal = 1.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward, //was shopping Cart
                                contentDescription = "Go to product page",
                                tint=Color.Yellow
                            )

                        }




                    }
                }
            }
        }


    }
}