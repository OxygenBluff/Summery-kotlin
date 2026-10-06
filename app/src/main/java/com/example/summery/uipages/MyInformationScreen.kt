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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.summery.AppToast
import com.example.summery.CustomTextField
import com.example.summery.ScreenTransition
import com.example.summery.ui.components.yellow

@Composable
fun MyInformationScreen(
navController: NavController,
viewModel: MyInformationViewModel,
){
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var isToastError by remember { mutableStateOf(false) }

    //get it
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.errorMessage){
        val message= uiState.errorMessage
        if(!message.isNullOrEmpty()){
            toastMessage=message
            isToastError = true
            showToast = true
            viewModel.clearErrorMessage()
        }
    }

    LaunchedEffect(uiState.statusMessage) {
        val message=uiState.statusMessage
        if(!message.isNullOrEmpty()){
            toastMessage=message
            isToastError = false
            showToast = true
            viewModel.clearStatusMessage()
        }
    }


    Surface(
        modifier = Modifier
            .fillMaxSize(),

        color = Color(0xFFFFF89F)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            ScreenTransition(
                isDataLoading = false,
                delayMillis = 0,
                withSpinner = false
            ) {


                LazyColumn(
                    modifier = Modifier
                        .statusBarsPadding(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 50.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                    }
                            )
                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "Edit Account information",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontFamily = FontFamily.SansSerif,
                            )
                        }

                    }//
                    item{
                        Text(
                            text = "Manage the name and primary email address connected to your account. " +
                                    "Delivery address changes will not be reflected across your past orders",
                            style= MaterialTheme.typography.bodyMedium
                        )
                    }

                    //the forms ! separate better ?
                    //1-First name
                    item{
                        Column(
                            modifier=Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "First name:",
                                style= MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color=Color.Black
                            )

                            CustomTextField(
                                value = uiState.firstName,
                                //just for controlling the display tho ??
                                onValueChange = { newValue ->
                                    viewModel.onFirstNameChanged(newValue)
                                },
                                // HUUUH ? i didn't know this..
                                //modifier = TODO(),
                                isPasswordField = false,
                                isSearchField = false,
                                keyboardOptions = KeyboardOptions.Default,
                                maxLines = 1,
                                placeholder = "your first name",
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            viewModel.onFirstNameChanged("")
                                        },
                                        modifier = Modifier
                                            .size(24.dp)
                                            .minimumInteractiveComponentSize()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Close,
                                            tint = Color.Gray,
                                            contentDescription = null
                                        )
                                    }

                                }

                            )
                        }
                    }

                    //2-last name
                    item{
                        Column(
                            modifier=Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Last name:",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            CustomTextField(
                                value = uiState.lastName,
                                //just for controlling the display tho ??
                                onValueChange = { newValue ->
                                    viewModel.onLastNameChanged(newValue)
                                },
                                // HUUUH ? i didn't know this..
                                //modifier = TODO(),
                                isPasswordField = false,
                                isSearchField = false,
                                keyboardOptions = KeyboardOptions.Default,
                                maxLines = 1,
                                placeholder = "your last name",
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            viewModel.onLastNameChanged("")
                                        },
                                        modifier = Modifier
                                            .size(24.dp)
                                            .minimumInteractiveComponentSize()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Close,
                                            tint = Color.Gray,
                                            contentDescription = null
                                        )
                                    }

                                }

                            )
                        }
                    }

                    //3-email
                    item{
                        Column(
                            modifier=Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Email:",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            CustomTextField(
                                value = uiState.email,
                                //just for controlling the display tho ??
                                onValueChange = { newValue ->
                                    viewModel.onEmailChanged(newValue)
                                },
                                // HUUUH ? i didn't know this..
                                //modifier = TODO(),
                                isPasswordField = false,
                                isSearchField = false,
                                keyboardOptions = KeyboardOptions.Default,
                                maxLines = 1,
                                placeholder = "your Email",
                                //TODO add enabled = !uiState.isLoading ?
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            viewModel.onEmailChanged("")
                                        },
                                        modifier = Modifier
                                            .size(24.dp)
                                            .minimumInteractiveComponentSize()
                                        //stupid hidden padding
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Close,
                                            tint = Color.Gray,
                                            contentDescription = null
                                        )
                                    }

                                }
                            )
                        }
                    }

                    //4-button
                    item{
                        val fieldsList = listOf(uiState.firstName,uiState.lastName,uiState.email)
                        Button(
                            modifier = Modifier
                                .padding(top=10.dp)
                                .fillMaxWidth()
                                .height(45.dp),


                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color(0xFFE8D755),
                                disabledContainerColor = Color(0xFF3A3939)
                            ),
                            enabled = !uiState.isLoading && !fieldsList.any{it.isNullOrBlank()}  ,

                            onClick = {
                                viewModel.saveChanges()
                            }
                        ) {
                            if (uiState.isLoading) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = Color(0xFFE8D755),
                                        modifier = Modifier
                                            .size(32.dp),

                                        strokeWidth = 4.5.dp
                                    )
                                }
                            } else {
                                Text(
                                    text = "Save changes",
                                    style = TextStyle(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.5.sp,
                                        fontSize = 16.sp
                                    )
                                )
                            }

                        }
                    }//

                }
            }
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
        }//end of box
    }
}