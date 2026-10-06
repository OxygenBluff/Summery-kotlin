package com.example.summery


import ProductDetailScreen
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.summery.data.AccountRepository
import com.example.summery.data.AuthRepository
import com.example.summery.data.OrderRepository
import com.example.summery.local.EncryptedTokenManager
import com.example.summery.navigation.AccountDestination
import com.example.summery.navigation.BranchDestination
import com.example.summery.navigation.BranchesDestination
import com.example.summery.navigation.CartDestionation
import com.example.summery.navigation.HomeDestination
import com.example.summery.navigation.LoginDestination
import com.example.summery.navigation.MyAddressesScreen
import com.example.summery.navigation.MyInformationScreen
import com.example.summery.navigation.MyOrdersScreen
import com.example.summery.navigation.OrderDestination
import com.example.summery.navigation.ProductDetailsDestination
import com.example.summery.navigation.SignupDestination
import com.example.summery.navigation.bottomNavBar
import com.example.summery.network.ProductResponseDTO
import com.example.summery.network.RetrofitInstance
import com.example.summery.ui.theme.SummeryTheme
import com.example.summery.uipages.AccountScreen
import com.example.summery.uipages.AccountViewModel
import com.example.summery.uipages.AuthViewModel
import com.example.summery.uipages.BranchScreen
import com.example.summery.uipages.BranchViewModel
import com.example.summery.uipages.BranchesScreen
import com.example.summery.uipages.BranchesViewModel
import com.example.summery.uipages.CartScreen
import com.example.summery.uipages.CartViewModel
import com.example.summery.uipages.HomeScreen
import com.example.summery.uipages.HomeViewModel
import com.example.summery.uipages.LoginScreen
import com.example.summery.uipages.MyAddressesScreen
import com.example.summery.uipages.MyAddressesViewModel
import com.example.summery.uipages.MyInformationScreen
import com.example.summery.uipages.MyInformationViewModel
import com.example.summery.uipages.OrderHistoryScreen
import com.example.summery.uipages.OrderHistoryViewModel
import com.example.summery.uipages.OrderScreen
import com.example.summery.uipages.OrderViewModel
import com.example.summery.uipages.ProductDetailsViewModel
import com.example.summery.uipages.SignupScreen
import com.google.gson.Gson
import kotlin.reflect.typeOf


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
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
            val token = remember {tokenManager.getAccessToken() }
            val startDestination = if(!token.isNullOrEmpty()){
                HomeDestination
            }else{
                LoginDestination
            }
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
                        val hideNavBar = currentDestination?.route?.contains("SignupDestination") == true || currentDestination?.route?.contains("LoginDestination") == true

                        if(!hideNavBar) {
                            bottomNavBar(navController)
                        }
                    }
                ) { innerPadding ->
                    //padding really wants to be here..
                    val screenModifier = Modifier.padding(innerPadding)

                    //log in again refresh expired
                    //+ CLEAR USER DATA
                    LaunchedEffect(Unit) {
                        tokenManager.loggedOutEvent.collect {
                            //1- user data
                            AccountRepository.clearUserState()
                            //2-Navigate
                            navController.navigate(LoginDestination){
                                popUpTo(0){inclusive=true}
                                launchSingleTop=true
                                /// ALL GONE
                            }
                        }
                    }
                        NavHost(
                            navController = navController,
                            startDestination = startDestination,
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
                                    onProductClick = { productId ->
                                        navController.navigate(
                                            ProductDetailsDestination(
                                                productId = productId
                                            )
                                        )
                                    }
                                )
                            }

                            composable<ProductDetailsDestination>(
                                typeMap = mapOf(typeOf<ProductResponseDTO>() to createCustomNavType<ProductResponseDTO>())                                //TODO WHAT .. cannot pass an object neeed a mapper ?
                            ){ backStackEntry ->

                                //viewmolde again.
                                val apiService = RetrofitInstance.api

                                val productDetailsViewModel: ProductDetailsViewModel = viewModel(
                                    factory = object: ViewModelProvider.Factory{
                                        override fun <T: ViewModel>  create(modelClass: Class<T>): T {
                                            return ProductDetailsViewModel(apiService) as T
                                        }
                                    }
                                )

                                //BUT need the id argument so from the ROUTE ??
                                val detailRoute = backStackEntry.toRoute<ProductDetailsDestination>()
                                ProductDetailScreen(
                                    //productId = detailRoute.productId,
                                    navController = navController,
                                    productId= detailRoute.productId,
                                    viewModel = productDetailsViewModel
                                ){sellerId -> //onBranchClick(sellerId)
                                    navController.navigate(BranchDestination(branchId = sellerId))
                                }
                            }

                            //finally .. cart destination
                            composable<CartDestionation>(){
                                val cartViewModel: CartViewModel = viewModel()

                                CartScreen(
                                    CartViewModel = cartViewModel,
                                    navController = navController,
                                    onNavigateToHome = {
                                        navController.navigate(HomeDestination) {
                                            popUpTo(HomeDestination) { inclusive = true }// sure why not
                                            launchSingleTop = true
                                        }
                                    },
                                    onNavigateToOrderScreen = {
                                        navController.navigate(OrderDestination){
                                            popUpTo(OrderDestination) { inclusive=true}
                                            launchSingleTop= true
                                        }
                                    }
                                )
                            }

                            //checkout / order
                            composable <OrderDestination>(){
                                val orderViewModel: OrderViewModel = viewModel()


                                OrderScreen(
                                    orderViewModel = orderViewModel,
                                    navController = navController
                                )
                            }

                            //part 2! branches screen
                            composable < BranchesDestination>(){
                                val branchesViewModel = BranchesViewModel(
                                );


                                BranchesScreen(
                                    navController = navController,
                                    viewModel = branchesViewModel,
                                    onBranchClick = {id ->
                                        navController.navigate(
                                            BranchDestination(
                                                branchId = id
                                            )
                                        )
                                    },

                                )

                            }

                            //the branch
                            composable < BranchDestination>(){ navBackStackEntry->
                                val branchViewModel: BranchViewModel = viewModel()  // ← use viewModel() not BranchViewModel()


                                val detailRoute = navBackStackEntry.toRoute<BranchDestination>()

                                BranchScreen(
                                    navController = navController,
                                    viewModel = branchViewModel,
                                    branchId= detailRoute.branchId
                                )
                            }

                            composable<AccountDestination>(){
                                val accountRepo = AccountRepository
                                //shared singleton !!!!

                                val accountViewModel: AccountViewModel= viewModel(
                                    factory = object: ViewModelProvider.Factory{
                                        @Suppress("UNCHECKED_CAST")
                                        override fun <T: ViewModel>  create(modelClass: Class<T>): T {
                                            return AccountViewModel(
                                                repository=accountRepo,
                                                tokenManager = tokenManager
                                            ) as T
                                        }
                                    }
                                )

                                AccountScreen(
                                    navController=navController,
                                    viewModel = accountViewModel,
                                    onOrdersClicked = {
                                        navController.navigate(MyOrdersScreen)
                                    },
                                    onEditInfoClicked = {
                                        navController.navigate(MyInformationScreen)
                                    },
                                    onManageAddressesClicked = {
                                        navController.navigate(MyAddressesScreen)
                                    }
                                )
                            }

                            //its sub screens
                            composable<MyOrdersScreen>(){
                                //viewModel needs the SAME orderRepo instance..

                                val orderHistoryViewModel : OrderHistoryViewModel =viewModel ()
                                OrderHistoryScreen(
                                    navController =navController,
                                    viewModel = orderHistoryViewModel
                                )

                            }

                            composable<MyInformationScreen>(){
                                val accountRepo = AccountRepository

                                val MyInfoViewModel: MyInformationViewModel= viewModel(
                                    factory = object: ViewModelProvider.Factory{
                                        @Suppress("UNCHECKED_CAST")
                                        override fun <T: ViewModel>  create(modelClass: Class<T>): T {
                                            return MyInformationViewModel(
                                                repository = accountRepo,
                                                tokenManager = tokenManager,
                                            ) as T
                                        }
                                    }
                                )
                                MyInformationScreen(
                                    navController = navController,
                                    viewModel = MyInfoViewModel
                                )
                            }

                            composable<MyAddressesScreen> (){
                                val repo= OrderRepository

                                val AddressesViewModel: MyAddressesViewModel = viewModel (
                                    factory = object: ViewModelProvider.Factory{
                                        @Suppress("UNCHECKED_CAST")
                                        override fun <T: ViewModel> create (modelCass:Class<T>):T{
                                            return MyAddressesViewModel(
                                                repository = repo
                                            )as T
                                        }
                                    }
                                )
                                MyAddressesScreen(
                                    viewModel = AddressesViewModel,
                                    navController = navController
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


