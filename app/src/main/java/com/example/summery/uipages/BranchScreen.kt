package com.example.summery.uipages

import android.R.attr.name
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.CallMade
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.ShareLocation
import androidx.compose.material.icons.outlined.AccessTimeFilled
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.example.summery.AppToast
import com.example.summery.R
import com.example.summery.ScreenTransition
import com.example.summery.ui.theme.descriptionText
import com.example.summery.white
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun BranchScreen(
    navController: NavController,
    viewModel: BranchViewModel,
    branchId: Long
) {
    //TOAST again
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var isToastError by remember { mutableStateOf(false) }

    //fetch otherwise a CRASH if it's null hmm that's concerning..
    LaunchedEffect(branchId) {
        viewModel.fetchBranch(branchId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val isLoading = uiState is BranchUiState.Loading


    val HORIZONTAL_PADDING =16.dp
    val ACTION_ICON_SIZE=28.dp

    Surface(
        modifier = Modifier
            .fillMaxSize(),

        color = Color(0xFFFFF89F)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            ScreenTransition(
                isDataLoading = isLoading,
                delayMillis = 120,
                withSpinner = true
            ) {
                when(val state=uiState){
                    is BranchUiState.Loading -> {
                        // SPINNER it's a long list (potentially)
                        Box(
                            modifier=Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ){
                            CircularProgressIndicator(
                                color=Color.Black.copy(alpha=0.8f),
                                strokeWidth = 4.5.dp,
                                modifier=Modifier.size(36.dp)

                            )
                        }
                }

                is BranchUiState.Error -> {
                    LaunchedEffect(state.message) {
                        toastMessage = state.message
                        isToastError = true
                        showToast = true
                    }
                }

                is BranchUiState.Success -> {
                    val branch=state.branch

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentPadding = PaddingValues(bottom=80.dp)
                        ) {
                            //no sticky header adds space..
                            //BIG IMAGE YASS
                            item{
                                BranchHeader(
                                    logoUrl = branch.logo,
                                    name = branch.name ?: "",

                                    )
                            }
                            //item 2 -> Name on the right, RATING + lemon of the left same as product
                            //TODO 16.dp padding everywhere from now on
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp, horizontal = HORIZONTAL_PADDING),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    //name
                                    Text(
                                        text = branch.name,
                                        style = MaterialTheme.typography.headlineLarge,// was 26

                                        modifier = Modifier
                                            .padding(start = 4.dp,end=4.dp,bottom=4.dp)
                                            .alignByBaseline()
                                            .weight(1f),
                                        maxLines = 2
                                    )
                                    //rating

                                    //not elivs.. IF rating not null -> format it ELSE (the let one) -> ?: N/A
                                    val formattedRating = branch.rating?.let {
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
                                            style = MaterialTheme.typography.bodyMedium,// 18

                                        )
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_lemon),
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp),
                                        )
                                    }
                                }
                            }//end of item 2

                            //item3 -> decription
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp, horizontal = HORIZONTAL_PADDING)
                                ) {
                                    Text(
                                        text = "\"${branch.description}\"",
                                        style = MaterialTheme.typography.descriptionText,
                                        modifier = Modifier.padding(bottom = 0.dp)
                                    )
                                }
                            }//end of item3

                            //item 4 -> hours
                            item {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = white.copy(0.7f),
                                    modifier = Modifier
                                        .padding(top = 8.dp,start=HORIZONTAL_PADDING,end=HORIZONTAL_PADDING)
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(6.dp),
                                        horizontalAlignment = Alignment.Start,
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp
                                        )
                                    ) {
                                        Row(
                                            modifier =
                                                Modifier.fillMaxSize(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {

                                            Text(
                                                text = "Working Hours",
                                                style = MaterialTheme.typography.titleMedium,
                                                modifier = Modifier.padding(top=4.dp)
                                            )
                                            Icon(
                                                imageVector = Icons.Outlined.AccessTimeFilled,
                                                tint = Color.Black,
                                                contentDescription = "Store Schedule",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        //1-Opening hours, except sunday
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(
                                                horizontal = 8.dp,
                                                vertical = 4.dp
                                            )
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically,

                                                ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.CalendarMonth,
                                                    tint = Color.Black,
                                                    contentDescription = "Monday to Saturday",
                                                    modifier = Modifier
                                                        .size(15.dp)
                                                )
                                                Text(
                                                    text = "Mon-Sat: ",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = Color.Black.copy(alpha = 0.6f),
                                                    modifier = Modifier.padding(bottom = 0.dp)
                                                )
                                            }

                                            val openingTime =
                                                formatTimeString(branch.openingTime ?: "N/A")
                                            val closingTime =
                                                formatTimeString(branch.closingTime ?: "N/A")

                                            Text(
                                                text = "$openingTime - $closingTime ",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(bottom = 0.dp)
                                            )
                                        }//

                                        //2- Sunday closed //TODO make the working hours payload a MAP in the backend better
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(
                                                horizontal = 8.dp,
                                                vertical = 4.dp
                                            )
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically,

                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.CalendarMonth,
                                                    tint = Color.Black,
                                                    contentDescription = "Sunday",
                                                    modifier = Modifier
                                                        .size(15.dp)
                                                )

                                                Text(
                                                    text = "Sunday: ",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = Color.Black.copy(alpha = 0.6f),
                                                    modifier = Modifier.padding(bottom = 0.dp)
                                                )
                                            }

                                            Text(
                                                text = "Closed",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(bottom = 0.dp)

                                            )
                                        }

                                        //disclaimer to fill the space
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Info,
                                                tint = Color.Gray,
                                                contentDescription = "Working hours disclaimer",
                                                modifier = Modifier
                                                    .size(15.dp)
                                            )
                                            // i thingy
                                            Text(
                                                text = "Delivery coverage & operating hours may vary depending on the branch and special events.",
                                                style = MaterialTheme.typography.labelSmall,
                                                color=Color.Gray,
                                                modifier=Modifier.padding(bottom=4.dp)
                                            )
                                        }
                                    }//end of column

                                }

                            }//end of item 4
                            //item 5 -> Geo location
                            item {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = white.copy(0.7f),
                                    modifier = Modifier
                                        .padding(
                                            top = 8.dp,
                                            start = HORIZONTAL_PADDING,
                                            end = HORIZONTAL_PADDING
                                        )
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(6.dp),
                                        horizontalAlignment = Alignment.Start,
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp
                                        )
                                    ) {
                                        Row(
                                            modifier =
                                                Modifier.fillMaxSize(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {

                                            Text(
                                                text = "Location Details",
                                                style = MaterialTheme.typography.titleMedium,
                                                modifier = Modifier.padding(top = 4.dp)

                                            )


                                            Icon(
                                                imageVector = Icons.Filled.ShareLocation,
                                                tint = Color.Black,
                                                contentDescription = "Store Location",
                                                modifier = Modifier
                                                    .size(20.dp)
                                            )
                                            

                                            
                                        }
                                        
                                        //first off.. address + city 
                                        val actualAddress = if (branch.city.isNullOrBlank() ||branch.address.isNullOrBlank()) "Address unavailable" 
                                            else "${branch.city}, ${branch.address}"
                                        Row(
                                            verticalAlignment = Alignment.Top, // Align Top in case address wraps to 2 lines
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = "Address:",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.Black.copy(alpha = 0.6f)
                                            )
                                            Text(
                                                text = actualAddress ?: "Address unavailable",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.weight(1f) // Ensures long addresses wrap cleanly
                                            )
                                        }
                                        //button for google maps
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,

                                            ) {
                                            Text(
                                                text = "Open in Maps",
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                            val context=LocalContext.current
                                            Box(
                                                modifier=Modifier
                                                    .size(ACTION_ICON_SIZE)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(Color.Black.copy(0.2f))
                                                    .clickable{
                                                        openGoogleMaps(
                                                            latitude = branch.latitude,
                                                            longitude = branch.longitude,
                                                            address = branch.address,
                                                            BranchName = branch.name,
                                                            context =context
                                                        )
                                                    }
                                                ,
                                                contentAlignment = Alignment.Center,
                                                
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Outlined.CallMade,
                                                    tint = Color.Black,
                                                    contentDescription = "Open in Maps app",
                                                )
                                            }
                                            
                                        }
                                       
                                        
                                        
                                    }
                                }
                            }
                            item{
                                contactCard(
                                    phone = branch.phone,
                                    email = branch.email,
                                    modifier = Modifier
                                )
                            }

                        }//end of lazy column
                    }

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

                IconButton(
                    onClick = {
                        //what if nothing before it??
                        if (navController.previousBackStackEntry != null) {
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(start = 16.dp, top = 8.dp)
                        .size(36.dp)
                        .align(Alignment.TopStart)
                        .clip(CircleShape)
                        .background(Color.Black.copy(0.2f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back button",
                        tint = Color.White
                    )
                }

        }//END OF THE OUTERMOST BOX holding everything
    }
}

