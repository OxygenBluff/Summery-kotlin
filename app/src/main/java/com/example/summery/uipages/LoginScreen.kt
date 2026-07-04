package com.example.summery.uipages
import LoginRequestDTO
import androidx.compose.animation.core.Animatable
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.summery.CustomTextField
import com.example.summery.ScreenTransition
import com.example.summery.local.EncryptedTokenManager
import kotlinx.coroutines.delay


@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    tokenManager: EncryptedTokenManager,// to track tokens.. security risk without it
    onNavigateToHomeScreen: () -> Unit, //in main activity: Automatic after getting tokens from backend yh better
    onNavigateToSignupScreen: () -> Unit
){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    //that's it
    //OTHERS get it from viewModel now!

    val statusMessage by authViewModel.statusMessage.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    var scope = rememberCoroutineScope()

    //forced delay when switching to this screen what else to do..
    var isInitialLoading by remember { mutableStateOf(true) }
    //true for the SPINNER!

    val isSuccessMessage by authViewModel.isSuccessMessage.collectAsState()

    val navigateToHome by authViewModel.navigateToHome.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    //LaunchedEffect(Unit) {
    //    delay(320)
    //    isInitialLoading=false
    //}

    //sucess AND token exists -> to ViewModel.. again..
    LaunchedEffect(navigateToHome) {
        //home screen now..BY managed by the viewmodel
        if(navigateToHome){
            delay(600)
            onNavigateToHomeScreen()
            authViewModel.onNavigationHandled() //reset to false
        }
    }

    Surface(
        modifier= Modifier.fillMaxSize(),
        color = Color(0xFFFFF89F)
    ){
        //THE FIRST ONE
        //=>while loading inially -> fades in -> spinner sits there
        //WHEN timer hits -> IT IS TOGLLED TO FALSE
        //oh that's the exist
        ScreenTransition(){

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Log in to see the Lemons and More!",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                       // letterSpacing = (-0.5).sp,
                        lineHeight = 36.sp,
                        color = Color.Black,
                        fontSize = 32.sp,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(2.dp))

                    //Image here
                    //TODO

                }
                Spacer(modifier = Modifier.height(28.dp))

                //Forms!
                //1-Email
                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Email",
                    isPasswordField = false,
                )

                Spacer(Modifier.height(8.dp))

                //2-Password
                CustomTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Password",
                    isPasswordField = true
                )

                //error status mesages now..
                Spacer(modifier = Modifier.height(24.dp))

                if (statusMessage.isNotEmpty()) {
                    Text(
                        text = statusMessage,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Medium,
                        color  = if (!isSuccessMessage) Color(0xFFE35555) else Color.Black.copy(0.8f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                //buttons itself bruh
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color(0xFFE8D755),
                        disabledContainerColor = Color(0xFF3A3939)
                    ),
                    enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty() ,

                    onClick = {
                        //clear + checks eh what checks ? either right or wrong just set loading + network call
                        //OR check , without enabled and disabled state
                        //nvm keeping it consisent..

                        //all to authviewmodel now..
                        authViewModel.handleLogin(LoginRequestDTO(email,password))

                        //TODO a little bounce ?
                        //coroutineScope.launch {
                        //    scale.animateTo(0.9f, animationSpec = Spring())
                        //}

                    }
                ) {

                    if (isLoading) {
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
                            text = "Log in",
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    }

                }
                Spacer(modifier = Modifier.height(16.dp))

                // Navigation
                TextButton(onClick = onNavigateToSignupScreen) {
                    Text(
                        text = "or you can sign up here",
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

                //TODO forgot password ? have to send emails ? springBoot help..
            }



        }
    }
}