package com.example.quicknotes.UI.lists

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.quicknotes.data.listsData.TodoEntity
import com.example.quicknotes.viewmodel.TodoViewModel

@Composable
fun EditTodoDialog(
    onDismiss: () -> Unit,
    todoEntity: TodoEntity,
    todoViewModel: TodoViewModel
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(todoEntity.title) }
    val listItems by todoViewModel.getListItems(todoEntity.id).collectAsState(initial = emptyList())
    var items by remember { mutableStateOf(listItems.map { it.content }) }

    LaunchedEffect(listItems) {
        items = if (listItems.isEmpty()) listOf("")
        else listItems.map { it.content } + ""
    }

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
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close")
                    }
                    Row(Modifier.wrapContentSize()) {
                        Button(
                            modifier = Modifier
                                .wrapContentSize(),
                            onClick = {
                                todoViewModel.delete(todoEntity)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                            shape = RoundedCornerShape(20.dp, 5.dp, 5.dp, 20.dp)
                        )
                        {
                            Image(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete"
                            )
                        }
                        Spacer(Modifier.width(5.dp))
                        Button(
                            onClick = {
                                val filteredItems = items.filter { it.isNotEmpty() }
                                if (title.isNotEmpty() && filteredItems.isNotEmpty()) {
                                    val updatedTodo = todoEntity.copy(title = title)
                                    todoViewModel.update(updatedTodo)

                                    listItems.forEach { item ->
                                        todoViewModel.deleteItem(item)
                                    }

                                    filteredItems.forEach { content ->
                                        todoViewModel.insertItem(todoEntity.id, content)
                                    }
                                    onDismiss()

                                } else {
                                    Toast.makeText(
                                        context,
                                        if (title.isEmpty()) "List title cannot be empty"
                                        else "Add at least one item",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                            enabled = title.isNotEmpty() && items.any { it.isNotEmpty() },
                            shape = RoundedCornerShape(5.dp, 20.dp, 20.dp, 5.dp)
                        ) {
                            Icon(Icons.Default.Check, "Save")
                        }
                    }

                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("List Title", style = MaterialTheme.typography.headlineSmall) },
                    textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    items(items.size) { index ->
                        TodoListItemUpdate(
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
                        onClick = { items = items + "" },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(Icons.Default.Add, "Add item")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Item")
                    }
                }
            }
        }
    }
}
@Composable
fun TodoListItemUpdate(
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