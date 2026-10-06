package com.example.summery.uipages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.example.summery.AppToast
import com.example.summery.ScreenTransition
import com.example.summery.network.BranchResponseDTO
import com.example.summery.ui.components.yellow
import com.example.summery.white

@Composable
fun BranchesScreen(
    navController: NavController,
    viewModel: BranchesViewModel,
    onBranchClick: (Long) -> Unit
){
    //TOAST again
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var isToastError by remember { mutableStateOf(false) }

    val branches by viewModel.branches.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val isFetchingBranches by viewModel.isFetchingBranches.collectAsStateWithLifecycle()


    Surface(
        modifier = Modifier
            .fillMaxSize(),

        color = Color(0xFFFFF89F)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            ScreenTransition(
                isDataLoading = isFetchingBranches,
                delayMillis = 120,
                withSpinner=true
            ) {


                LazyColumn(
                    modifier = Modifier
                        .statusBarsPadding(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top=16.dp,
                        bottom = 50.dp
                    )
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
                                        //pop back stack back ONE as always mhm
                                    }
                            )
                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "Our Branches",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontFamily = FontFamily.SansSerif,
                            )
                        }

                    }
                    //the branchess finally
                    //LOADING

                    items(
                        items=branches,
                        key= {branch -> branch.id }
                    ){branch ->
                        BranchCard(
                            branch = branch,
                            onClick = {
                                onBranchClick(branch.id)
                            },
                            modifier=Modifier.animateItem()
                        )
                    }
                }//end of lazy column
            }
            /*
            LaunchedEffect(statusMessage) {
                if(statusMessage.isNotBlank()) {
                    toastMessage = statusMessage
                    isToastError = false
                    showToast = true

                    CartViewModel.clearStatusMessage()
                }
            }

             */

            LaunchedEffect(errorMessage) {
                if(errorMessage.isNotBlank()){
                    toastMessage = errorMessage
                    isToastError = true
                    showToast = true

                    viewModel.clearErrorMessage()
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
        }
    }

}

//REVAMPED
//TODO readability for the text..
/*
Box(
    modifier= Modifier
        .fillMaxSize()
        .clickable{
            onClick()
        }
        .background(
            Brush.verticalGradient(
                colors=listOf(
                    Color.Transparent,
                    Color.Black.copy(0.1f)
                ),
                startY= 60f // ok wow this is handy
            )
        )
)

 */

//that could be good
@Composable
fun BranchCard(
    branch: BranchResponseDTO,
    onClick:() -> Unit= {},
    modifier: Modifier = Modifier
){
    val BOX_SIZE=70.dp

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clickable{onClick()},

        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = white.copy(0.7f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,

        ) {
            //1-image on the left yep BUT with our selected outline
            //WITH THE FALLBACK
            Box(
                modifier = Modifier
                    //.fillMaxWidth(0.3f)
                    .size(BOX_SIZE)
                    .clip(
                        RoundedCornerShape(
                            20.dp
                        )
                    ).background(Color.Black.copy(0.2f))

                    .padding(2.5.dp), //edge to edge image beter, also for the previous cards yh


                contentAlignment = Alignment.Center,

                ) {
                if (branch.logo.isNullOrBlank()) {
                        Icon(
                            imageVector = Icons.Outlined.Storefront,
                            contentDescription = "Default Store Logo",
                            tint = Color.Gray.copy(alpha = 0.6f),
                            modifier = Modifier.size(56.dp)//idk for now
                        )

                } else {
                    //logo the spotlight
                    SubcomposeAsyncImage(
                        model = branch.logo,
                        contentDescription = branch.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
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
                                modifier = Modifier.size(BOX_SIZE),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Storefront,
                                    contentDescription = "fallback branch logo",
                                    tint = Color.Gray.copy(0.7f),
                                    modifier = Modifier.size(56.dp)
                                )
                            }
                        }

                    )
                }
            }//end of left image

            //name + address ? yep  olcumn tho
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)// THE CHEVRON got eaten
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy (6.dp),
                horizontalAlignment = Alignment.Start
            ) {
                //1- Name
                Text(
                    text = branch.name,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    //modifier = Modifier.padding( start = 6.dp, end = 6.dp)
                )
                //2- city and address

                Text(
                    text = "${branch.address}, ${branch.city}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1 ,
                    modifier = Modifier.padding(bottom=4.dp)

                )

            }// end of name + address column

            //3-Chevrolet thingy (?) bro really said chevrolet
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                tint = Color.Black,
                contentDescription = "View this branch's details",
                modifier = Modifier
                    .padding(end=0.dp)
                    .size(28.dp)
            )
        }//end of outer Row

    }
}
