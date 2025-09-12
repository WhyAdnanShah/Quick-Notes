package com.example.quicknotes.UI.notes

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.quicknotes.R
import com.example.quicknotes.UI.SettingsDialog
import com.example.quicknotes.viewmodel.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@Composable
fun NotesScreen(notesViewModel: NotesViewModel, onSignOut: () -> Unit) {
    Log.d("NotesScreen", "NotesScreen called")
    var isNotesDialog by remember { mutableStateOf(false) }
    var isSettings by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    BackHandler(expanded){
        expanded = false
    }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.no_notes))
    val notes by notesViewModel.allNotes.collectAsState(initial = emptyList())
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text(text = "Quick Notes", fontSize = 30.sp)
                        Icon(Icons.Default.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier
                                .clickable(
                                    onClick = { isSettings = true }
                                )
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isNotesDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ){paddingValues ->
        if (notes.isEmpty())
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LottieAnimation(composition, iterations = LottieConstants.IterateForever ,modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.CenterHorizontally))
                Text(text = "Add notes by tapping '+' button")
            }
        else{
            NotesGridView(notes = notes, notesViewModel = notesViewModel, paddingValues)
        }
    }
    if (isNotesDialog){
        NotesDialog(onDismiss = { isNotesDialog = false }, notesViewModel = notesViewModel)
    }
    if (isSettings){
        SettingsDialog(onDismiss = { isSettings = false }, onSignOut = onSignOut)
    }
}
