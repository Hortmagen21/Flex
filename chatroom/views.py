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
from django.core.exceptions import MultipleObjectsReturned,ObjectDoesNotExist
from django.http import HttpResponse, HttpResponseRedirect, HttpResponseNotFound, HttpResponseBadRequest
from django.http import JsonResponse
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
                    try:
                        chat_list = list(ChatMembers.objects.filter(user_id=user_id))
                    except ObjectDoesNotExist:
                        pass#?
                    else:
                        print(chat_list,'Chaeck')
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
                                        break
                messages = []
                if chat_exist:
                    chat_response = int(chat_settings.chat_id)
                    mess = list(Message.objects.filter(chat_id=chat_response))[:10]
                    for msg in mess:
                        messages.append(msg.message)
                else:
                    creating_chat = Chat(chat_admin=user_id, chat_members=2)
                    creating_chat.save()
                    connection_me = ChatMembers(chat_id=creating_chat.chat_id, user_id=user_id)
                    connection_me.save()
                    connection_receiver = ChatMembers(chat_id=creating_chat.chat_id, user_id=id_receiver)
                    connection_receiver.save()
                    chat_response = int(creating_chat.chat_id)
            return JsonResponse({'isNew': not chat_exist, 'chat_id':  chat_response, 'receiver_ava': ava, 'receiver_name': receiver_name.username, 'receiver_online': 'none', 'messages': messages})
    else:
        return HttpResponse("Pls ensure that you use POST method", status=405)


@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def view_chat_room(request):
    if request.method == "POST":
        user_id = int(request.session['_auth_user_id'])
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
                    if int(chat_settings.chat_members) == 2:
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
                            try:
                                chat_name = User.objects.get(id=int(receiver_id.user_id)).username
                            except ObjectDoesNotExist:
                                return HttpResponseNotFound()
                            except MultipleObjectsReturned:
                                return HttpResponseBadRequest()
                    else:
                        chat_name = chat_settings.chat_name
                        ava_src = chat_settings.chat_ava
                    chaters[chat.chat_id] = {'chat_name': chat_name, 'chat_ava': ava_src, 'last_message': last_message, 'last_sender': last_sender_username}
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
        chat_id = int(request.POST.get(['id'][0], ""))
        last_id = int(request.POST.get(['id'][0], 0))
        mess = list(Message.objects.filter(chat_id=chat_id,message_id__gt=last_id))[:10]
        response = []
        for msg in mess:
            avatars = list(UserAvatar.objects.filter(id_user=int(msg.user_id)))
            try:
                sender = list(User.objects.get(id=msg.user_id))
                post = PostBase.objects.get(id=int(avatars[-1].id_post))
            except ObjectDoesNotExist:
                return HttpResponseNotFound()
            except MultipleObjectsReturned:
                return HttpResponseBadRequest()
            except IndexError:
                ava_src = "None"
            else:
                ava_src = post.img
            response.append({'messages':msg.message,'pub_data':int(msg.date),'senders_names':sender[0].username,'senders_avatars':ava_src})
        return JsonResponse({'msg_information':response})


def create_chat_ws(receiver_name, user_name):
    chat_exist = False

    try:
        receiver = User.objects.get(username=receiver_name)
        user = User.objects.get(username=user_name)
    except ObjectDoesNotExist:
        return -1
    except MultipleObjectsReturned:
        return HttpResponseBadRequest()
    else:
        receiver_id = int(receiver.id)
        user_id = int(user.id)
        try:
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
                            break#pass
                        else:
                            chat_exist = True
                            break#pass

            if chat_exist:
                chat_response = int(chat_settings.chat_id)
            else:
                creating_chat = Chat(chat_admin=user_id, chat_members=2)
                creating_chat.save()
                connection_me = ChatMembers(chat_id=creating_chat.chat_id, user_id=user_id)
                connection_me.save()
                connection_receiver = ChatMembers(chat_id=creating_chat.chat_id, user_id=receiver_id)
                connection_receiver.save()
                chat_response = int(creating_chat.chat_id)
            return chat_response
"""async def send_messages(websocket,path):
    message= await websockets.recv()
    print(f'{message}')
    await websockets.send(message)
start_server=websockets.serve(response,'localhost',1234)
asyncio.get_event_loop().run_until_complete(start_server)
asyncio.get_event_loop().run_forever()


#@csrf_protect
#@login_required(login_url=core_url + 'acc_base/login_redirection')
#def view_duo_chat_room(request):
    #if request.method == "GET":
        #id_receiver = request.GET.get('id', "")
        #user_id = int(request.session['_auth_user_id'])
        #try:
            #receiver_avatar=UserAvatar.objects.filter(id_user=int(id_receiver)).last()
            #receiver_name = User.objects.get(id=int(id_receiver))
            #img_ava = PostBase.objects.get(id=int(receiver_avatar.id_post))
        #except ObjectDoesNotExist:
            #return HttpResponseNotFound()
        #except MultipleObjectsReturned:
            #return HttpResponseBadRequest()
        #else:
            #return JsonResponse({'receiver_ava': img_ava, 'receiver_name': receiver_name, 'receiver_messages': "none",'receiver_online' : 'none'})

"""
