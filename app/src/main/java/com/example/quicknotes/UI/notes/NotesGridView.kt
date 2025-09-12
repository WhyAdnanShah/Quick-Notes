package com.example.quicknotes.UI.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quicknotes.data.NotesEntity
import com.example.quicknotes.viewmodel.NotesViewModel

@Composable
fun NotesGridView(
    notes: List<NotesEntity>,
    notesViewModel: NotesViewModel,
    paddingValues: PaddingValues
) {
    LazyVerticalStaggeredGrid(modifier = Modifier
        .fillMaxSize()
        .padding(top = paddingValues.calculateTopPadding(), start = 8.dp, end = 8.dp),
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 10.dp,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(notes.size) { index ->
            NotesCard(notesEntity = notes[index], notesViewModel = notesViewModel)
        }

    }
}

@Composable
fun NotesCard(notesEntity: NotesEntity, notesViewModel: NotesViewModel) {
    var isEditNote by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        onClick = { isEditNote = true },
    ) {
        Text(
            text = notesEntity.title.take(n = 50) + if (notesEntity.title.length > 50) "..." else "",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
        )
        Text(
            text = notesEntity.description.take(n = 150) + if (notesEntity.description.length > 150) "..." else "",
            fontWeight = FontWeight.W300,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
        )
    }
    if (isEditNote){
        EditNoteDialog(onDismiss = { isEditNote = false }, notesEntity = notesEntity, notesViewModel = notesViewModel)
    }

}
