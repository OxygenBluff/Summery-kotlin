package com.example.summery

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import me.saket.telephoto.ExperimentalTelephotoApi
import me.saket.telephoto.zoomable.OverzoomEffect
import me.saket.telephoto.zoomable.Viewport
import me.saket.telephoto.zoomable.ZoomSpec
import me.saket.telephoto.zoomable.ZoomableState
import me.saket.telephoto.zoomable.coil.ZoomableAsyncImage
import me.saket.telephoto.zoomable.rememberZoomableImageState
import me.saket.telephoto.zoomable.rememberZoomableState
import me.saket.telephoto.zoomable.spatial.CoordinateSpace
import kotlin.math.roundToInt


//Google's default stuff is uh.. something..


@Composable
fun AppToast(
    message: String,
    isVisible: Boolean,
    isError: Boolean = false,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(2500)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = isVisible,

        enter = slideInVertically(initialOffsetY = { it /2 })
                + scaleIn(initialScale = 0.8f)
                + fadeIn(),
        // it <-> BELOW the screen start positive it
        exit = slideOutVertically(targetOffsetY = { it })
                + scaleOut(targetScale = 0.8f)
                + fadeOut(),
        modifier=modifier

    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .heightIn(60.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(Color.Black.copy(0.9f))
                //.background(if (isError) Color(0xFFE56D6D) else Color(0xFF8AE18D))
                .padding(horizontal = 16.dp, vertical=5.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (isError) Icons.Default.Error else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    modifier=Modifier.weight(1f),
                    text=message,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White,
                    fontWeight = FontWeight.Medium)

                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close notification pop up",
                    tint=white,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            onDismiss()
                        }

                )
            }
        }
    }
}
//1-Custom text input field

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearchAction: () -> Unit= {}, //welp.. just make it {} if not  search  OR default {}
    placeholder: String,
    modifier: Modifier = Modifier,
    isPasswordField: Boolean = false, // for the transfomation dots
    isSearchField: Boolean = false,//search icon purely
    keyboardOptions: KeyboardOptions= if(isSearchField) KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
            )
            else  KeyboardOptions.Default,

    //new > make it expandddd ! for reviews
    maxLines: Int =1,
    //color should also
    color:Color= white.copy(0.7f),
    //can add icon at the right ? then it needs a lambda as well
    trailingIcon: @Composable ( () -> Unit)? =null
    //IT CAN BE NULLABLE LMAOOOOO

){
    BasicTextField(
        //basic text field =  the ENGINE
        //unlike textField pr Outlined one (have google's stupid design)
        //THIS has 0 design yes just functionnality !!
        //just the bliking cursor is here btw
        value = value,
        onValueChange = onValueChange,
        maxLines= maxLines, // yess
        modifier = modifier,
        textStyle= TextStyle(
            color = Color.Black,
            fontSize = 16.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium
        ),
        keyboardOptions = keyboardOptions,
        //for the search magnifying glass
        keyboardActions = KeyboardActions (
            onSearch = {
                onSearchAction()
            }
        ),

        visualTransformation = if(isPasswordField) PasswordVisualTransformation() else
            VisualTransformation.None,
        cursorBrush=SolidColor(Color.Black),

        //it's inviisble so..
        //-> Google gave this paramter decorationBox for design !
        //the box or row now
        decorationBox = { innerTextField ->
            Row(
                modifier= Modifier
                    .fillMaxWidth()
                    .heightIn(46.dp)// heighIn instead of fixed height maybe
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        color=color,//wasColor(0xFFF6F6F2).copy(0.7f)

                    )
                    //.border(1.dp, Color.Black.copy(alpha=0.08f), RoundedCornerShape(16.dp))

                    .padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                //cute lock icon on the left very
                if (isPasswordField){
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Lock cute icon",
                        tint=Color.Black.copy(0.3f),
                        modifier= Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }

                if(isSearchField){
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search icon",
                        tint=Color.Black.copy(0.3f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Box(modifier= Modifier.weight(1f)){
                    if (value.isEmpty()){
                        Text(
                            text = placeholder,
                            color = Color.Black.copy(alpha = 0.45f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                    innerTextField()
                    //Google also gave this to make it acctually work now..
                }
                //trailing iconnnn
                if(trailingIcon!=null){
                    Spacer(Modifier.width(8.dp))
                    trailingIcon()
                    //SO.. make it an icon button 2 IN ONE icon + lambda
                }
            }

        }

    )
}

//NOW this spotify transition is the SHIT!
//hmm we got content: @Composable () -> Unit
//means ANY screen can pass its NETIRE ui to this here
@Composable
fun ScreenTransition(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0XFFFFF89F),
    delayMillis: Long = 350,
    withSpinner: Boolean = true,
    isDataLoading: Boolean = false,//default to false, so forced by default
    content: @Composable () -> Unit // can pass in an entire composable.. the lambda
){
    var isInitailLoading by remember { mutableStateOf(true) }

    //actually .. wait for BOTH so works either way
    LaunchedEffect(Unit) {
        delay(delayMillis)
        isInitailLoading=false
    }

    //val overllLoading = isInitailLoading || isDataLoading

    //THE PROBLEM with a simple variable
    //-> simple variable = each time even the network state changes in this case
    //->kotlin re-evalutaes THE ENTIRE function top to bottom
    //too much.. => DerivedStateOf instead!

    val overllLoading = isInitailLoading || isDataLoading
    //TODO kotlin makes it UPDATE ON EITHER

    //->if the evaltion doesn't change (Eg: still true)
    //-> IT WILL NOT make the function get re-evaluated
    //only when the overall evulation changges!

    //why key = isDataLoading only ?
    //-> it's the unpredictable one :/
    //watching one isDataLoading is more efficent

    Surface(
        modifier = modifier.fillMaxSize(),
        color=backgroundColor
    ){
        //1- the spinner, must become OPTIONAL later..
        //or just now..
        AnimatedVisibility(
            visible=overllLoading,
            enter = fadeIn(animationSpec = tween(70)),// true -> false behaviour
            exit = fadeOut(animationSpec=tween(80,easing= LinearEasing))
            //false -> true behaviour of the visible variable!
        ){
            Box(
                modifier=Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
                //should be optional as well
            ){
                if(withSpinner){
                    CircularProgressIndicator(
                        color=Color.Black.copy(alpha=0.8f),
                        strokeWidth = 4.5.dp,
                        modifier=Modifier.size(36.dp)

                    )
                }
            }


        }
        //2-the cotnent of this target screen revleaing itself
        AnimatedVisibility(
            visible=!overllLoading,
            enter=fadeIn(animationSpec = tween(durationMillis = 70,easing=LinearEasing))
        ) {
            content()
            //BOOM! the conent of the ENTIRE page gets dropped here
        }

    }

}

val white =  Color(0xFFF6F6F2)
//3-Drop down menu (button might be next)
///oh wait.. the ICON IS INSIDE THIS!
@Composable
fun CustomDropDownMenu(){
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier= Modifier
            .wrapContentSize(Alignment.TopEnd)
        //why wrap ?
        //-> to ANCHOR itself around the icon, TODO W H A T ?
        //without it it might anchor itself to the corners of the screen
        //WHAT ?
    ) {
        IconButton(
            onClick = { isExpanded = !isExpanded }
        ) {
            //TODO make it customizable! maybe even an image instead of an icon
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Account icon",
                tint = Color.Black.copy(0.3f),
                modifier = Modifier.size(32.dp)
            )
        }
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            shape = RoundedCornerShape(12.dp),
            containerColor = white, // off white kinda
            border = BorderStroke(0.2.dp, Color.Black.copy(0.1f)),
            offset = DpOffset(x = (-10).dp, y = (4).dp)
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        "Account",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                onClick = {
                    isExpanded = false
                    //TODO to account page ?
                },
                contentPadding = PaddingValues(start=16.dp,end=32.dp)

            )
            DropdownMenuItem(
                text = {
                    Text(
                        "Settings",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                onClick = { /* to seetings page */ isExpanded = false },
                contentPadding = PaddingValues(start=16.dp,end=52.dp)
            )
        }
    }

}

@Composable
fun DialogMenu(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
    //SLOT API but wth is dimiss doing there
){


        Dialog(onDismissRequest = onDismissRequest){
            Surface(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(0.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFE1E196),//parchment color lmao
                tonalElevation = 0.dp
            ){
               content()
            }
        }

}

//TODO a horizontal pager is next! u just pass in the list of Composables, scrolling and dots indicators underneath are done for you!
//TODO a super class that composables inherit from to make them like that animation when clicked ? the scale aniamte as float one

//Bottom sheet ?
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier= Modifier,
    //inside
    content: @Composable ColumnScope.() -> Unit,


){
    val isExpanded= rememberModalBottomSheetState(
        skipPartiallyExpanded = false,

    )

    //horizotal scroll naurr

    ModalBottomSheet(
        onDismissRequest= {
            onDismissRequest ()
        },
        sheetState = isExpanded,
        modifier = modifier,

        contentWindowInsets = {WindowInsets.navigationBars},
        //protects content from the system's navbar mhm

        containerColor = Color(0xFFFDF489)
    ) {
        Box(
            modifier=Modifier
                .fillMaxWidth()
                .pointerInput(Unit){
                    //TODO INTERCEPTING DOWNWARD DRAG ? built in ? yessss

                    detectDragGestures { change, _ ->
                        change.consume() // Stops ModalBottomSheet from receiving the drag ??
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f)
                    .padding(start = 2.dp, end = 2.dp, top = 8.dp, bottom = 20.dp)
            ) {
                /*
            Text(
                text = "Select a filter",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

             */


                content() //TODO WRITE THIS DOWN, "slot API pattern"
            }
        }

    }
}

