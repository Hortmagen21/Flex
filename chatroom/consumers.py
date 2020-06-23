import asyncio
from django.contrib.auth import get_user_model
import json
from channels.generic.websocket import WebsocketConsumer
from channels.consumer import AsyncConsumer
from channels.db import database_sync_to_async
#from .models import Thread, ChatMessage
from .models import Message,Chat
from .views import create_chat_ws,get_receivers_ids,get_user_special_tokens,get_receiver_avatar
from django.contrib.auth.models import User
from channels.auth import login
from django.db import close_old_connections
from asgiref.sync import async_to_sync
from asgiref.sync import sync_to_async
from fcm_django.models import AbstractFCMDevice
from fcm_django.fcm import fcm_send_message,FCMNotification

API_KEY="AAAAVQJ_SoU:APA91bFWua6OATBhXUCZdTGiRWBg_af-3H4wrLmBBBC8dcPzzpacSg8HYbm3YUYTGiK9sLgU-Dm5-IxgSIxHOSMSNq7o-NQXW37QWX5gykQzNGr7USXfm1HpRZnAkcF4hvbFi0Dk9lEn"

user_to_chats = {}

online_users = []


class ChatConsumer(AsyncConsumer):
    async def websocket_connect(self,event):
        await self.send({
            "type": "websocket.accept"
        })
        other_user = str(self.scope['url_route']['kwargs']['username'])
        if self.scope['user'].is_anonymous:
            await self.close()
        else:
            me = str(self.scope['user'])
            #print(self.scope['user'], 'IT IS USER!!!!')
            self.me= me
            treat_obj=await self.get_tread(me,other_user)#treat_obj == chat_id
            close_old_connections()
            self.treat_obj = int(treat_obj)
            #print(self.scope["headers"],'HEADDERS')
            chat_room=f"chat_{treat_obj}"
            self.chat_room =  chat_room
            await self.send({
                "type": "websocket.send",
                "text": str(treat_obj),
            })
            user_to_chats[int(self.scope['cookies']['id'])] = int(treat_obj)
            print(user_to_chats,'USERS_TO_CHAT')
            await self.channel_layer.group_add(
                chat_room,
                self.channel_name
            )

    async def websocket_receive(self,event):
        front_text = event.get('text', None)#chat_id
        user = str(self.scope['user'])
        close_old_connections()
        receivers_ids = await self.dump_user_ids(int(self.treat_obj))
        close_old_connections()
        ava = await self.get_ava(int(self.scope['cookies']['id']))
        if front_text is not None:
            dict_data = json.loads(front_text)
            # msg =dict_data.get('message')

            # if user.is_authenticated:
            # username=user
            data = {'text': dict_data['text'],
                    'time': dict_data['time'],
                    'ava': str(ava),
                    }
            msg_obj = await self.save_msg(self.treat_obj, str(dict_data['text']), int(dict_data['time']))
            close_old_connections()
            await self.msg_priority(self.treat_obj, 1)
        for user in receivers_ids:
            try:
                user_to_chats[int(user)]
            except KeyError:
                close_old_connections()
                token = await self.get_user_token(int(user))
                for i in token:
                    close_old_connections()
                    # response = FCMNotification(api_key=API_KEY)
                    print(i, 'CHECK MEE')
                    # await response.notify_single_device(registration_id=token, message_body='text')
                    fcm_send_message(registration_id=i, data={"msg_id": int(msg_obj.message_id), "ava": str(ava)}, body=dict_data['text'][:20])
            else:
                if user_to_chats[int(user)] == int(self.treat_obj):
                    close_old_connections()
                    async_to_sync(self.channel_layer.group_discard)(self.chat_room, self.channel_name)
                    await self.channel_layer.group_send(
                        self.chat_room,

                        {
                            "type": "chat_message",
                            #"text": json.dumps(data),
                            "text": front_text,
                        })
                    async_to_sync(self.channel_layer.group_add)(self.chat_room, self.channel_name)
                else:
                    close_old_connections()
                    token = await self.get_user_token(int(user))

                    for i in token:
                        close_old_connections()
                    #response = FCMNotification(api_key=API_KEY)
                        print(i,'CHECK MEE')
                    #await response.notify_single_device(registration_id=token, message_body='text')
                        fcm_send_message(registration_id=i, data={"msg_id": int(msg_obj.message_id), "ava": str(ava)}, body=dict_data['text'][:20])




                #await self.channel_layer.group_send(
                #self.chat_room,

                #{
                    #"type":"chat_message",
                    #"text":json.dumps(data),
                    #"text": front_text,
                #})
            #else:

            close_old_connections()

    async def chat_message(self,event):
        print('text', event)
        # send messages
        await self.send({
            "type": "websocket.send",
            "text": event['text'],
        })

    async def websocket_disconnect(self,event):
        del user_to_chats[int(self.scope['cookies']['id'])]
        print('disconnected', event)



    @database_sync_to_async
    def get_tread(self, user, other_username):
        return create_chat_ws(other_username, user)

    @database_sync_to_async
    def save_msg(self, threat_obj, msg, time):
        user_id = (User.objects.get(username=str(self.me))).id
        return Message.objects.create(chat_id=threat_obj,user_id=int(user_id),message=msg,date=time)
        #new_message.save()
        #return create_chat_ws(other_username, user)

    @database_sync_to_async
    def msg_priority(self,chat_id,priority_change):
        obj = Chat.objects.get(chat_id=chat_id)
        obj.priority += priority_change
        obj.save()
        return True

    @database_sync_to_async
    def dump_user_ids(self, chat_id):
        return get_receivers_ids(chat_id)

    @database_sync_to_async
    def get_user_token(self, user_id):
        return get_user_special_tokens(int(user_id))

    @database_sync_to_async
    def get_ava(self, user_id):
        return get_receiver_avatar(int(user_id))