fun formatTimeString(timeString: String):String{
    if(timeString.isNullOrBlank()){
        return "N/A"
    }
    return try {
        val parsedTime = LocalTime.parse(timeString)
        val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
        //TODO NO WAY IT ADDS THE AM AND PM

        parsedTime.format(formatter)
    }catch(e:Exception){
        timeString // ORIGINAL FALL BACK genius..
    }
}

//trigger google maps .
//SO NO CONTEXT in ViewModel EVER -> Memory leakss
fun openGoogleMaps(
    context: Context,
    latitude: Double?,
    longitude: Double?,
    address:String?,
    BranchName:String
    //WHY NAME ?
    //-> SHows it inside the () instead of the lat, lng, numbers that's it
){
    //Uri.encode needs NON NULLABLE string, i don't think name
    //will ever be null but it's fineee
    val safeName = BranchName?: "Branch Location Dummy placeholder"

    val uri= if(latitude != null && longitude!=null ){
        Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(${Uri.encode(safeName)})")
    }else if(!address.isNullOrBlank()){
        Uri.parse("geo:0,0?q=${Uri.encode("$address,$name")}")
    }else{
        return //D O NOTHING
    }

    //THIS OPENS it,
    val mapItent = Intent(Intent.ACTION_VIEW,uri)
    context.startActivity(mapItent)
}

