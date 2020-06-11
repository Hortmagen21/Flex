package com.example.flex.DataBase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.flex.POJO.ChatMessage

@Dao
interface ChatMessageDao {
    @Insert
    fun insert(message:ChatMessage)
    @Insert
    fun insert(messages:List<ChatMessage>)
    @Delete
    fun delete(message:ChatMessage)
    @Query("select * from chat_message_table where belongsToChat=:chatId order by timeSended desc")
    fun getMessagesFromChat(chatId:Long):LiveData<List<ChatMessage>>
    @Query("delete from chat_message_table where belongsToChat=:chatId")
    fun deleteAllFromChat(chatId:Long)
}