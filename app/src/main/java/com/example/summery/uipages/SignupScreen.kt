package com.example.summery.uipages

import RegisterRequestDTO
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.summery.CustomTextField
import com.example.summery.R
import com.example.summery.ScreenTransition
import com.example.summery.local.EncryptedTokenManager
import com.example.summery.network.RetrofitInstance
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun SignupScreen(
    modifier: Modifier = Modifier,
    tokenManager: EncryptedTokenManager,
    onNavigateToLogin: () -> Unit, //passed in from the main actiivty
    onNavigateToHomeScreen: () -> Unit,
) {
    //reactive Ui remeber ? (no pun intended)
    //mutableStateOf = observable ! the UI updates automatically
    //i already remmeber this but nice to remind
    //remember = CACHE! screen recomposes -> doesn't reset !

    var prenom by remember { mutableStateOf("") }
    var nom by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("") }

    //Red vs sucess statusMesssage..
    var isSuccessMessage by remember { mutableStateOf(false) }


    //No viewModel for this screen
    var navigateToHome by remember { mutableStateOf(false) }



    val scope = rememberCoroutineScope()

    //to home screen if token there
    LaunchedEffect(navigateToHome) {
        if(navigateToHome){
            delay(600)
            onNavigateToHomeScreen()
        }
    }


    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFFFFF89F)
        //yellow 0xFFFFF479
    ) {
        ScreenTransition(delayMillis = 50,
            withSpinner = false)
        {


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),

                //.verticalScroll(rememberScrollState()),
            //keyboard blocking fixx !! scrolling that's it ?
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "I NEED MORE\nLEMONAAADE!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = Color.Black,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(modifier = Modifier.width(2.dp))

                Image(
                    painter = painterResource(id = R.drawable.juice_icon),
                    contentDescription = "Juice Icon",
                    modifier = Modifier.size(92.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(Color.Black)
                )


            }
            Spacer(modifier = Modifier.height(28.dp))

            // Input 1: First Name
            CustomTextField(
                value = prenom,
                onValueChange = { prenom = it },
                placeholder = "First Name",

                )
            Spacer(modifier = Modifier.height(8.dp))

            // Input 2: Last Name
            CustomTextField(
                value = nom,
                onValueChange = { nom = it },
                placeholder = "Last Name"
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Input 3: Email
            CustomTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "E-mail"
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Input 4: Password
            CustomTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                isPasswordField = true

            )

            Spacer(modifier = Modifier.height(8.dp))

            // Input 5: Password confirm
            CustomTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = "Confirm Password",
                isPasswordField = true

            )
            Spacer(modifier = Modifier.height(24.dp))


            // Status
            if (statusMessage.isNotEmpty()) {
                Text(
                    text = statusMessage,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Medium
                    ),
                    color = if (!isSuccessMessage) Color(0xFFE35555) else Color.Black.copy(0.8f)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }


            // Register Button
            Button(
                onClick = {

                    statusMessage = ""

                    //checkss
                    //password min 6 ?
                    if (password.length < 6) {
                        statusMessage = "Password must be at least 6 characters Long"
                        return@Button
                        isSuccessMessage = false
                        //HUH wth is that ?
                        //->Stops execution, doesnt' do the coroutine call HMM nice

                        //called "Labeled return" !!
                        //-> return + @ + NAME OF THE FUNCTION!
                        //so return @ Button here!
                        //just exists this sepfcific onCLick code
                    }

                    if (password != confirmPassword) {
                        statusMessage = "Password matching Error!"
                        return@Button
                        isSuccessMessage = false
                    }

                    //all good THEN loading
                    isLoading = true


                    scope.launch {
                        try {
                            val requestPayload = RegisterRequestDTO(
                                prenom = prenom,
                                nom = nom,
                                email = email,
                                password = password
                            )

                            val response = RetrofitInstance.api.register(requestPayload)
                            isLoading = false
                            //imeddiately false after assigning the response var huh..

                            if (response.isSuccessful && response.body() != null) {
                                statusMessage = "Account created :) Proceeding... "
                                isSuccessMessage = true

                                // TODO: Save tokens inside Encrypted data store
                                //well sharedPrefernces easier..
                                val authData = response.body()!!
                                tokenManager.saveTokens(
                                    accessToken = authData.accessToken,
                                    refreshToken = authData.refreshToken
                                )
                                navigateToHome=true

                            } else {
                                //MAGIC part ! made it reusable boy was it complicated..

                                //statusMessage = "Registration Failed: ${response.code()}"
                                statusMessage = RetrofitInstance.parseErrorMessage(response)
                                isSuccessMessage = false
                            }
                        } catch (e: Exception) {
                            isLoading = false
                            statusMessage =
                                "Seems like we cannot access the server at this moment.."
                            isSuccessMessage = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color(0xFFE8D755),
                    disabledContainerColor = Color(0xFF3A3939)

                ),
                enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty()
                        && nom.isNotEmpty() && prenom.isNotEmpty() && confirmPassword.isNotEmpty()
            ) {
                // SPINNER!!
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFE8D755),
                            modifier = Modifier
                                .size(32.dp),

                            strokeWidth = 4.5.dp //YES I CAN CHANGE IT YES
                        )
                    }
                } else {
                    Text(
                        text = "Sign up!",
                        style = TextStyle(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp,
                            fontSize = 16.sp
                        )
                    )
                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation
            TextButton(onClick = onNavigateToLogin) {
                Text(
                    text = "already have an account ? Log in",
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }
        }



        }


    }
}


