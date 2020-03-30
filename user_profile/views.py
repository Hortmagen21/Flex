from django.shortcuts import render
from user_profile.models import UserFollower,PhotoBase
from django.http import HttpResponse,HttpResponseRedirect,HttpResponseNotFound
from django.contrib.auth.decorators import login_required
from django.contrib.sessions.models import Session
from django.core.exceptions import ObjectDoesNotExist
from django.db import DatabaseError
from django.views.decorators.csrf import csrf_exempt,ensure_csrf_cookie,csrf_protect
from django.http import JsonResponse
import django
from urllib.parse import urlparse
import mimetypes

import datetime
core_url = 'https://sleepy-ocean-25130.herokuapp.com/'
test_url = 'http://127.0.0.1:8000/'


@login_required(login_url=core_url+'acc_base/login_redirection')
def follow(request):
    if request.method == 'GET':
        user_follow = request.GET.get('id', ' ')
        user_id = int(request.session['_auth_user_id'])
        try:
            user = UserFollower(id=user_follow, follower=user_id)
        except DatabaseError:
            return HttpResponse('Duplicate follow', status=409)
        else:
            if user_follow != user_id:
                user.save()
                return HttpResponse('I follow')
            else:
                return HttpResponse('I can not follow myself',status=403)
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)


@login_required(login_url=core_url+'acc_base/login_redirection')
def check_i_follow(request):
    if request.method == 'GET':
        user_id = request.GET.get('id', int(request.session['_auth_user_id']))
        user_row = list(UserFollower.objects.filter(follower=int(user_id)))
        return HttpResponse(len(user_row))
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)


@login_required(login_url=core_url + 'acc_base/login_redirection')
def followers(request):
    if request.method == 'GET':
        user_id = request.GET.get('id', int(request.session['_auth_user_id']))
        user_row = list(UserFollower.objects.filter(id=int(user_id)))
        return HttpResponse(len(user_row))
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)


@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def add_post(request):
    if request.method == "POST":
        img = request.FILES['img']
        description = request.POST.get(['description'][0], '')
        username = str(request.session['username'])
        time = datetime.datetime.today()
        #without user-> id.user
        photo = PhotoBase(username=username, day=time, img=" ", description=description)
        photo.save()
        url = "user_profile/photos/{username}_{id}.jpg".format(username=username, id=photo.id)
        with open(url, 'wb+') as destination:
            for chunk in img.chunks():
                destination.write(chunk)
        photo.img = core_url+url
        photo.save()
        response = JsonResponse({'src': core_url+url})
        return response
    else:
        return HttpResponse("Pls ensure that you use POST method", status=405)


@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def view_acc(request):
    if request.method == 'POST':
        user_id = request.POST.get(['id'][0], int(request.session['_auth_user_id']))
        if user_id == int(request.session['_auth_user_id']):
            http_resp = HttpResponse()
            http_resp.__setitem__(header='isI', value=True)
            return http_resp
        else:
            http_resp = HttpResponse()
            http_resp.__setitem__(header='isI', value=False)
            return http_resp
    else:
        return HttpResponse("Pls ensure that you use POST method", status=405)


@csrf_protect
@login_required(login_url=core_url + 'acc_base/login_redirection')
def view_photo(request):
    if request.method == 'GET':
        img = request.GET.get('img', '')
        src = urlparse(img)
        try:
            file = open(src[2][1:], 'rb+')
            mime_type_guess = mimetypes.guess_type(src[2][1:])
            print(mime_type_guess)
            if mime_type_guess is not None:
                response = HttpResponse(file, content_type=mime_type_guess[0])
            response['Content-Disposition'] = 'attachment; filename="{}"'.format(img)
        except IOError:
            response = HttpResponseNotFound()
        return response
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)
