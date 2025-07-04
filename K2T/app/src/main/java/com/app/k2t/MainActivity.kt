package com.app.k2t

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.k2t.ui.presentation.screen.admin.AdminNavigation
import com.app.k2t.ui.presentation.screen.chef.ChefNavigation
import com.app.k2t.ui.presentation.screen.table.TableNavigation
import com.app.k2t.ui.presentation.screen.userauth.LoginScreen
import com.app.k2t.ui.presentation.screen.userauth.UserRegisterScreen
import com.app.k2t.ui.theme.K2TTheme
import com.app.k2t.ui.presentation.viewmodel.UserViewModel // Assuming your UserViewModel is here
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
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() } // Lazy init
    private val userViewModel: UserViewModel by inject() // Koin injection

    private lateinit var networkObserver: NetworkConnectivityObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkObserver = NetworkConnectivityObserver(applicationContext)
        enableEdgeToEdge()

        setContent {
            K2TTheme {
                val userViewModel: UserViewModel = this@MainActivity.userViewModel
                val userState by userViewModel.userState.collectAsState()
                val navController = rememberNavController()
                var initialRoute by remember { mutableStateOf<String?>(null) }

                // On relaunch, check if user is logged in and navigate to role_router if so
                LaunchedEffect(userState) {
                    if (userState != null && userState?.role != null) {
                        navController.navigate("role_router") {
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }

                Scaffold { padding ->


                    NavHost(
                        navController = navController,
                        startDestination = "login",
                    ) {
                        composable("login") {
                            LoginScreen(
                                navController = navController,
                                onSuccessfulLogin = {
                                    navController.navigate("role_router") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("signup") {
                            UserRegisterScreen(
                                navController = navController
                            )
                        }
                        composable("role_router") {
                            RoleRouter(userViewModel = userViewModel, navController = navController)
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
                navController = navController,
                onSuccessfulLogin = TODO()
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
    val isLoading = userState == null
    if (isLoading) {
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
                Text("Unknown role or not logged in.")
            }
        }
    }
}