//INTENTS also for phone call and email DAYUM
fun makeCall(context: Context, phoneNumber:String?){
    if(phoneNumber.isNullOrBlank()) return
    val intent=Intent(Intent.ACTION_DIAL,Uri.parse("tel:$phoneNumber"))
    context.startActivity(intent)
}

//be more modular ffs LMAO
@Composable
fun contactCard(
    phone:String?,
    email:String?,
    modifier:Modifier=Modifier,
    ACTION_ICON_SIZE:Dp =28.dp

) {
    val scope = rememberCoroutineScope() // clipboard is ASYNC now
    val clipboard = LocalClipboard.current

    val context=LocalContext.current

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = white.copy(0.7f),
        modifier = Modifier
            .padding(
                top = 8.dp,
                start = 16.dp,
                end = 16.dp
            )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
        ) {
            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Contact us!",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(top = 4.dp)

                )

                //?
                Icon(
                    imageVector = Icons.Filled.EmojiEmotions,
                    tint = Color.Black,
                    contentDescription = "contact info",
                    modifier = Modifier
                        .size(20.dp)
                        .offset(y = (-1).dp)
                )
            }
            //okayy..
            //1- Phone
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()

                ) {

                //split the text..
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),

                ) {
                    Text(
                        text = "Phone Number:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                    Text(
                        text = phone ?: "N/A",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Box(
                    modifier=Modifier
                        .size(ACTION_ICON_SIZE)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black.copy(0.2f))
                        .clickable {
                            makeCall(
                                context = context,
                                phoneNumber = phone
                            )
                        }
                        .padding(4.dp)
                    //TODO CLICKABLE BEFORE PADDING for the ripple
                    ,
                    contentAlignment = Alignment.Center,

                    ) {
                    Icon(
                        imageVector = Icons.Outlined.Phone,
                        tint = Color.Black,
                        contentDescription = "Open in Phone app",
                    )
                }


            }
            //2- email
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,

                ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    Text(
                        text = "E-mail:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black.copy(alpha = 0.6f)
                    )


                    Text(
                        text = email ?: "N/A",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                Box(
                    modifier = Modifier
                        .size(ACTION_ICON_SIZE)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black.copy(0.2f))
                        .padding(4.dp)
                        .clickable {
                            if (!phone.isNullOrBlank()) {
                                val clipboard =
                                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                                val clipData = ClipData.newPlainText("email", email)
                                clipboard.setPrimaryClip(clipData)
                            }
                        },
                    contentAlignment = Alignment.Center,

                    ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        tint = Color.Black,
                        contentDescription = "Copy email address",
                    )
                }
            }


        }
    }
}



//the one
@Composable
fun BranchHeader(
    logoUrl: String ?,
    name: String,
    modifier:Modifier=Modifier
){
    Box(
        modifier=modifier
            .fillMaxWidth()
            .height(280.dp)
    ){
        //1- image, all edge to edge EVEN THE STATUS PADDING
        //mhm no image fallback
        if (logoUrl.isNullOrBlank()) {
            Icon(
                imageVector = Icons.Outlined.Storefront,
                contentDescription = "Default Store Logo",
                tint = Color.Gray.copy(alpha = 0.6f),
                modifier = Modifier.size(72.dp)
            )
        }else {
            SubcomposeAsyncImage(
                model = logoUrl,
                contentDescription = "${name} branch image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),

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

                error = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Storefront,
                            contentDescription = "fallback branch logo",
                            tint = Color.Gray.copy(0.7f),
                            modifier = Modifier.size(72.dp)
                        )
                    }
                }
            )
        }

        //2-gradient? top + bottom maybe ?
        Box(
            modifier= Modifier
                .fillMaxWidth()
                .height(50.dp)
                .align (Alignment.TopCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors=listOf(
                            Color.Black.copy(0.25f),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier= Modifier
                .fillMaxWidth()
                .height(50.dp)
                .align (Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors=listOf(
                            Color.Transparent,
                            Color.Black.copy(0.25f),

                        )
                    )
                )
        )


    }
}