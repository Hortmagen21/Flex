package com.example.flex.POJO

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "chat_message_table")
data class ChatMessage(
    var text: String = "",
    var timeSended: Long = 0,
    var userId: Long = 0,
    @Ignore
    var user: User = User(userId),
    var userImgLink: String = "",
    var userName: String = "",
    var isMy: Boolean = false,
    var belongsToChat: Long = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}