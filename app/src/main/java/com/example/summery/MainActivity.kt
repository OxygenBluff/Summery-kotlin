package com.example.summery


import ProductDetailScreen
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.summery.data.AuthRepository
import com.example.summery.local.EncryptedTokenManager
import com.example.summery.navigation.HomeDestination
import com.example.summery.navigation.LoginDestination
import com.example.summery.navigation.ProductDetailsDestination
import com.example.summery.navigation.SignupDestination
import com.example.summery.navigation.bottomNavBar
import com.example.summery.network.ProductResponseDTO
import com.example.summery.network.RetrofitInstance
import com.example.summery.ui.theme.SummeryTheme
import com.example.summery.uipages.AuthViewModel
import com.example.summery.uipages.HomeScreen
import com.example.summery.uipages.HomeViewModel
import com.example.summery.uipages.LoginScreen
import com.example.summery.uipages.SignupScreen
import com.google.gson.Gson
import kotlin.reflect.typeOf


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //cache context initlization //TODO write down
        enableEdgeToEdge()

        //HERE is where they're instanciated only once
        val tokenManager = EncryptedTokenManager(applicationContext)

        RetrofitInstance.initCache(applicationContext)
    //TODO THE ORDER MATTERS OMG
        RetrofitInstance.tokenManager = tokenManager
        //IT NEEDS OT BE HANDED OVER TO RETROFIT FIRST !!!!! bruh what is this

        //now safe, wtf..
        val authRepository = AuthRepository(RetrofitInstance.api,tokenManager)
        val authViewModel = AuthViewModel(authRepository,tokenManager)
        

        setContent {
            SummeryTheme {

                //nav controller now we're evolving !!
                val navController = rememberNavController()


                val navBackStackEntry by navController.currentBackStackEntryAsState()
                //TODO this gets the current back stack entry as a RERACTIVE state object ??


                val currentDestination = navBackStackEntry?.destination
                //what is has currently the route
                Scaffold(

                    bottomBar = {
                        //not every screen..
                        val hideNavBar = currentDestination?.route?.contains("SignupDestination") == true

                        if(!hideNavBar) {
                            bottomNavBar(navController)
                        }
                    }
                ) { innerPadding ->
                    //padding really wants to be here..
                    val screenModifier = Modifier.padding(innerPadding)
                    //CALCUALTED FOR THE BOTTOM NAV BAR AUTOMTIACALLY ?

                    //hell nah google what did you apply to defauly navhost..
                    val duration= 230

                    //IAMGE GLIDING
                    //1-> needs to wrap NavHost in a shared transition layout
                    //2-> passing the scope + animated visibility scope

                        NavHost(
                            navController = navController,
                            startDestination = SignupDestination,
                            modifier = Modifier.fillMaxSize(),

                            enterTransition = { EnterTransition.None },
                            exitTransition = { ExitTransition.None },
                            popEnterTransition = { EnterTransition.None },
                            popExitTransition = { ExitTransition.None },

                        ) {
                            composable<SignupDestination> { backStackEntry ->
                                SignupScreen(
                                    onNavigateToLogin = {
                                        navController.navigate(LoginDestination)
                                    },
                                    tokenManager = tokenManager
                                ) {
                                    navController.navigate(HomeDestination) {
                                        //+ clearing backstack
                                        popUpTo(SignupDestination) { inclusive = true }
                                        launchSingleTop = true
                                    }

                                }
                            }

                            composable<LoginDestination> {
                                LoginScreen(
                                    onNavigateToHomeScreen = {
                                        navController.navigate(HomeDestination) {
                                            //ALSO clear the stack.. why tf would you be allowed
                                            //to go back to login/signup after loggin in ? no

                                            //popUpTo(SignupDestination){
                                            // = clears all the stack UP to a target huh
                                            //   inclusive=true
                                            //}
                                            launchSingleTop =
                                                true//-> never multiple copes of a screen if clicked twice ?
                                        }
                                        //will add onLogout ->
                                        //tokenManager.clearTokens()
                                        //and popUpTo(Home destination))

                                    },
                                    onNavigateToSignupScreen = {
                                        navController.navigate(SignupDestination)
                                    },
                                    authViewModel = authViewModel,
                                    tokenManager = tokenManager
                                )
                            }

                            composable<HomeDestination>(
                            ) {
                                val apiService = RetrofitInstance.api

                                //now .. a factory ??
                                val homeViewModel: HomeViewModel = viewModel (
                                    factory = object: ViewModelProvider.Factory{
                                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                            return HomeViewModel(apiService) as T
                                        }
                                    }
                                )
                                //wtf... override the create omg..

                                HomeScreen(
                                    homeViewModel = homeViewModel,
                                    //modifier=screenModifier,
                                    onProductClick = { product ->
                                        navController.navigate(
                                            ProductDetailsDestination(
                                                product = product,

                                            )
                                        )
                                    }
                                )
                            }

                            composable<ProductDetailsDestination>(
                                typeMap = mapOf(typeOf<ProductResponseDTO>() to createCustomNavType<ProductResponseDTO>())                                //TODO WHAT .. cannot pass an object neeed a mapper ?
                            ){ backStackEntry ->
                                //BUT need the id argument so from the ROUTE ??
                                val detailRoute = backStackEntry.toRoute<ProductDetailsDestination>()
                                ProductDetailScreen(
                                    //productId = detailRoute.productId,
                                    navController = navController,
                                    product= detailRoute.product

                                )
                            }
                        }


                }
            }
        }
    }
}

inline fun <reified T : Any> createCustomNavType(): NavType<T> {
    val gson = Gson()

    return object : NavType<T>(isNullableAllowed = false) {

        override fun put(bundle: Bundle, key: String, value: T) {
            // Convert object to JSON string using Gson
            bundle.putString(key, gson.toJson(value))
        }

        override fun get(bundle: Bundle, key: String): T? {
            // Read JSON string back into object using Gson
            return bundle.getString(key)?.let { gson.fromJson(it, T::class.java) }
        }

        override fun parseValue(value: String): T {
            return gson.fromJson(value, T::class.java)
        }

        override fun serializeAsValue(value: T): String {
            // Protect special symbols in image URLs
            return Uri.encode(gson.toJson(value))
        }
    }
}



    //okay why collectAsState..
    //-> to convert StateFlow into sth compose can WATCH!
    //it cannot directly watch flowState huh..

    //CANNOT CAST IT HERE YET! because..idk what state it is lol

