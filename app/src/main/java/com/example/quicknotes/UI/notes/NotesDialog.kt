package com.example.quicknotes.UI.notes

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import com.example.quicknotes.data.notesData.NotesEntity
import com.example.quicknotes.viewmodel.NotesViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NotesDialog(onDismiss: () -> Unit, notesViewModel: NotesViewModel) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
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
                    Button(
                        modifier = Modifier
                            .wrapContentSize(),
                        onClick = {
                            val newNote = NotesEntity(
                                title = title,
                                description = description
                            )
                            if ((title.isNotEmpty() && description.isNotEmpty()) || (title.isNotEmpty() || description.isNotEmpty())) {
                                notesViewModel.addNote(newNote)
                                onDismiss()
                            }
                            else{
                                Toast.makeText(context, "Empty Note Discarded", Toast.LENGTH_SHORT).show()
                                onDismiss()
                            }
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
        }
    }
}