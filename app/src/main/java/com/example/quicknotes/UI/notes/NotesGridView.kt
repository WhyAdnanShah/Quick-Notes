package com.example.quicknotes.UI.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quicknotes.UI.lists.EditTodoDialog
import com.example.quicknotes.data.listsData.TodoEntity
import com.example.quicknotes.data.notesData.NotesEntity
import com.example.quicknotes.viewmodel.NotesViewModel
import com.example.quicknotes.viewmodel.TodoViewModel
@Composable
fun NotesGridView(
    notes: List<NotesEntity>,
    lists: List<TodoEntity>,
    notesViewModel: NotesViewModel,
    todoViewModel: TodoViewModel,
    paddingValues: PaddingValues
) {
    val allItems = remember(notes, lists) {
        notes.map { it to true } + lists.map { it to false }
    }
    LazyVerticalStaggeredGrid(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = paddingValues.calculateTopPadding(),
                start = 8.dp,
                end = 8.dp
            ),
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 10.dp,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(allItems.size) { index ->
            val (item, isNote) = allItems[index]
            if (isNote) {
                NotesCard(
                    notesEntity = item as NotesEntity,
                    notesViewModel = notesViewModel
                )
            } else {
                TodoCard(
                    todoEntity = item as TodoEntity,
                    todoViewModel = todoViewModel
                )
            }
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


@Composable
fun TodoCard(todoEntity: TodoEntity, todoViewModel: TodoViewModel) {
    var isEditLists by rememberSaveable { mutableStateOf(false) }

    val listItems by todoViewModel.getListItems(todoEntity.id).collectAsState(initial = emptyList())
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        onClick = { isEditLists = true },
    ) {
        Column(modifier = Modifier.padding(8.dp)) {

            Text(
                text = todoEntity.title.take(50) + if (todoEntity.title.length > 50) "..." else "",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            listItems.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                ) {
                    Checkbox(
                        checked = item.isChecked,
                        onCheckedChange = {
                            todoViewModel.toggleItem(item)
                        }
                    )
                    Text(
                        text = item.content,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }

    if (isEditLists) {
        EditTodoDialog(
            onDismiss = { isEditLists = false },
            todoEntity = todoEntity,
            todoViewModel = todoViewModel
        )
    }
}