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
    if request.method == "POST":
        id_receiver = int(request.POST.get(['id'][0], False))
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
                            chat_exist = True
            if chat_exist:
                chat_response = int(chat_settings.chat_id)
            else:
                creating_chat = Chat(chat_admin=user_id, chat_members=2)

                creating_chat.save()
                connection_me = ChatMembers(chat_id=creating_chat.chat_id, user_id=user_id)

                print(creating_chat.chat_id)
                print(user_id)
                connection_me.save()
                connection_receiver = ChatMembers(chat_id=creating_chat.chat_id, user_id=id_receiver)
                print(creating_chat.chat_id)
                print(id_receiver)
                connection_receiver.save()
                chat_response = int(creating_chat.chat_id)
                    #message dump
        return JsonResponse({'isNew': not chat_exist, 'chat_id':  chat_response, 'receiver_ava': img_ava.img, 'receiver_name': receiver_name.username, 'receiver_online': 'none'})
    else:
        return HttpResponse("Pls ensure that you use POST method", status=405)


@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def view_chat_room(request):
    if request.method == "GET":
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
                                ava_src = "none"
                            else:
                                ava_src = post.img
                    else:
                        chat_name = chat_settings.chat_name
                        ava_src = chat_settings.chat_ava

                    try:
                        chat_name = User.objects.get(id=int(receiver_id.user_id)).username
                    except ObjectDoesNotExist:
                        return HttpResponseNotFound()
                    except MultipleObjectsReturned:
                        return HttpResponseBadRequest()
                    chaters[chat.chat_id]={'chat_name': chat_name, 'chat_ava': ava_src, 'last_message': last_message, 'last_sender': last_sender_username}
                i = i-1
        sorted_id = sorted(time_to_id.keys())
        sorted_id.reverse()
        for keys in sorted_id:
            response.append(chaters[int(time_to_id[keys])])
            del chaters[int(time_to_id[keys])]
        return JsonResponse(response, safe=False)
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


