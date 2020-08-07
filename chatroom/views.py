from django.shortcuts import render

import logging#?
import asyncio#?
import django

import asyncio
from channels.db import database_sync_to_async
from django.views.decorators.csrf import csrf_exempt, ensure_csrf_cookie, csrf_protect
from django.contrib.auth.decorators import login_required
from chatroom.models import Chat,ChatMembers,Message
from user_profile.models import UserAvatar,PostBase
from django.contrib.auth.models import User
from django.db.models import Max
from acc_base.models import UniqueTokenUser
from django.core.exceptions import MultipleObjectsReturned,ObjectDoesNotExist
from django.http import HttpResponse, HttpResponseRedirect, HttpResponseNotFound, HttpResponseBadRequest
from django.http import JsonResponse
import psycopg2
from django.db import IntegrityError
from psycopg2.extensions import ISOLATION_LEVEL_AUTOCOMMIT
from fcm_django.api.rest_framework import FCMDevice
from fcm_django.fcm import fcm_send_message,FCMNotification
from channels.consumer import AsyncConsumer
from user_profile.models import UserFollower
#from channels_presence.models import Room
core_url = 'https://sleepy-ocean-25130.herokuapp.com/'
test_url = 'http://127.0.0.1:8000/'

#!procedurs or function
@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def create_chat(request):
    if request.method == "POST":
        try:
            id_receiver = int(request.POST.get(['id'][0], False))
        except ValueError:
            return HttpResponseBadRequest()
        else:
            user_id = int(request.session['_auth_user_id'])
            chat_exist = False
            try:
                receiver_avatar = list(UserAvatar.objects.filter(id_user=int(id_receiver)))[-1]#new table
            except IndexError:
                ava = "None"#null
            except MultipleObjectsReturned:
                return HttpResponseBadRequest()
            else:
                img_ava = PostBase.objects.get(id=int(receiver_avatar.id_post))
                ava = img_ava.img
            finally:
                try:
                    receiver_name = User.objects.get(id=int(id_receiver))
                except ObjectDoesNotExist:
                    return HttpResponseNotFound()
                except MultipleObjectsReturned:
                    return HttpResponseBadRequest()
                else:
                    print('BEFORE CONNECT')
                    conn = psycopg2.connect(dbname='d7f6m0it9u59pk', user='iffjnrmpbopayf',
                                            password='20d31f747b4397c839a05d6d70d2decd02b23a689d86773a84d8dcfa23428946', host='ec2-54-83-1-101.compute-1.amazonaws.com')
                    print('CON CREATED')
                    cursor = conn.cursor()
                    print('CURSOR CREATED')
                    args = (user_id, id_receiver)
                    cursor.callproc('is_chat', args)
                    print('CALLPROC')
                    chat_exist = cursor.fetchall()[0][0]
                    print(chat_exist,' FETCHALL')
                    """try:
                        chat_list = list(ChatMembers.objects.filter(user_id=user_id))
                    except ObjectDoesNotExist:
                        pass#?
                    else:
                        print(chat_list,'Check')
                        for chat in chat_list:
                            try:
                                chat_settings=Chat.objects.get(chat_id=chat.chat_id)
                            except ObjectDoesNotExist:
                                return HttpResponseNotFound()
                            except MultipleObjectsReturned:
                                return HttpResponseBadRequest()
                            else:
                                if int(chat_settings.chat_members)==2:
                                    try:
                                        receiver_chat=ChatMembers.objects.get(chat_id=chat_settings.chat_id,user_id=id_receiver)
                                    except MultipleObjectsReturned:
                                        return HttpResponseBadRequest()
                                    except ObjectDoesNotExist:
                                        pass
                                    else:
                                        print(receiver_chat.chat_id,'Chaeck')
                                        chat_exist = True#break
                                        break"""
                date = []

                if chat_exist:
                    print('chat_exist')
                    args = (user_id, id_receiver)
                    cursor.callproc('chat_id', args)
                    print('CHAT_ID PROCC')
                    chat_response = int(cursor.fetchall()[0][0])
                    print(chat_response,' FETCHALL')
                    cursor.close()
                    conn.close()
                    print('CONN CLOSE AND CURSOR')
                    mess = list(Message.objects.filter(chat_id=chat_response))[:10]
                    for msg in mess:
                        date.append({"text": msg.message, "time": msg.date, 'sender_id': int(msg.user_id)})

                else:
                    cursor.close()
                    conn.close()
                    creating_chat = Chat(chat_admin=user_id, chat_members=2, is_group=False)
                    creating_chat.save()
                    connection_me = ChatMembers(chat_id=creating_chat.chat_id, user_id=user_id)
                    connection_me.save()
                    connection_receiver = ChatMembers(chat_id=creating_chat.chat_id, user_id=id_receiver)
                    connection_receiver.save()
                    chat_response = int(creating_chat.chat_id)


            return JsonResponse({'isNew': not chat_exist, 'chat_id':  chat_response, 'receiver_ava': ava, 'receiver_name': receiver_name.username, 'receiver_online': 'none', 'messages': date, 'sender_id': user_id})
    else:
        return HttpResponse("Pls ensure that you use POST method", status=405)


