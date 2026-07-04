package com.example.summery

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay


//Google's default stuff is uh.. something..

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
            else  KeyboardOptions.Default
){
    BasicTextField(
        //basic text field =  the ENGINE
        //unlike textField pr Outlined one (have google's stupid design)
        //THIS has 0 design yes just functionnality !!
        //just the bliking cursor is here btw
        value = value,
        onValueChange = onValueChange,
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
                    .height(46.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        color=Color(0xFFF6F6F2),

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
                            color = Color.Black.copy(alpha = 0.3f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                    innerTextField()
                    //Google also gave this to make it acctually work now..
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

    val overllLoading by remember (isDataLoading){
        derivedStateOf { isInitailLoading ||isDataLoading }
    }
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
            enter = fadeIn(animationSpec = tween(120)),// true -> false behaviour
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
            enter=fadeIn(animationSpec = tween(durationMillis = 120,easing=LinearEasing))
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

//TODO a horizontal pager is next! u just pass in the list of Composables, scrolling and dots indicators underneath are done for you!
//TODO a super class that composables inherit from to make them like that animation when clicked ? the scale aniamte as float one

//Bottom sheet ?
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier= Modifier
){
    val isExpanded= rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    ModalBottomSheet(
        onDismissRequest= onDismissRequest,
        sheetState = isExpanded,
        modifier = modifier,

        contentWindowInsets = {WindowInsets.navigationBars},
        //protects content from the system's navbar mhm

        containerColor = Color(0xFFFDF489)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 20.dp)
        ){
            Text(
                text = "Select a filter",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text="Oomf!",
                modifier=Modifier.padding(top=8.dp),
                fontSize = 14.sp,
                color = Color.Black
            )
            //TODO FILTERS
        }

    }
}