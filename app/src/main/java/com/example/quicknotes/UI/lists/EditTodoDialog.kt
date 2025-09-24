package com.example.quicknotes.UI.lists

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
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
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.quicknotes.data.listsData.TodoEntity
import com.example.quicknotes.viewmodel.TodoViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("UnusedContentLambdaTargetStateParameter")
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
    var deleteButton by remember { mutableStateOf(false) }
    val transition = updateTransition(targetState = deleteButton, label = "deleteButtonTransition")

    val buttonColor by transition.animateColor(
        label = "buttonColor",
        transitionSpec = { tween(durationMillis = 100) }
    ) { isDelete ->
        if (isDelete) Color.Red else Color.LightGray
    }

    val buttonWidth by transition.animateDp(
        label = "buttonWidth",
        transitionSpec = { tween(durationMillis = 300) }
    ) { isDelete ->
        if (isDelete) 130.dp else 70.dp
    }


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
                horizontalAlignment = Alignment.End,
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
                                .width(buttonWidth)
                                .height(40.dp),
                            onClick = {
                                if (deleteButton) {
                                    todoViewModel.delete(todoEntity)
                                    onDismiss()
                                } else {
                                    deleteButton = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                            shape = RoundedCornerShape(20.dp, 5.dp, 5.dp, 20.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .alpha(if (deleteButton) 0f else 1f)
                                )

                                if (deleteButton) {
                                    Text(
                                        text = "Delete?",
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                }
                            }
                        }
                        LaunchedEffect(deleteButton) {
                            if (deleteButton) {
                                delay(2500)
                                deleteButton = false
                            }
                        }
                        Spacer(Modifier.width(5.dp))
                        Button(modifier = Modifier.height(40.dp).width(70.dp),
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
                                }
                                else {
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

                LazyColumn(modifier = Modifier.wrapContentHeight()) {
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
                        modifier = Modifier.padding(vertical = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.Default.Add, "Add item")
                    }
                }
            }
            Row (
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ){
                Text("Last Created : ", style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
                        .format(Date(todoEntity.timestamp)),
                    style = MaterialTheme.typography.bodySmall
                )
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
                .padding(4.dp)
                .border(1.dp,MaterialTheme.colorScheme.primary, CircleShape),
        ){

        }
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