@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def view_chat_room(request):
    if request.method == "POST":
        user_id = int(request.session['_auth_user_id'])
        is_Group = (request.POST.get(''))
        user_chats = list(ChatMembers.objects.filter(user_id=user_id))
        time_to_id = {}
        chaters = {}
        response = []
        i = 0
        for chat in user_chats:
            try:
                messages = list(Message.objects.filter(chat_id=chat.chat_id))[0]
            except IndexError:
                last_message = ""
                last_sender_username = ""
                time_to_id[i]=chat.chat_id
            else:
                last_message = messages.message
                last_sender_username = User.objects.get(id=int(messages.user_id)).username
                time_to_id[messages.date] = chat.chat_id

            finally:
                try:#
                    chat_settings = Chat.objects.get(chat_id=int(chat.chat_id))
                except ObjectDoesNotExist:
                    return HttpResponseNotFound()
                except MultipleObjectsReturned:
                    return HttpResponseBadRequest()
                else:
                    if not bool(chat_settings.is_group):#int(chat_settings.chat_members) == 2:
                        try:
                            receiver_id = ChatMembers.objects.filter(chat_id=int(chat.chat_id)).exclude(user_id=user_id).get()
                        except ObjectDoesNotExist:
                            return HttpResponseNotFound()
                        except MultipleObjectsReturned:
                            return HttpResponseBadRequest()
                        else:
                            avatars = list(UserAvatar.objects.filter(id_user=int(receiver_id.user_id)))
                            try:
                                post = PostBase.objects.get(id=int(avatars[-1].id_post))
                            except ObjectDoesNotExist:
                                return HttpResponseNotFound()
                            except MultipleObjectsReturned:
                                return HttpResponseBadRequest()
                            except IndexError:
                                ava_src = "None"
                            else:
                                ava_src = post.img
                            finally:#it was added so it can be laged
                                try:
                                    chat_name = User.objects.get(id=int(receiver_id.user_id)).username
                                except ObjectDoesNotExist:
                                    return HttpResponseNotFound()
                                except MultipleObjectsReturned:
                                    return HttpResponseBadRequest()
                    else:
                        chat_name = chat_settings.chat_name
                        ava_src = chat_settings.chat_ava
                    chaters[chat.chat_id] = {'chat_id': chat_settings.chat_id, 'chat_name': chat_name, 'chat_ava': ava_src, 'last_message': last_message, 'last_sender': last_sender_username, 'is_group': chat_settings.is_group}
                i = i-1
        sorted_id = sorted(time_to_id.keys())
        sorted_id.reverse()
        for keys in sorted_id:
            response.append(chaters[int(time_to_id[keys])])
            del chaters[int(time_to_id[keys])]
        return JsonResponse(response, safe=False)
    else:
        return HttpResponse("Pls ensure that you use POST method", status=405)


@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def upload_messages(request):
    if request.method == "POST":
        chat_id = int(request.POST.get(['chat_id'][0], ""))
        last_id = int(request.POST.get(['id'][0], 0))
        mess = list(Message.objects.filter(chat_id=chat_id, message_id__gt=last_id))[:30]
        response = []
        for msg in mess:
            avatars = list(UserAvatar.objects.filter(id_user=int(msg.user_id)))
            try:
                sender = User.objects.get(id=msg.user_id)
                post = PostBase.objects.get(id=int(avatars[-1].id_post))
            except ObjectDoesNotExist:
                return HttpResponseNotFound()
            except MultipleObjectsReturned:
                return HttpResponseBadRequest()
            except IndexError:
                ava_src = "None"
            else:
                ava_src = post.img
            response.append({'msg_id': msg.message_id, 'user_id': msg.user_id, 'text': msg.message, 'time': int(msg.date), 'user_name': sender.username, 'user_avatar': ava_src})
        return JsonResponse({'msg_information': response})
    else:
        return HttpResponse("Pls ensure that you use POST method", status=405)


