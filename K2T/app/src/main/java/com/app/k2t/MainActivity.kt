package com.app.k2t

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.k2t.ui.presentation.screen.admin.AdminNavigation
import com.app.k2t.ui.presentation.screen.chef.ChefNavigation
import com.app.k2t.ui.presentation.screen.table.TableNavigation
import com.app.k2t.ui.presentation.screen.userauth.LoginScreen
import com.app.k2t.ui.presentation.screen.userauth.UserRegisterScreen
import com.app.k2t.ui.presentation.viewmodel.NavigationEvent
import com.app.k2t.ui.theme.K2TTheme
import com.app.k2t.ui.presentation.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.android.ext.android.inject

// Sealed class to represent network statusCode (can be in a separate file)
sealed class NetworkStatus {
    object Available : NetworkStatus()
    object Unavailable : NetworkStatus()
}

// Network observer (can be in a separate file)
class NetworkConnectivityObserver(context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun observe(): Flow<NetworkStatus> {
        return callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    trySend(NetworkStatus.Available)
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    trySend(NetworkStatus.Unavailable)
                }

                override fun onUnavailable() { // For older APIs
                    super.onUnavailable()
                    trySend(NetworkStatus.Unavailable)
                }
            }

            // Get current network statusCode
            val currentNetwork = connectivityManager.activeNetwork
            if (currentNetwork == null) {
                trySend(NetworkStatus.Unavailable)
            } else {
                val capabilities = connectivityManager.getNetworkCapabilities(currentNetwork)
                if (capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                ) {
                    trySend(NetworkStatus.Available)
                } else {
                    trySend(NetworkStatus.Unavailable)
                }
            }
            // Register callback
            connectivityManager.registerDefaultNetworkCallback(callback)

            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged()
    }
}


// --- Main Activity ---
class MainActivity : ComponentActivity() {

    private val userViewModel: UserViewModel by inject()
    private lateinit var networkConnectivityObserver: NetworkConnectivityObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        networkConnectivityObserver = NetworkConnectivityObserver(applicationContext)

        setContent {
            K2TTheme {
                val userViewModel: UserViewModel = this@MainActivity.userViewModel
                val userState by userViewModel.userState.collectAsState()
                val isLoading by userViewModel.isLoading.collectAsState()
                val navController = rememberNavController()

                // Handles one-time navigation events from the ViewModel, like after login.
                LaunchedEffect(Unit) {
                    userViewModel.navigationEvents.collect { event ->
                        when (event) {
                            is NavigationEvent.NavigateToRoleRouter -> {
                                navController.navigate("role_router") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        }
                    }
                }

                // Handles initial navigation and state changes
                LaunchedEffect(userState, isLoading) {
                    if (!isLoading) {
                        val currentRoute = navController.currentDestination?.route
                        when {
                            userState != null && currentRoute != "role_router" -> {
                                navController.navigate("role_router") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                            userState == null && currentRoute !in listOf("login", "signup") -> {
                                navController.navigate("login") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        }
                    }
                }

                Scaffold{ paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = "splash",
                        enterTransition = {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(300, easing = FastOutLinearInEasing)
                            ) + fadeIn(animationSpec = tween(300))
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(300, easing = LinearOutSlowInEasing)
                            ) + fadeOut(animationSpec = tween(300))
                        }
                    ) {
                        // Enhanced splash screen with app branding
                        composable("splash") {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primary,
                                                MaterialTheme.colorScheme.primaryContainer
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    // App Logo/Icon
                                    Card(
                                        modifier = Modifier.size(120.dp),
                                        shape = RoundedCornerShape(30.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        ),
                                        elevation = CardDefaults.cardElevation(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(
                                                    painter = painterResource(R.drawable.baseline_fastfood_24),
                                                    contentDescription = "App Logo",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(40.dp),
                                                )
                                                Text(
                                                    text = "K2T",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    Text(
                                        text = "Kitchen to Table",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.Medium
                                    )

                                    Spacer(modifier = Modifier.height(40.dp))

                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 3.dp
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "Loading...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }

                        composable(
                            "login",
                            enterTransition = {
                                when (initialState.destination.route) {
                                    "signup" -> slideIntoContainer(
                                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                        animationSpec = tween(400)
                                    )
                                    else -> slideIntoContainer(
                                        towards = AnimatedContentTransitionScope.SlideDirection.Up,
                                        animationSpec = tween(400)
                                    )
                                }
                            },
                            exitTransition = {
                                when (targetState.destination.route) {
                                    "signup" -> slideOutOfContainer(
                                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                        animationSpec = tween(400)
                                    )
                                    else -> slideOutOfContainer(
                                        towards = AnimatedContentTransitionScope.SlideDirection.Down,
                                        animationSpec = tween(400)
                                    )
                                }
                            }
                        ) {
                            LoginScreen(
                                navController = navController,
                                userViewModel = userViewModel
                            )
                        }

                        composable(
                            "signup",
                            enterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(400)
                                )
                            },
                            exitTransition = {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(400)
                                )
                            }
                        ) {
                            UserRegisterScreen(
                                navController = navController,
                                userViewModel = userViewModel
                            )
                        }

                        composable(
                            "role_router",
                            enterTransition = {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                                    animationSpec = tween(400)
                                ) + fadeIn(animationSpec = tween(400))
                            }
                        ) {
                            RoleRouter(
                                userViewModel = userViewModel,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}


// --- Composable Helper Functions ---
@Composable
fun AuthNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(
                navController = navController
            )
        }
        composable("signup") {
            UserRegisterScreen(
                navController = navController,
            )
        }
    }
}

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun NetworkErrorScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No Network Connection.\nPlease check your internet and try again.",
            modifier = Modifier.padding(16.dp)
            // Add more styling as needed
        )
        // Optionally, add a retry button here
    }
}

@Composable
fun WaiterNavigation(modifier: Modifier = Modifier) {
    // Placeholder for waiter navigation
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Waiter Navigation")
    }
}

@Composable
fun RoleRouter(
    userViewModel: UserViewModel,
    navController: androidx.navigation.NavHostController
) {
    val userState by userViewModel.userState.collectAsState()

    // RoleRouter is now only responsible for displaying the UI for the current user's role.
    // Navigation is handled centrally in MainActivity.
    if (userState == null) {
        // If user becomes null (e.g., sign out), show a loading indicator
        // while the main LaunchedEffect navigates back to the login screen.
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        when (userState?.role) {
            "admin" -> AdminNavigation()
            "chef" -> ChefNavigation()
            "waiter" -> WaiterNavigation()
            "table" -> TableNavigation()
            else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                // This case should rarely be seen as the userState check handles it.
                Text("Unknown role or not logged in.")
            }
        }
    }
}
