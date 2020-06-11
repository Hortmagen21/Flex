package com.example.flex.Websockets

import com.example.flex.MainData
import com.example.flex.POJO.ChatMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ChatWebsocket(
    val mChatInteraction: ChatInteraction,
    val csrftoken: String,
    val sessionId: String
) {
    private var webSocket: WebSocket? = null
    var user: String = ""
        private set
    var chatId: Long = 0
    val client: OkHttpClient = OkHttpClient.Builder().build()
    var isFirst: Boolean = true
    fun connectChat(user: String,yourUserId:Long) {
        this.user = user
        val request = Request.Builder()
            .url("wss://${MainData.BASE_URL}/${MainData.CHAT}/$user")
            .addHeader("Cookie", "csrftoken=$csrftoken; sessionid=$sessionId;id=$yourUserId")
            .build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                if (true) {
                }
                /*val body=response.body?.string()
                chatId=body.toString().toLong()*/
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                if (isFirst) {
                    //chatId = text.toLong()
                    //mChatInteraction.setChatId(chatId)
                    isFirst = false
                } else {
                    val temp=JSONObject(text)

                    mChatInteraction.receiveMessage(
                        ChatMessage(
                            text =temp["text"].toString(),
                            timeSended = temp["time"].toString().toLong(),
                            belongsToChat = chatId,
                            isMy = false
                        )
                    )
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                if (true) {

                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                if (true) {

                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                if (true) {

                }
            }
        })
    }

    fun sendMessage(message: ChatMessage) {
        if (webSocket != null) {
            val text=encodeMessageToJson(message)
            webSocket!!.send(text)
        }
    }

    fun createChat(userId: Long) {
        val formBody = FormBody.Builder()
            .add("id", userId.toString())
            .add("csrfmiddlewaretoken", csrftoken)
            .build()
        val request = Request.Builder()
            .url("https://${MainData.BASE_URL}/${MainData.CHAT}/${MainData.CREATE_CHAT}")
            .post(formBody)
            .addHeader(MainData.HEADER_REFRER, "https://" + MainData.BASE_URL)
            .addHeader("Cookie", "csrftoken=${csrftoken}; sessionid=${sessionId}")
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    CoroutineScope(IO).launch {
                        val body = response.body?.string()
                        if (body != null) {
                            val jsonObject = JSONObject(body)
                            val keys = jsonObject.keys()
                            val isNew = jsonObject["isNew"]
                            val chatId = jsonObject["chat_id"].toString().toLong()
                            val avatar = jsonObject["receiver_ava"]
                            val receiverName = jsonObject["receiver_name"]
                            val listOfMessages = mutableListOf<ChatMessage>()
                            val messages = jsonObject["messages"]
                            if (messages is JSONArray) {
                                val length = messages.length()
                                for (i in 0 until length) {
                                    val value = messages[i]
                                    if (value is JSONObject) {
                                        val temp=JSONObject(value.toString())
                                        listOfMessages.add(
                                            ChatMessage(
                                                text =temp["text"].toString(),
                                                timeSended = temp["time"].toString().toLong(),
                                                belongsToChat = chatId
                                            )
                                        )
                                    }
                                }
                            }
                            setThisChatId(chatId)
                            mChatInteraction.setChatId(chatId)
                            mChatInteraction.receiveMessages(listOfMessages)
                        }
                    }
                } else if (response.code == MainData.ERR_403) {
                } else {

                }
            }
        })
    }
    private fun setThisChatId(chatId: Long){
        this.chatId=chatId
    }

    private fun encodeMessageToJson(message: ChatMessage): String {
        return "{" +
                "\"text\":\"${message.text}\"," +
                "\"time\":\"${message.timeSended}\"" +
                "}"

    }
}
interface ChatInteraction {
    fun receiveMessage(message: ChatMessage)
    fun receiveMessages(messages: List<ChatMessage>)
    fun setChatId(chatId: Long)
}