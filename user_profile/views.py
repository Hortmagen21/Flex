from django.shortcuts import render
from user_profile.models import UserFollower
from django.http import HttpResponse,HttpResponseRedirect
from django.contrib.auth.decorators import login_required
from django.contrib.sessions.models import Session
from django.core.exceptions import ObjectDoesNotExist
from django.db import DatabaseError
from django.views.decorators.csrf import csrf_exempt,ensure_csrf_cookie,csrf_protect
import django
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


@login_required(login_url=core_url + 'acc_base/login_redirection')
def view_acc(request):
    if request.method == 'POST':
        user_id = request.POST.get('id', int(request.session['_auth_user_id']))
        if user_id == int(request.session['_auth_user_id']):
            http_resp = HttpResponse()
            csrf_token = django.middleware.csrf.get_token(request)
            http_resp.__setitem__(header='X-CSRF-TOKEN', value=str(csrf_token))
            http_resp.__setitem__(header='isI', value=True)
            return http_resp
        else:
            http_resp = HttpResponse()
            http_resp.__setitem__(header='isI', value=False)
            return http_resp
    else:
        return HttpResponse("Pls ensure that you use POST method", status=405)
