package com.example.quicknotes.UI.notes

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import com.example.quicknotes.data.NotesEntity
import com.example.quicknotes.viewmodel.NotesViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun EditNoteDialog(
    onDismiss: () -> Unit,
    notesEntity: NotesEntity,
    notesViewModel: NotesViewModel
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(notesEntity.title) }
    var description by remember { mutableStateOf(notesEntity.description) }
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color.Transparent,
        unfocusedBorderColor = Color.Transparent,
        cursorColor = MaterialTheme.colorScheme.primary
    )
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
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                    )
                    Row(Modifier.wrapContentSize()){
                        Button(
                            modifier = Modifier
                                .wrapContentSize(),
                            onClick = {
                                notesViewModel.delete(notesEntity)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                        )
                        {
                            Image(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete"
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Button(
                            modifier = Modifier
                                .wrapContentSize(),
                            onClick = {
                                val updateNote = notesEntity.copy(
                                    title = title,
                                    description = description
                                )
                                notesViewModel.update(updateNote)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                        )
                        {
                            Image(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Save"
                            )
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
}