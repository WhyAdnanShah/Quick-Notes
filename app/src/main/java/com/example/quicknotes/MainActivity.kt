package com.example.quicknotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.credentials.CredentialManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quicknotes.UI.LoginScreen
import com.example.quicknotes.UI.notes.NotesScreen
import com.example.quicknotes.viewmodel.NotesViewModelFactory
import com.example.quicknotes.ui.theme.QuickNotesTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        credentialManager = CredentialManager.create(this)

        setContent {
            QuickNotesTheme {
                val navController = rememberNavController()
                val currentUser = auth.currentUser

                NavHost(
                    navController = navController,
                    startDestination = if (currentUser != null) "notes" else "login"
                ) {
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate("notes") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("notes") {
                        NotesScreen(
                            notesViewModel = viewModel(
                                factory = NotesViewModelFactory(application)
                            ),
                            onSignOut = {
                                auth.signOut()
                                navController.navigate("login") {
                                    popUpTo("notes") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}