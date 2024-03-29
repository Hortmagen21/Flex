package com.example.flex.POJO

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_table")
data class Chat(
    @PrimaryKey var id: Long,
    var name: String = "",
    var users: List<User>
) {
}