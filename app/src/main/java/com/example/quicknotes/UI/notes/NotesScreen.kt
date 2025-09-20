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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.quicknotes.R
import com.example.quicknotes.UI.SettingsDialog
import com.example.quicknotes.UI.lists.TodoDialog
import com.example.quicknotes.viewmodel.NotesViewModel
import com.example.quicknotes.viewmodel.TodoViewModel

class NotesScreenModel : ViewModel() {
    private val _isNotesDialog = mutableStateOf(false)
    val isNotesDialog: MutableState<Boolean> = _isNotesDialog
    private val _isListsDialog = mutableStateOf(false)
    val isListsDialog: MutableState<Boolean> = _isListsDialog
    private val _isSettings = mutableStateOf(false)
    val isSettings: MutableState<Boolean> = _isSettings
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@Composable
fun NotesScreen(notesScreenModel: NotesScreenModel, notesViewModel: NotesViewModel, todoViewModel: TodoViewModel, onSignOut: () -> Unit) {
    Log.d("NotesScreen", "NotesScreen called")
    var isNotesDialog by notesScreenModel.isNotesDialog
    var isListsDialog by notesScreenModel.isListsDialog
    var isSettings by notesScreenModel.isSettings
    val listState = rememberLazyListState()
    var expanded by remember { mutableStateOf(false) }
    val fabVisible by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }
    BackHandler(expanded){
        expanded = false
    }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.no_notes))
    val notes by notesViewModel.allNotes.collectAsState(initial = emptyList())
    val lists by todoViewModel.allLists.collectAsState(initial = emptyList())
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
            FloatingActionButtonMenu(
                expanded = expanded,
                button = {
                    ToggleFloatingActionButton(
                        modifier =
                            Modifier
                                .semantics {
                                    traversalIndex = -1f
                                    stateDescription = if (expanded) "Expanded" else "Collapsed"
                                    contentDescription = "Toggle menu"
                                }
                                .animateFloatingActionButton(
                                    visible = fabVisible || expanded,
                                    alignment = Alignment.BottomEnd,
                                ),
                        checked = expanded,
                        onCheckedChange = { expanded = !expanded },
                    ) {
                        Icon(
                            if (expanded) Icons.Filled.Close else Icons.Filled.Add, contentDescription = if (expanded) "Close" else "Open"
                        )
                    }
                },
            ) {
                FloatingActionButtonMenuItem(
                    text = { Text("Lists") },
                    onClick = {
                        isListsDialog = true
                        expanded = false },
                    icon = {
                        Icon(Icons.Filled.List, contentDescription = "List")
                    }
                )
                FloatingActionButtonMenuItem(
                    text = { Text("Text") },
                    onClick = {
                        isNotesDialog = true
                        expanded = false
                    },
                    icon = {
                        Icon(Icons.Filled.Edit, contentDescription = "Add")
                    }
                )
            }
        }
    ){paddingValues ->
        if (notes.isEmpty() && lists.isEmpty())
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
            NotesGridView(notes = notes, lists = lists, notesViewModel = notesViewModel, todoViewModel = todoViewModel, paddingValues)
        }
    }
    if (isListsDialog){
        TodoDialog(onDismiss = { isListsDialog = false }, todoViewModel = todoViewModel)
    }
    if (isNotesDialog){
        NotesDialog(onDismiss = { isNotesDialog = false }, notesViewModel = notesViewModel)
    }
    if (isSettings){
        SettingsDialog(onDismiss = { isSettings = false }, onSignOut = onSignOut)
    }
}