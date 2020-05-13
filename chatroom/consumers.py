import asyncio
from django.contrib.auth import get_user_model
import json
from channels.generic.websocket import WebsocketConsumer
from channels.consumer import AsyncConsumer
from channels.db import database_sync_to_async
#from .models import Thread, ChatMessage
from .models import ChatMembers
from .views import create_chat_ws



class ChatConsumer(AsyncConsumer):

    async def websocket_connect(self,event):


        await self.send({
            "type": "websocket.accept"
        })

        #await asyncio.sleep(30)#30
        other_user=str(self.scope['url_route']['kwargs']['username'])
        me= str(self.scope['user'])

        treat_obj=await self.get_tread(me,other_user)
        print(treat_obj,'HERE')
        #chat_id = get_tread(me,other_user)
        #print(chat_id,'IDD')



        #chat_id=dict_data.get('chat_id')
        #self.chat_id=chat_id



        #print("chat_{chat_id}".format(chat_id=chat_id),'HERREEE')
        chat_room=f"chat_{treat_obj}"
        self.chat_room =  chat_room

        await self.channel_layer.group_add(
            chat_room,
            self.channel_name
        )
        await self.send({
            "type" : "websocket.accept"
        })



    async def websocket_receive(self,event):
        front_text=event.get('text', None)#chat_id

        #if front_text is not None:
            #dict_data =json.loads(front_text)
            #msg =dict_data.get('message')

            #user = self.scope['user']
            #username='Anonimys'
            #if user.is_authenticated:
                #username=user
            #data= {'message':front_text,
                   #'user_name':username,
                   #}

        await self.channel_layer.group_send(
            self.chat_room,
            #new_event
            {
                "type":"chat_message",
                "text":front_text
            })



            #await self.send({
                #"type": "websocket.send",
                #"text":json.dumps(data),
                #"text":self.chat_room,

            #})


        #await self.send({
            #"type": "websocket.send",
            #"text":front_text,
        #})
        #print('receive', event)

    async def chat_message(self,event):
        print('text', event)

    async def websocket_disconnect(self,event):
        print('disconnected', event)
        #send messages
        await self.send({
            "type":"websocket.send",
            "text":event['text']
        })

    @database_sync_to_async
    def get_tread(self, user, other_username):
        return create_chat_ws(other_username, user)





