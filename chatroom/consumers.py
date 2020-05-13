import asyncio
from django.contrib.auth import get_user_model
import json
from channels.generic.websocket import WebsocketConsumer
from channels.consumer import AsyncConsumer
from channels.db import database_sync_to_async
#from .models import Thread, ChatMessage
from .models import Message
from .views import create_chat_ws
from django.contrib.auth.models import User



class ChatConsumer(AsyncConsumer):

    async def websocket_connect(self,event):


        await self.send({
            "type": "websocket.accept"
        })


        other_user=str(self.scope['url_route']['kwargs']['username'])
        me= str(self.scope['user'])
        for i,j in self.scope.items():
            print(i,j)
        treat_obj=await self.get_tread(me,other_user)
        print(treat_obj,'HERE')
        self.treat_obj = treat_obj
        #if treat_obj != -1:
        chat_room=f"chat_{treat_obj}"
        self.chat_room =  chat_room

        await self.channel_layer.group_add(
            chat_room,
            self.channel_name
        )

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
        # send messages
        data={
            "message":event['text'],
            "chat_id":self.treat_obj,
        }
        await self.send({
            "type": "websocket.send",
            "text": json.dumps(data),
        })

    async def websocket_disconnect(self,event):
        print('disconnected', event)



    @database_sync_to_async
    def get_tread(self, user, other_username):
        return create_chat_ws(other_username, user)

    #@database_sync_to_async
    #def get_tread(self, msg,time):
        #user_name = User.objects.get(id=int(user_id)).username
        #new_message=Message()
        #return create_chat_ws(other_username, user)





