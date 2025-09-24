package com.example.quicknotes.UI.notes

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.quicknotes.data.notesData.NotesEntity
import com.example.quicknotes.viewmodel.NotesViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EditNoteDialog(
    onDismiss: () -> Unit,
    notesEntity: NotesEntity,
    notesViewModel: NotesViewModel
) {
    var title by remember { mutableStateOf(notesEntity.title) }
    var description by remember { mutableStateOf(notesEntity.description) }
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color.Transparent,
        unfocusedBorderColor = Color.Transparent,
        cursorColor = MaterialTheme.colorScheme.primary
    )
    var deleteButton by remember { mutableStateOf(false) }
    var clearButton by remember { mutableStateOf(false) }
    val transition = updateTransition(targetState = deleteButton, label = "deleteButtonTransition")
    val transition2 = updateTransition(targetState = clearButton, label = "clearButtonTransition")

    val buttonColor by transition.animateColor(
        label = "buttonColor",
        transitionSpec = { tween(durationMillis = 100) }
    ) { isDelete ->
        if (isDelete) Color.Red else Color.LightGray
    }

    val deleteButtonWidth by transition.animateDp(
        label = "buttonWidth",
        transitionSpec = { tween(durationMillis = 300) }
    ) { isDelete ->
        if (isDelete) 130.dp else 70.dp
    }
    val clearButtonWidth by transition2.animateDp(
        label = "buttonWidth",
        transitionSpec = { tween(durationMillis = 300) }
    ) { isClear ->
        if (isClear) 130.dp else 70.dp
    }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(
                                onClick = onDismiss
                            ),
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                    )
                    Row(Modifier.wrapContentSize()){
                        Button(
                            modifier = Modifier
                                .width(deleteButtonWidth).height(40.dp),
                            onClick = {
                                if (deleteButton){
                                    notesViewModel.delete(notesEntity)
                                    onDismiss()
                                }else {
                                    deleteButton = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                            shape = RoundedCornerShape(20.dp, 5.dp, 5.dp, 20.dp)
                        )
                        {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                modifier = Modifier
                                    .size(24.dp)
                            )
                            if (deleteButton) {
                                Text(
                                    text = "Delete?",
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                        }
                        LaunchedEffect(deleteButton || clearButton) {
                            if (deleteButton || clearButton) {
                                delay(2500)
                                deleteButton = false
                                clearButton = false
                            }
                        }
                        Spacer(Modifier.width(5.dp))
                        Button(
                            modifier = Modifier.width(clearButtonWidth)
                                .height(40.dp),
                            onClick = {
                                if (clearButton){
                                    title = ""
                                    description = ""
                                    clearButton = false
                                }else{
                                    clearButton = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                            shape = RoundedCornerShape(5.dp, 20.dp, 20.dp, 5.dp)
                        )
                        {
                            Image(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear"
                            )
                            if (clearButton){
                                Text(
                                    text = "Clear?",
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                        }
                    }

                }
                OutlinedTextField(
                    value = title,
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    onValueChange = {title = it},
                    placeholder = {
                        Text(
                            text = "Title",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    textStyle = MaterialTheme.typography.headlineSmall
                        .copy(fontWeight = FontWeight.Bold),
                    colors = textFieldColors
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = {description = it},
                    placeholder = { Text ("Description") },
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    colors = textFieldColors
                )
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
                        .format(Date(notesEntity.timestamp)),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
    val updateNote = notesEntity.copy(
        title = title,
        description = description
    )
    notesViewModel.update(updateNote)
}