@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def create_group_chat(request):
    if request.method == "POST":
        group_name = str(request.POST.get(['group_name'][0], False))
        members_count = int(request.POST.get(['members_count'][0], False))
        members_id = (request.POST.get(['members_id'][0], False)).split()
        ava_src = str(request.POST.get(['ava_src'][0], 'nothing'))
        user_id = int(request.session['_auth_user_id'])
        print(members_id,'MEMBERS')
        if group_name and members_id and members_count and len(members_id) == members_count:
            # max_priority = int((Chat.objects.all().aggregate(Max('priority')))['priority__max'])
            group_chat = Chat(chat_name=group_name, chat_ava=ava_src, chat_admin=user_id, chat_members=members_count, is_group=True)
            group_chat.save()
            for member_id in members_id:
                chat_conn = ChatMembers(chat_id=int(group_chat.chat_id), user_id=int(member_id))
                chat_conn.save()
            return HttpResponse(int(group_chat.chat_id))
        else:
            return HttpResponse("NOT VALID DATA", status=415)
    else:
        return HttpResponse("Pls ensure that you use POST method", status=405)


@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def get_chat_members(request):
    if request.method == "POST":
        chat_id = request.POST.get(['chat_id'][0],False)
        members = ChatMembers.objects.filter(chat_id=chat_id)
        members_id = []
        for member in members:
            members_id.append(int(member.user_id))
        return JsonResponse({"chat_members": members_id})
    else:
        return HttpResponse("Pls ensure that you use POST method", status=405)



def create_chat_ws(receiver_name, user_name):
    try:
        receiver = User.objects.get(username=receiver_name)
        user = User.objects.get(username=user_name)
    except ObjectDoesNotExist:
        return HttpResponseBadRequest()
    except MultipleObjectsReturned:
        return HttpResponseBadRequest()
    else:
        receiver_id = int(receiver.id)
        user_id = int(user.id)

        conn = psycopg2.connect(dbname='d7f6m0it9u59pk', user='iffjnrmpbopayf',
                                password='20d31f747b4397c839a05d6d70d2decd02b23a689d86773a84d8dcfa23428946',
                                host='ec2-54-83-1-101.compute-1.amazonaws.com')
        print('CONNECTED TO BD')
        conn.set_isolation_level(ISOLATION_LEVEL_AUTOCOMMIT)
        cursor = conn.cursor()
        print('CURSOR CONNECTED')
        args = (user_id, receiver_id)
        cursor.callproc('is_chat', args)
        print('CALLPROC IS_CHAT2')
        chat_exist = cursor.fetchall()[0][0]
        print(chat_exist,' FETCHALL2')

        """try:
            chat_list = list(ChatMembers.objects.filter(user_id=user_id))
        except ObjectDoesNotExist:
            pass
        else:
            for chat in chat_list:
                try:
                    chat_settings = Chat.objects.get(chat_id=chat.chat_id)
                except ObjectDoesNotExist:
                    return -1
                except MultipleObjectsReturned:
                    return HttpResponseBadRequest()
                else:
                    if int(chat_settings.chat_members) == 2:
                        try:
                            receiver_chat = ChatMembers.objects.get(chat_id=chat_settings.chat_id, user_id=receiver_id)
                        except MultipleObjectsReturned:
                            return HttpResponseBadRequest()
                        except ObjectDoesNotExist:
                            pass
                        else:
                            chat_exist = True
                            break"""

        if chat_exist:
            args = (user_id, receiver_id)
            cursor.callproc('chat_id', args)
            print('CALLPROC chat_id2')
            chat_response = int(cursor.fetchall()[0][0])
            print('fetchall2')
            cursor.close()
            conn.close()
            print('ALL CLOSED')
        else:
            cursor.close()
            conn.close()
            print('ALL CLOSED2')
            creating_chat = Chat(chat_admin=user_id, chat_members=2)
            creating_chat.save()
            connection_me = ChatMembers(chat_id=creating_chat.chat_id, user_id=user_id)
            connection_me.save()
            connection_receiver = ChatMembers(chat_id=creating_chat.chat_id, user_id=receiver_id)
            connection_receiver.save()
            chat_response = int(creating_chat.chat_id)
        return chat_response


