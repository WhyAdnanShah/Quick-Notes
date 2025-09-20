package com.example.quicknotes.data.listsData

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "todo")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)

/*          Here the 'id' of the TodoEntity is linked with the 'listId' of the ListItemEntity
*           TodoEntity is the parent table and ListItemEntity is the child table.
*/
@Entity(
    tableName = "list_items",
    foreignKeys = [ForeignKey(
        entity = TodoEntity::class,
        parentColumns = ["id"],
        childColumns = ["listId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("listId")]
)

data class ListItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val listId: Int,
    val content: String,
    val isChecked: Boolean = false
)