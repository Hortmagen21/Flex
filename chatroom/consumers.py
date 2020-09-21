import asyncio
from django.contrib.auth import get_user_model
import json
from channels.generic.websocket import WebsocketConsumer
from channels.consumer import AsyncConsumer
from channels.db import database_sync_to_async
#from .models import Thread, ChatMessage
from .models import Message,Chat,MsgType,BannedInChat
from .views import create_chat_ws, get_receivers_ids, get_user_special_tokens, get_receiver_avatar, ban_check_user,\
    ban_user
from django.core.exceptions import MultipleObjectsReturned,ObjectDoesNotExist
from django.contrib.auth.models import User
from channels.auth import login
from django.db import close_old_connections
from asgiref.sync import async_to_sync
from asgiref.sync import sync_to_async
from fcm_django.models import AbstractFCMDevice
from fcm_django.fcm import fcm_send_message,FCMNotification
from fcm_django.api.rest_framework import FCMDevice
#from channels_presence.models import Room
from django.db.utils import OperationalError
#from channels_presence.decorators import remove_presence
#from channels_presence.models import Presence
from chatroom.views import remove_from_group_chat, add_to_group_chat
from channels_presence.models import Room, Presence
#from channels.auth
from channels.exceptions import StopConsumer

API_KEY = "AAAAVQJ_SoU:APA91bFWua6OATBhXUCZdTGiRWBg_af-3H4wrLmBBBC8dcPzzpacSg8HYbm3YUYTGiK9sLgU-Dm5-IxgSIxHOSMSNq7o-NQXW37QWX5gykQzNGr7USXfm1HpRZnAkcF4hvbFi0Dk9lEn"

user_to_chats = {}
online_users = []
user_ban_to_chat = {}
users_banned = []