//slider ?
@Composable
fun slider(
    selectedRange: ClosedFloatingPointRange<Float>,
    min: Float =0f,
    max: Float = 500f,



    onRangeChangeFinished: (ClosedFloatingPointRange<Float>) -> Unit
){
    var sliderPos by remember(selectedRange) { mutableStateOf(selectedRange) }

    var isDragging by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 18.dp )

    ){
        Row(
            modifier=Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "Price Range",
                color=Color.Black,
                fontWeight = FontWeight.SemiBold,

            )
            Text(
                text="${sliderPos.start.roundToInt()} TND - ${sliderPos.endInclusive.roundToInt()} TND",
                fontWeight = FontWeight.SemiBold,
                color=Color.Black
            )
        }

        @OptIn(ExperimentalMaterial3Api::class)
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            RangeSlider(
                value = sliderPos,
                onValueChange = { newRange ->

                    sliderPos = newRange
                },
                valueRange = min..max,
                onValueChangeFinished = {
                    onRangeChangeFinished(sliderPos)
                    //ONLY when fingers lifted off screen NO API SPAM


                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top=6.dp)
                ,
                colors = SliderDefaults.colors(
                    thumbColor = Color.Black,
                    activeTrackColor = Color.Black,
                    inactiveTrackColor = Color.Black.copy(0.7f)

                ),
                //LEMONI ?
                startThumb = {

                        Icon(
                            painter = painterResource(id = R.drawable.ic_lemon),
                            contentDescription = "Price range thumb",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(18.dp)
                                .wrapContentSize(unbounded = true)
                        )

                },

                endThumb = {

                    Icon(
                        painter = painterResource(id = R.drawable.ic_lemon),
                        contentDescription = "Price range thumb",
                        tint = Color.Black,
                        modifier =  Modifier
                            .size(18.dp)
                            .wrapContentSize(unbounded = true)
                    )

                }


            )
        }
    }
}


