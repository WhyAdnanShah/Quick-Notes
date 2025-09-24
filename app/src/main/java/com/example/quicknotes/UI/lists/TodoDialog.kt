package com.example.quicknotes.UI.lists

import android.widget.Toast
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.quicknotes.UI.notes.NotesScreenModel
import com.example.quicknotes.data.listsData.TodoEntity
import com.example.quicknotes.viewmodel.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDialog(
    onDismiss: () -> Unit,
    todoViewModel: TodoViewModel,
    notesScreenModel: NotesScreenModel
) {
    val context = LocalContext.current
    var title by rememberSaveable { mutableStateOf("") }
    var items by rememberSaveable { mutableStateOf(listOf("")) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onDismiss
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }

                    Button(
                        onClick = {
                            val filteredItems = items.filter { it.isNotEmpty() }
                            val newList = TodoEntity(title = title)
                            if (title.isNotEmpty() && filteredItems.isNotEmpty()) {
                                todoViewModel.add(newList){generatedId ->
                                    filteredItems.forEach { item ->
                                        todoViewModel.insertItem(generatedId, item)
                                    }
                                    onDismiss()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    if (title.isEmpty() && filteredItems.isNotEmpty()) "List title cannot be empty"
                                    else if (title.isNotEmpty() && filteredItems.isEmpty()) "Title cannot be empty" else "Add at least one item",
                                    Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                        enabled = title.isNotEmpty() && items.any { it.isNotEmpty() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save"
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = {
                        Text(
                            text = "List Title",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    textStyle = MaterialTheme.typography.headlineSmall
                        .copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(items.size) { index ->
                        TodoListItemInput(
                            item = items[index],
                            onItemChange = { newValue ->
                                items = items.toMutableList().apply {
                                    this[index] = newValue
                                }
                            },
                            onAddNew = {
                                if (index == items.size - 1 && items.last().isNotEmpty()) {
                                    items = items + ""
                                }
                            },
                            onRemove = {
                                if (items.size > 1) {
                                    items = items.toMutableList().apply { removeAt(index) }
                                }
                            },
                            isLast = index == items.size - 1
                        )
                    }
                }
                if (items.all { it.isNotEmpty() }) {
                    Button(
                        onClick = {
                            items = items + ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add item"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Item")
                    }
                }
            }
        }
    }
}

@Composable
fun TodoListItemInput(
    item: String,
    onItemChange: (String) -> Unit,
    onAddNew: () -> Unit,
    onRemove: () -> Unit,
    isLast: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .border(1.dp, Color.Gray, CircleShape)
                .padding(4.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        OutlinedTextField(
            value = item,
            onValueChange = {
                onItemChange(it)
                if (it.isNotEmpty() && isLast) {
                    onAddNew()
                }
            },
            placeholder = { Text("List item...") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        if (!isLast || item.isEmpty()) {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove item",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}