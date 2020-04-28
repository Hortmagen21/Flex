from django.shortcuts import render
import websockets
from websockets import WebSocketServerProtocol
import logging#?
import asyncio#?
import django
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


@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def create_chat(request):
    if request.method == "GET":
        id_receiver = request.GET.get('id', "")
        user_id = int(request.session['_auth_user_id'])
        chat_exist = False
        try:
            receiver_avatar = list(UserAvatar.objects.filter(id_user=int(id_receiver)))[-1]
            receiver_name = User.objects.get(id=int(id_receiver))
            img_ava = PostBase.objects.get(id=int(receiver_avatar.id_post))
        except ObjectDoesNotExist:
            return HttpResponseNotFound()
        except MultipleObjectsReturned:
            return HttpResponseBadRequest()
        else:
            chat_list=list(ChatMembers.objects.filter(user_id=user_id))
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
                            chat_exist=True

            if chat_exist:
                chat_response = int(chat_settings.chat_id)
            else:
                creating_chat = Chat(chat_admin=user_id, chat_members=2)

                creating_chat.save()
                connection_me = ChatMembers(chat_id=creating_chat.chat_id, user_id=user_id)
                connection_receiver = ChatMembers(chat_id=creating_chat.chat_id, user_id=id_receiver)
                connection_me.save()
                connection_receiver.save()
                chat_response = int(creating_chat.chat_id)
                    #message dump
        return JsonResponse({'isNew': chat_exist, 'chat_id':  chat_response, 'receiver_ava': img_ava, 'receiver_name': receiver_name, 'receiver_online': 'none'})



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


