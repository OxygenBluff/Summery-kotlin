package com.example.summery.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.summery.network.ProductResponseDTO
import com.example.summery.ui.components.white
import com.example.summery.ui.components.yellow
import kotlinx.serialization.Serializable


@Serializable object LoginDestination
@Serializable object SignupDestination
@Serializable object HomeDestination

@Serializable
data class ProductDetailsDestination(
    val product: ProductResponseDTO
)

//placeholders
@Serializable object CartDestionation
@Serializable object placeHolderDestination1
@Serializable object placeHolderDestination2


//sealed or Enum ?
sealed class BottomBarItem(
    val title: String, val icon: ImageVector, val destination: Any
){
    object Home: BottomBarItem("Home",Icons.Filled.Home, HomeDestination)
    object Cart: BottomBarItem("Cart",Icons.Filled.ShoppingCart, CartDestionation)
    object placeHolder1: BottomBarItem("plhldr1",Icons.Filled.AddCard, placeHolderDestination1)
    object placeHolder2: BottomBarItem("Account",Icons.Filled.AccountCircle, placeHolderDestination2)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun bottomNavBar(
    navController: NavController
) {
    val items = listOf(
        BottomBarItem.Home,
        BottomBarItem.Cart,
        BottomBarItem.placeHolder1,
        BottomBarItem.placeHolder2
    )

    NavigationBar(
        containerColor = Color.Black.copy(0.8f),
        tonalElevation = 0.dp,
        modifier=Modifier
            .padding(horizontal=4.dp, vertical=4.dp)
            .clip(RoundedCornerShape(42.dp))
            .height(65.dp)
            ,

    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        CompositionLocalProvider(LocalRippleConfiguration provides null) {

            items.forEach { item ->
                val isSelected = currentDestination?.hasRoute(item.destination::class) == true

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            modifier = Modifier.offset(y = 6.dp)
                        )
                    },
                    label = { Text(text = item.title) },

                    selected = isSelected,


                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = yellow,
                        selectedTextColor = yellow,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = white,
                        unselectedTextColor = white,
                    ),

                    onClick = {
                        if (!isSelected) {
                            navController.navigate(item.destination) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }


                )

            }
        }
    }
}

//the pagination indicator thing
@Composable
fun PaginationBar(
    totalPages: Int,
    currentPage: Int,
    onPageSelected: (Int) -> Unit,
    modifier: Modifier=Modifier
){
    Row(
        modifier=Modifier
            .fillMaxWidth()
            ,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        //PAGES START FROM 0 ?
        //curernt page + one before and after it + last page
        //oh first page also always on

        val visisblePages = remember(currentPage,totalPages) {
            val pages = mutableSetOf<Int>()
            pages.add(0)
            pages.add(totalPages -1 )

            for (i in (currentPage -1) .. currentPage +1){
                if (i in 0 until totalPages){
                    pages.add(i)
                }
            }
            pages.sorted()
        }

        //displaying them time
        var lastPageSeen = -1
        for (pageIndex in visisblePages){
            // ellipsis
            if (lastPageSeen != -1 && pageIndex - lastPageSeen >1){
                Text(
                    text=" ... ",
                    modifier = Modifier.padding(horizontal = 4.dp),
                    color=Color.Black

                )
            }

            val isSelected = currentPage == pageIndex

            Box(
                modifier=Modifier
                    .size(32.dp) // SIZE FIRST OMG... this is stupid
                    .clip(CircleShape)
                    .background(
                        if(isSelected) Color.Black else white
                    )
                    .clickable { onPageSelected(pageIndex)}
                    //.wrapContentSize()
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text=(pageIndex+1).toString(),
                    color = if (isSelected) Color.Yellow else Color.Black,
                    fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.width(2.dp))

            lastPageSeen = pageIndex
        }

    }
}

