package com.ukv.assignmentproject

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.messaging.FirebaseMessaging
import com.ukv.assignmentproject.database.AppDatabase
import com.ukv.assignmentproject.database.UserEntity
import com.ukv.assignmentproject.pdfviewer.PdfViewerScreen
import com.ukv.assignmentproject.screen.ItemListScreen
import com.ukv.assignmentproject.sign_in.GoogleUiClient
import com.ukv.assignmentproject.sign_in.ProfileScreen
import com.ukv.assignmentproject.sign_in.SignInScreen
import com.ukv.assignmentproject.sign_in.SignInViewModel
import com.ukv.assignmentproject.ui.theme.AssignmentProjectTheme
import com.ukv.assignmentproject.viewmodel.ItemViewModel
import com.ukv.assignmentproject.viewmodel.ItemViewModelFactory
import kotlinx.coroutines.launch
import java.net.URLEncoder

class MainActivity : ComponentActivity() {

    private lateinit var googleAuthUiClient: GoogleUiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseMessaging.getInstance().subscribeToTopic("all")
            .addOnCompleteListener {
                Log.d("FCM", "Subscribed to topic: all")
            }

        googleAuthUiClient = GoogleUiClient(this)

        // Get AppContainer from the Application class for DI
        val appContainer = (application as AssignmentProject).container

        setContent {
            AssignmentProjectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel = viewModel<SignInViewModel>()
                    val state by viewModel.state.collectAsStateWithLifecycle()

                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult(),
                        onResult = { result ->
                            if (result.resultCode == RESULT_OK && result.data != null) {
                                lifecycleScope.launch {
                                    val signInResult = googleAuthUiClient.signInWithIntent(
                                        intent = result.data!!
                                    )
                                    viewModel.onSignInResult(signInResult)
                                }
                            }
                        }
                    )

                    NavHost(navController = navController, startDestination = "sign_in") {

                        composable("sign_in") {
                            LaunchedEffect(Unit) {
                                if (googleAuthUiClient.getSignedInUser() != null) {
                                    navController.navigate("profile") {
                                        popUpTo("sign_in") { inclusive = true }
                                    }
                                }
                            }

                            LaunchedEffect(state.isSignInSuccessful) {
                                if (state.isSignInSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Sign in successful",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val user = googleAuthUiClient.getSignedInUser()
                                    if (user != null) {
                                        val db = AppDatabase.getDatabase(applicationContext)
                                        val userEntity = UserEntity(
                                            email = user.userId,
                                            displayName = user.username,
                                            profilePictureUrl = user.profilePictureUrl,
                                        )
                                        lifecycleScope.launch {
                                            db.userDao().insertUser(userEntity)
                                        }
                                    }

                                    navController.navigate("profile") {
                                        popUpTo("sign_in") { inclusive = true }
                                    }

                                    viewModel.resetState()
                                }
                            }

                            SignInScreen(
                                state = state,
                                onGoogleSignInClick = {
                                    val signInIntent = googleAuthUiClient.getSignInIntent()
                                    launcher.launch(signInIntent)
                                },
                                navController = navController
                            )
                        }

                        composable("profile") {
                            ProfileScreen(
                                userData = googleAuthUiClient.getSignedInUser(),
                                onSignOut = {
                                    lifecycleScope.launch {
                                        googleAuthUiClient.signOut()
                                        Toast.makeText(
                                            applicationContext,
                                            "Signed out",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.navigate("sign_in") {
                                            popUpTo("profile") { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }

                        composable(
                            "pdf_viewer?url={url}",
                            arguments = listOf(navArgument("url") { defaultValue = "" })
                        ) { backStackEntry ->
                            val url = backStackEntry.arguments?.getString("url") ?: ""
                            PdfViewerScreen(pdfUrl = url, navController = navController)
                        }

                        composable("products") {
                            // Use the manually injected ItemViewModel here
                            val productViewModel: ItemViewModel = viewModel(
                                factory = ItemViewModelFactory(appContainer.itemRepository)
                            )
                            ItemListScreen(viewModel = productViewModel, navController = navController)
                        }

                    }
                }
            }
        }
    }
}