//not range slider
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSlider(
    rating: Float?,
    onRatingChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 1f..5f,
    steps: Int = 7,
    //hmm
    onRatingReset:()-> Unit
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 18.dp )

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Minimum Rating",
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )
            //Slider doesn't accept nullable stuff ..
            //?.let strikes again
            Row(

            ) {
                Text(
                    text = rating?.let { "${it}" } ?: "Any",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                //what about.. animated visibility for a button to reset back to null ?? ooh
                AnimatedVisibility(
                    visible=rating != null
                ) {
                    IconButton(
                        onClick = onRatingReset,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            tint=Color.Gray,
                            contentDescription = "Clear minimum rating filter for products",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

            }
        }
            Slider(
                value = rating?:1f,
                onValueChange = onRatingChange,
                valueRange = valueRange,
                steps = steps,
                modifier = modifier,
                thumb = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lemon),
                        contentDescription = "Price range thumb",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(18.dp)
                            .wrapContentSize(unbounded = true)
                    )
                },
                colors = SliderDefaults.colors(
                    activeTrackColor = Color.Black,
                    inactiveTrackColor = Color.Black.copy(0.7f),
                    activeTickColor = Color.Transparent,
                    inactiveTickColor = Color.Transparent
                )
            )
        }

}


//RE USABLE! chip -> can be either display only or clickable pass lambda
@Composable
fun CustomChip(
    modifier: Modifier = Modifier,
    label: String,
    selectable: Boolean = false, //by default just display
    selected: Boolean = false, // for the fitlers ones , is it selected already by deafult already ?
    icon: ImageVector? = null,
    //TINT for the icon
    iconTint:Color= if(selected)Color.Yellow else Color.Gray,

    //deisgn
    ContainerColor: Color = white, //for both ?
    LabelColor :Color = Color.Black, // also for both.
    SelectedContainerColor: Color = white,
    SelectedIconColor : Color = white,
    SelectedLabelColor: Color = Color.Black,

    onClick: () -> Unit = {}
){

    //colros for category chips -> assitive only
    val categoryColors = mapOf(
        "cold" to Color(0xFF90CAF9),
        "hot" to Color(0xFFEF9A9A),
        "fresh" to Color(0xFFA5D6A7),
        "tropical" to Color(0xFFFFCC80)
    )


    if (selectable){
        //selectable -> change color = onClick
        FilterChip(
            selected = selected,
            onClick = onClick,
            label= {Text(
                text=label,
                fontWeight = FontWeight.SemiBold
            )},


            //NO BORDER GOOGLE NO
            border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = selected,
                borderColor = Color.Transparent,
                selectedBorderColor = Color.Transparent
            ),

            leadingIcon = if (icon!=null){
                {
                    Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp),tint=iconTint )
                }
            }else null,

            shape = RoundedCornerShape(50.dp),
            colors= FilterChipDefaults.filterChipColors(
                containerColor = ContainerColor,
                labelColor = LabelColor, //UNSELECTED TEXT!
                selectedContainerColor = SelectedContainerColor,
                selectedLabelColor = SelectedLabelColor,
                selectedLeadingIconColor = SelectedIconColor,
            ),
            modifier = modifier

        )
    }else {
        //Non selectable just for display
        //also has the same onClick btw

        //color matches map ?
        val containerColor = categoryColors[label.lowercase()]
            ?: white

        AssistChip(
            onClick = onClick,
            label = { Text(label)},
            leadingIcon = if (icon != null){
                {
                    Icon(imageVector = icon,
                        contentDescription = null,
                        modifier= Modifier.size(16.dp)
                    )
                }
            }else null,
            shape=RoundedCornerShape(50.dp),
            colors= AssistChipDefaults.assistChipColors(
                containerColor = containerColor,
                labelColor =LabelColor
            ),
            border=null,
            modifier = Modifier
        )
    }
}

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
            .background(Color.Black.copy(0.15f)),
    ){

        var showFullscreenViewer by remember { mutableStateOf(false) }
        var initialPage by remember { mutableStateOf(0) }

        //it has a zoomFraction variable.. null if not zoomed so. defaultting to 0f pls
        HorizontalPager(
            state=pagerState,
            modifier= Modifier.fillMaxSize(),
            beyondViewportPageCount = 1
        ) { page ->

            AsyncImage(
                model=productImages[page], // page is the index bruhh
                contentDescription = "Product image number $page",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        initialPage = page
                        showFullscreenViewer = true
                    }

            )
        }

        //D O T
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
        //Dialog for stupid image zooming
        if (showFullscreenViewer){
            Dialog(
                onDismissRequest = {showFullscreenViewer=false },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false
                )
            ){
                Box(
                    Modifier.fillMaxSize()
                        .background(Color.Black.copy(0.3f))
                ){
                    val fullScreenPagerState = rememberPagerState (
                        initialPage= initialPage,
                        pageCount = { productImages.size}
                    )
                    var activeZoomableState by remember {   mutableStateOf<ZoomableState?>(null) }
                    val pagerScrollEnabled by remember {
                        derivedStateOf { (activeZoomableState?.zoomFraction ?: 0f) ==0f }
                    }

                    HorizontalPager(
                        state=fullScreenPagerState,
                        userScrollEnabled = pagerScrollEnabled,
                        modifier=Modifier.fillMaxSize(),
                        beyondViewportPageCount = 1

                    ) { page ->
                        val zoomableState = rememberZoomableState(
                            //animation when max zoo mreached ?
                            zoomSpec = ZoomSpec(
                                maxZoomFactor = 2f,
                                overzoomEffect = OverzoomEffect.RubberBanding
                            )
                        )
                        //-> the generic zoom one ?
                        //-> just tracs scale offset.. DOES NOT know iamge or otherstuff jsut math

                        val imageState = rememberZoomableImageState(zoomableState)
                        //->adds image specific stuff to it  like min max bounds states..

                        LaunchedEffect(fullScreenPagerState.settledPage) {
                            if(fullScreenPagerState.settledPage == page){
                                activeZoomableState = zoomableState
                            }else {
                                zoomableState.resetZoom(withAnimation = false)
                            }
                        }


                        @OptIn(ExperimentalTelephotoApi::class)
                        ZoomableAsyncImage(
                            model = productImages[page],
                            contentDescription = "Product image number $page",
                            state = imageState,
                            contentScale = ContentScale.Fit,
                            onClick = { clickedAt ->
                                val isZoomed = (zoomableState.zoomFraction ?: 0f) >0f
                                if (!isZoomed){
                                    val imageBounds = with (zoomableState.coordinateSystem){
                                        contentBounds.rectIn(CoordinateSpace.Viewport)
                                    }
                                    if (!imageBounds.contains(clickedAt)){
                                        showFullscreenViewer =false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxSize(),

                            )

                    }
                }
            }
        }
    }


}