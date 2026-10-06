package com.example.summery.uipages

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AddLocation
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.summery.AppToast
import com.example.summery.DialogMenu
import com.example.summery.ScreenTransition
import com.example.summery.ui.components.yellow
import kotlinx.coroutines.delay


@Composable
fun AccountScreen(
    navController: NavController,
    viewModel: AccountViewModel,

    //sub screens
    onOrdersClicked:() -> Unit,
    onEditInfoClicked:() -> Unit,
    onManageAddressesClicked: () -> Unit,
){


    //TOAST again
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var isToastError by remember { mutableStateOf(false) }




    //wohoo UiState + Optimistic ui BOTHH
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    //user info
    val displayEmail = uiState.user?.email ?: "N/A"
    val displayFirstName = uiState.user?.firstName ?: ""
    val displayLastName = uiState.user?.lastName ?: ""

    //error message for toast from the uiState...  ALREADY COLLECTED
    LaunchedEffect(uiState.errorMessage){
        val message= uiState.errorMessage
        if(!message.isNullOrEmpty()){
            toastMessage=message
            isToastError = true
            showToast = true
            viewModel.clearErrorMessage()
        }
    }

    var showLogOutDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxSize(),

        color = Color(0xFFFFF89F)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            ScreenTransition(
                isDataLoading = false,//nothing to load kinda ?
                delayMillis = 0,
                withSpinner = false
            ) {

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
                                text = "Account Management",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontFamily = FontFamily.SansSerif,
                            )
                        }

                    }// end of stikcy header

                    item {
                        AccountHeader(
                            firstName = displayFirstName,
                            lastName = displayLastName,
                            email = displayEmail,
                            modifier = Modifier
                        )
                    }


                    //MOVED TO A NEW SCREEN ENTIRELY
                    /*
                    items(
                        count = lazyOrderItems.itemCount,
                        key = lazyOrderItems.itemKey { it.orderId }
                    ) { index ->
                        val order = lazyOrderItems[index]
                        if (order != null) {
                            OrderCardItem(
                                orderItem = order,
                                onClick = {},//TODO order page ?? or ?
                            )
                        }
                    }

                     */
                    item {
                        AccountMenuSection(
                            onMenuItemClick = { item ->
                                when (item) {
                                    is AccountMenuItem.ChangeUserInformation -> {
                                        onEditInfoClicked()
                                    }

                                    is AccountMenuItem.OrderHistory -> {
                                        onOrdersClicked()
                                    }

                                    is AccountMenuItem.ManageAddresses -> {
                                        onManageAddressesClicked()
                                    }

                                    is AccountMenuItem.LogOut -> {
                                        //it's TIMMEEEE dialog with message and log out red pastel + cancel button,
                                        showLogOutDialog = true
                                    }
                                }//end of when
                            }
                        )
                    }


                }
            }

            if (showLogOutDialog){

                LogOutDialog(
                    modifier = Modifier,
                    onDismissRequest = {showLogOutDialog=false},
                    onLogOut = {
                        showLogOutDialog=false
                        viewModel.logout {  }
                    },
                    onCancelClick = { showLogOutDialog=false},

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
        }//end of outer box


    }


}

//can this be a standalone function ?
@Composable
fun LogOutDialog(
    modifier: Modifier=Modifier,
    onDismissRequest: () -> Unit,
    onLogOut: () -> Unit,
    onCancelClick: () -> Unit,

){
        DialogMenu(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                ,//wrap content height
            onDismissRequest =  onDismissRequest,

            ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Log out",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Are you sure you want to exit? You will need to log in again the next time you access the app",
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            onLogOut()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE35555),
                            contentColor = White
                        ),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            "Log Out",
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick =  onCancelClick ,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.Yellow
                        ),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            "Cancel",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

            }
        }//end of dialog

}

//card at top -> circle -> user initials
//omg u can define custom functions for TYPES???
@Composable
fun AccountHeader(
    firstName: String,
    lastName:String,
    email:String,
    modifier: Modifier = Modifier
){

    val context=LocalContext.current
    //surname + name initials
    val initials = remember (firstName,lastName){
        //first or null -> elivs handle the null ooh
        "${firstName?.firstOrNull()?.uppercase() ?: ""}${lastName?.firstOrNull()?.uppercase() ?: ""}"
    }
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.Black.copy(alpha = 0.08f),
        modifier = modifier
            //.padding(16.dp)
            .fillMaxWidth()

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "$firstName $lastName",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black.copy(0.2f))
                        .padding(4.dp)
                        .clickable {
                            if (!email.isNullOrBlank()) {
                                val clipboard =
                                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                                val clipData = ClipData.newPlainText("User Email", email)
                                clipboard.setPrimaryClip(clipData)
                            }
                        },
                    contentAlignment = Alignment.Center,

                    ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        tint = Color.Black,
                        contentDescription = "Copy user email address",
                    )
                }
            }
        }
    }

}



//this is the pro way isn't it..
sealed class AccountMenuItem(
    val id: String,
    val label: String,
    val icon: ImageVector
){
    data object ChangeUserInformation: AccountMenuItem("changeEmail","Account Information",Icons.Outlined.AccountCircle)
    data object OrderHistory: AccountMenuItem("orderHistory","My Orders",Icons.Outlined.ShoppingCart)
    data object ManageAddresses: AccountMenuItem("manageAddresses","My Addresses",Icons.Outlined.AddLocation)
    //log out
    data object LogOut: AccountMenuItem("LogOut","Log Out",Icons.Outlined.Logout)


}

//REDESIGN minimal account screen: Orders, Addresses, change email/name + log out
@Composable
fun AccountScreenItem(
    item: AccountMenuItem,
    onClick: () -> Unit,
    modifier:Modifier=Modifier,
){
    //ONLY when actively tapping -> color change pressed
    //val interactionSource = remember { MutableInteractionSource() }
    //val isPressed by interactionSource.collectIsPressedAsState()


    val isPressed =remember { mutableStateOf(false) }
    //isPressedAsState exists omg ..

    Surface(
        shape=RoundedCornerShape(12.dp),
        color=  if (isPressed.value) Color.Black.copy(alpha = 0.08f) else Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit){
                detectTapGestures(
                    onPress = {
                        isPressed.value=true
                        tryAwaitRelease() //HOLDS the press state ?
                        delay(50)
                        isPressed.value=false
                    },
                    onTap = {onClick()}
                )
            }

    ){
        Row(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(vertical = 6.dp, horizontal = 12.dp)

            ,
            horizontalArrangement = Arrangement.spacedBy(6.dp),//for the icon
            verticalAlignment = Alignment.CenterVertically
        ){
            //1- icon
            val iconColor = if(item.id=="LogOut") Color(0xFFE35555) else Color.Black
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint=iconColor
            )

            //2- label
            Text(
                text = item.label,
                style = MaterialTheme.typography.titleMedium,
                color = iconColor
            )

        }
    }
}

//the one to be called in the main screen
@Composable
fun AccountMenuSection(
    onMenuItemClick: (AccountMenuItem) -> Unit,
    modifier: Modifier= Modifier
){
    val items = remember {
        listOf(
            AccountMenuItem.ChangeUserInformation,
            AccountMenuItem.OrderHistory,
            AccountMenuItem.ManageAddresses,
            AccountMenuItem.LogOut
        )
    }

    Column(
        modifier=modifier
            .fillMaxWidth()
            .padding(top=10.dp)
        ,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        items.forEach { item ->
            AccountScreenItem(
                item = item,
                onClick ={onMenuItemClick(item)} , //TODO naviagate
                modifier=Modifier
            )
        }
    }
}