@csrf_protect
@login_required(login_url=core_url+'acc_base/login_redirection')
def add_to_group_chat(request):
    if request.method == 'POST':
        chat_id = request.POST.get(["chat_id"][0], '')
        add_users_id = request.POST.get(["users_id"][0], '').split()
        user_id = int(request.session['_auth_user_id'])
        json_error_list = {}
        is_member = False
        for add_user_id in add_users_id:
            print(add_user_id,'IDD')
            if not is_user_in_chat(chat_id, user_id):
                json_error_list[add_user_id] = 403
                continue
            try:
                test_query = ChatMembers.objects.get(user_id=add_user_id, chat_id=chat_id)
            except ObjectDoesNotExist:
                is_member = False
            except IntegrityError:
                is_member = False
            else:
                is_member = True
                json_error_list[add_user_id] = 409
            finally:
                if not is_member:
                    #Room.objects.add(chat_room, AsyncConsumer.channel_name, user=username)
                    user_obj = User.objects.get(id=add_user_id)
                    username = user_obj.username
                    new_member = ChatMembers(user_id=add_user_id, chat_id=chat_id)
                    new_member.save()
                    chat = Chat.objects.get(chat_id=chat_id)
                    chat.chat_members += 1
                    chat.save()
                    add_user_tokens = FCMDevice.objects.filter(device_id=add_user_id)
                    for token in add_user_tokens:
                        fcm_send_message(registration_id=token, data={"is_new": True, "text": f'User {username} has been added'}, body=f'User {username} has been added')
        return JsonResponse({'error_list': json_error_list})
            #AsyncConsumer.channel_layer.group_add(chat_room, AsyncConsumer.channel_name)

    else:
        return HttpResponse("Pls ensure that you use POST method", status=405)


@csrf_protect
@login_required(login_url=core_url+'acc_base/login_redirection')
def remove_from_group_chat(request):
    if request.method == 'POST':
        chat_id = request.POST.get(["chat_id"][0], '')
        add_users_id = request.POST.get(["users_id"][0], '').split()
        user_id = int(request.session['_auth_user_id'])
        json_error_list = {}
        is_exist = True
        for add_user_id in add_users_id:
            if not is_user_in_chat(chat_id, user_id):
                json_error_list[add_user_id] = 403
                continue
            try:
                delete_query = ChatMembers.objects.filter(user_id=add_user_id, chat_id=chat_id)
                delete_query.delete()
                chat = Chat.objects.get(chat_id=chat_id)
            except ObjectDoesNotExist:
                json_error_list[add_user_id] = 404
                is_exist = False
            finally:
                if is_exist:
                    chat.chat_members -= 1
                    chat.save()
        return JsonResponse({'error_list': json_error_list})
    else:
        return HttpResponse("Pls ensure that you use POST method", status=405)


@csrf_protect
@login_required(login_url=core_url+'acc_base/login_redirection')
def follower_list_for_adding(request):
    if request.method == 'GET':
        chat_id = int(request.GET.get('chat_id', ''))
        user_id = int(request.session['_auth_user_id'])
        last_user_id = int(request.GET.get('last_id', 0))
        try:
            chat_members = list(ChatMembers.objects.filter(chat_id=chat_id))
            user_followers = list((UserFollower.objects.filter(id=user_id)).filter(id__gt=last_user_id))
        except ObjectDoesNotExist:
            return HttpResponse(status=404)
        else:
            user_followers.sort()
            for follower in user_followers:
                if chat_members.count(int(follower)) == 1:
                    user_followers.remove(follower)
            return JsonResponse({"potential_members":user_followers[:10]})

    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)


def is_user_in_chat(chat_id, user_id):
    chat_members = ChatMembers.objects.filter(user_id=user_id)
    for member in chat_members:
        if int(member.chat_id) == int(chat_id):
            return True
    return False

def get_receivers_ids(chat_id):
    members = ChatMembers.objects.filter(chat_id=chat_id)
    members_id = []
    for member in members:
        members_id.append(int(member.user_id))
    return members_id


def get_user_special_tokens(user_id):

    user = FCMDevice.objects.filter(device_id=user_id)#try
    tokens = []
    for token in user:
        tokens.append(token.registration_id)
    return tokens


def get_receiver_avatar(user_id):
    try:
        receiver_avatar = list(UserAvatar.objects.filter(id_user=int(user_id)))[-1]
        img_ava = PostBase.objects.get(id=int(receiver_avatar.id_post))
        ava = img_ava.img
    except IndexError:
        ava = "None"  # null
    return ava