class ChatConsumer(AsyncConsumer):
    async def websocket_connect(self, event):
        await self.send({
            "type": "websocket.accept"
        })
        try:
            chat_id = self.scope['cookies']['chat_id']
        except KeyError:
            other_user = self.scope['cookies']['username']
            if self.scope['user'].is_anonymous:
                await self.close()
            else:
                close_old_connections()
                chat_id = await self.get_tread(str(self.scope['user']), other_user)#treat_obj == chat_id
                close_old_connections()
        else:
            print('IM IN ELSE')
            chat_id = self.scope['cookies']['chat_id']
        finally:
            print('FINALLY')
            me = str(self.scope['user'])
            self.me = me
            print(self.me,'MEEEE')
            self.chat_id = int(chat_id)
            print(self.scope["headers"],'HEADDERS')
            chat_room = f"chat_{chat_id}"
            self.chat_room = chat_room
            await self.room_add(chat_room)
            # await Room.objects.add(chat_room, self.channel_name, self.scope["user"])
            # Room.objects.add(chat_room, self.channel_name, self.scope["user"])
            await self.send({
                "type": "websocket.send",
                "text": str(chat_id),
            })
            print(self.scope['cookies']['id'],' ',chat_id, 'IDDDDDDDDDDDDDD')
            user_to_chats[int(self.scope['cookies']['id'])] = int(chat_id)
            print(user_to_chats,'USERS_TO_CHAT')
            await self.channel_layer.group_add(
                chat_room,
                self.channel_name
            )

    async def websocket_receive(self, event):
        front_text = event.get('text', False)
        dict_data = json.loads(front_text)
        is_ban = await self.ban_check(self.scope['cookies']['id'], self.scope['cookies']['chat_id'])
        if is_ban:
            raise StopConsumer
        request_type = str(dict_data['type'])
        if request_type == 'heartbeat':
            print('heartbeat')
            await self.presence_touch()
            print('END heartbeat')
        if request_type == 'add_users':
            print('add_user')
            close_old_connections()
            await self.add_to_group(chat_id=self.chat_id, user_id=self.scope['cookies']['id'],
                                    add_users_id=dict_data['users_id'])
            my_data_dict = {"front": front_text}
            await self.channel_layer.group_send(
                self.chat_room,
                {
                    "type": "chat_message",
                    "text": json.dumps(my_data_dict),
                })
            msg_obj = await self.save_msg(self.chat_id, str(dict_data['users_id']), int(dict_data['time']),
                                          msg_type=request_type)
            close_old_connections()
            print('END add_user')
        if request_type == 'delete_users':
            print('delete_user')
            close_old_connections()
            error_list = (await self.remove_from_group(chat_id=self.chat_id, user_id=self.scope['cookies']['id'],
                          remove_users_id=dict_data['users_id']))['error_list']
            room = await self.get_room_by_channel_name()
            room_id = int(room.id)
            print(dict_data['users_id'].split(), 'DICT_DATA')
            for user_id in dict_data['users_id'].split():
                print(user_id, 'USER IN DICT_DATA')
                try:
                    error_list[int(user_id)]
                except KeyError:
                    try:
                        presence = await self.get_presence_list(room_id, user_id)
                        presence[-1].channel_name
                    except IndexError:
                        pass
                    else:
                        del user_to_chats[int(self.scope['cookies']['id'])]
                        msg_obj = await self.save_msg(self.chat_id, str(dict_data['users_id']), int(dict_data['time']),
                                                      msg_type=request_type)
                        close_old_connections()
                        await self.ban_user(dict_data['users_id'], msg_obj.message_id, self.scope['cookies']['chat_id'])
                        close_old_connections()
                        '''sync_to_async(self.channel_layer.group_discard)(
                                group=self.chat_room,
                                channel=presence[-1].channel_name
                            )'''
                else:
                    if error_list[int(user_id)] == '404':
                        pass
                    if error_list[int(user_id)] == '403':
                        pass
            my_data_dict = {"front": front_text}
            await self.channel_layer.group_send(
                self.chat_room,
                {
                    "type": "chat_message",
                    # "text": json.dumps(data),
                    "text": json.dumps(my_data_dict),
                })
            print('END delete_user')
        if request_type == 'message':
            print('message')
            close_old_connections()
            receivers_ids = await self.dump_user_ids(int(self.chat_id))
            close_old_connections()
            ava = await self.get_ava(int(self.scope['cookies']['id']))
            close_old_connections()
            if front_text is not None:
                print(front_text, 'FRONT_TEXT')
                close_old_connections()
                msg_obj = await self.save_msg(self.chat_id, str(dict_data['text']), int(dict_data['time']),
                                              msg_type=request_type)
                close_old_connections()
            for user in receivers_ids:
                try:
                    user_to_chats[user]
                except KeyError:
                    print('IM WORKING1')
                    token = await self.get_user_token(user)
                    for i in token:
                        close_old_connections()
                        print(i, 'CHECK MEE')
                        fcm_send_message(registration_id=i, data={"msg_id": int(msg_obj.message_id), "ava": str(ava)}, body=str(dict_data['text'][:20]))
                    break
                else:
                    close_old_connections()
                    print('IAM HERE')
                    if user_to_chats[int(self.scope['cookies']['id'])] == int(self.chat_id):
                        close_old_connections()
                        my_data_dict = {"front": front_text,
                                        "ava": str(ava),
                                        "msg_id": int(msg_obj.message_id),
                                        }
                        await self.channel_layer.group_send(
                            self.chat_room,
                            {
                                "type": "chat_message",
                                # "text": json.dumps(data),
                                "text": json.dumps(my_data_dict),
                            })
                    else:
                        close_old_connections()
                        print('IM WORKING2')
                        token = await self.get_user_token(user)
                        for i in token:
                            close_old_connections()
                            print(i, 'CHECK MEE')
                            fcm_send_message(registration_id=i, data={"msg_id": int(msg_obj.message_id), "ava": str(ava)},
                                             body=str(dict_data['text'][:20]))
                        break
                close_old_connections()
        else:
            print('NOT TYPE')

    async def chat_message(self, event):
        print('text', event)
        await self.send({
            "type": "websocket.send",
            "text": event['text'],
        })

    async def websocket_disconnect(self, event):
        del user_to_chats[int(self.scope['cookies']['id'])]
        await self.room_remove(channel_name=self.channel_name)
        print('disconnected', event)

    @database_sync_to_async
    def room_remove(self, channel_name):
        return Room.objects.remove(self.chat_room, channel_name)

    @database_sync_to_async
    def get_tread(self, user, other_username):
        return create_chat_ws(other_username, user)

    @database_sync_to_async
    def save_msg(self, threat_obj, msg, time, msg_type):
        user_id = (User.objects.get(username=str(self.me))).id
        message_obj = Message.objects.create(chat_id=threat_obj, user_id=int(user_id), message=msg, date=time)
        msg_type_obj = MsgType(id=message_obj.message_id, type=msg_type)
        msg_type_obj.save()
        return message_obj

    @database_sync_to_async
    def dump_user_ids(self, chat_id):
        return get_receivers_ids(chat_id)

    @database_sync_to_async
    def get_user_token(self, user_id):
        return get_user_special_tokens(int(user_id))

    @database_sync_to_async
    def get_ava(self, user_id):
        return get_receiver_avatar(int(user_id))

    @database_sync_to_async
    def get_fcm_tokens(self,user):
        #return get_fcm_device(user)
        return FCMDevice.objects.filter(device_id=user)

    @database_sync_to_async
    def add_to_group(self, chat_id, user_id, add_users_id):
        return add_to_group_chat(chat_id=chat_id, user_id=user_id,
                                 add_users_id=add_users_id)

    @database_sync_to_async
    def remove_from_group(self, chat_id, user_id, remove_users_id):
        return remove_from_group_chat(chat_id=chat_id, user_id=user_id,
                                      remove_users_id=remove_users_id)

    @database_sync_to_async
    def room_add(self, chat_room):
        return Room.objects.add(chat_room, self.channel_name, User.objects.get(id=int(self.scope['cookies']['id'])))

    @database_sync_to_async
    def get_room_by_channel_name(self,):
        return Room.objects.get(channel_name=self.chat_room)

    @database_sync_to_async
    def get_presence_list(self, room_id, user_id):
        return list(Presence.objects.filter(room_id=room_id, user_id=user_id))

    @database_sync_to_async
    def remove_presence_room(self, channel_name):
        Room.objects.remove(self.chat_room, channel_name)
        return True

    @database_sync_to_async
    def presence_touch(self):
        return Presence.objects.touch(self.channel_name)

    @database_sync_to_async
    def ban_user(self, users_id, msg_id, chat_id):
        return ban_user(users_id, msg_id, chat_id)

    @database_sync_to_async
    def ban_check(self, user_id, chat_id):
        return ban_check_user(user_id, chat_id)



