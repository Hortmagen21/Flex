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
def view_duo_chat_room(request):
    if request.method == "GET":
        id_receiver = request.GET.get('id', "")
        user_id = int(request.session['_auth_user_id'])
        try:
            receiver_avatar=UserAvatar.objects.filter(id_user=int(id_receiver)).last()
            receiver_name = User.objects.get(id=int(id_receiver))
            img_ava = PostBase.objects.get(id=int(receiver_avatar.id_post))
        except ObjectDoesNotExist:
            return HttpResponseNotFound()
        except MultipleObjectsReturned:
            return HttpResponseBadRequest()
        else:
            return JsonResponse({'receiver_ava': img_ava, 'receiver_name': receiver_name, 'receiver_messages': "none",'receiver_online' : 'none'})


class Server():
    clients = set()

    async def register(self,ws:WebSocketServerProtocol)->None:
        self.clients.add(ws)
        logging.info(f'{ws.remote_address} connects.')

    async def unregister(self, ws: WebSocketServerProtocol) -> None:
        self.clients.remove(ws)
        logging.info(f'{ws.remote_address} disconnects.')

    async def send_to_clients(self,message:str)->None:
        if self.clients:
            await asyncio.wait([client.send(message) for client in self.clients])

    async def ws_handler(self, ws: WebSocketServerProtocol, url:str) -> None:
        await self.register(ws)
        try:
            await self.distribute(ws)
        finally:
            await self.unregister(ws)

    async def distribute(self,ws:WebSocketServerProtocol) -> None:
        async for message in ws:
            message=message.upper().join('ty')
            await self.send_to_clients(message)


server = Server()
start_server = websockets.serve(server.ws_handler, 'localhost', 4000)
loop = asyncio.get_event_loop()
loop.run_until_complete(start_server)
