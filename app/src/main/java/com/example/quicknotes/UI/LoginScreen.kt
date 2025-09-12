package com.example.quicknotes.UI

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.example.quicknotes.MainActivity
import com.example.quicknotes.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class CarouselItem(
    val id: Int,
    val about: String,
    val contentDescription: String
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: (FirebaseUser) -> Unit) {
    val context = LocalContext.current
    val carouselItems = listOf(
        "Welcome to Quick Note, your digital sanctuary for thoughts. We've stripped away all distractions to offer a pure, focused writing experience.",
        "Experience the power of minimalism. Without tags or complex formatting, your mind is free to focus solely on writing.",
        "Your ideas are safe the moment you type them. Quick Note automatically saves every character, ensuring you never lose a spark of inspiration.",
        "Rediscover your past ideas in an instant. Our search function helps you find any note in seconds.",
        "Quick Note is your ultimate digital scratchpad. Designed for speed and simplicity—everything you need, nothing you don't."
    )

    var currentItem by remember { mutableIntStateOf(0) }
    val animatedHeight by animateDpAsState(
        targetValue = 200.dp,
        animationSpec = tween(durationMillis = 500),
        label = "carouselAnimation"
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000L)
            currentItem = (currentItem + 1) % carouselItems.size
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            color = Color(0xFF03DAC5)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "QN",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Quick Note",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "Minimalist Note Taking",
                    fontSize = 16.sp,
                    color = Color(0xFF64FFDA),
                    letterSpacing = 0.5.sp
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(animatedHeight)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E1E1E))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = carouselItems[currentItem],
                    fontSize = 16.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                for (i in carouselItems.indices) {
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (i == currentItem) 12.dp else 8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (i == currentItem) Color(0xFF03DAC5)
                                else Color(0xFF424242)
                            )
                    )
                }
            }

            Button(
                onClick = { (context as MainActivity).launchGoogleSignIn(onLoginSuccess) } ,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF03DAC5),
                    contentColor = Color(0xFF121212)
                )
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

fun MainActivity.launchGoogleSignIn(onLoginSuccess: (FirebaseUser) -> Unit) {
    lifecycleScope.launch {
        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(getString(R.string.default_web_client_id))
                .setFilterByAuthorizedAccounts(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(this@launchGoogleSignIn, request)  // ✅ works now
            val credential = result.credential

            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                firebaseAuthWithGoogle(googleIdTokenCredential.idToken, onLoginSuccess)
            }
        } catch (e: Exception) {
            Log.e("LOGIN", "Google sign-in failed: ${e.localizedMessage}")
        }
    }
}

fun MainActivity.firebaseAuthWithGoogle(
    idToken: String,
    onLoginSuccess: (FirebaseUser) -> Unit
) {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    auth.signInWithCredential(credential)
        .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.let { onLoginSuccess(it) }
            } else {
                Log.e("FIREBASE", "Auth failed: ${task.exception?.message}")
            }
        }
}