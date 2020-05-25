from django.http import HttpResponse,HttpResponseRedirect
from django.contrib import auth
from django.contrib.sessions.models import Session
from rest_framework import authtoken
from django.contrib.auth.models import User
from django.views.decorators.csrf import csrf_exempt,ensure_csrf_cookie,csrf_protect
import django
from django.contrib.auth.decorators import login_required
from django.core.mail import send_mail
from django.core.exceptions import ObjectDoesNotExist
from django.template import loader
from django.shortcuts import render
from django.http import JsonResponse
from django.contrib.auth.decorators import login_required
core_url = 'https://sleepy-ocean-25130.herokuapp.com/'
test_url = 'http://127.0.0.1:8000/'


@login_required(login_url=core_url+'acc_base/login_redirection')
def search_people(request):
    if request.method == 'GET':
        name = request.GET.get('name', '')
        user_row_start_with = list(User.objects.filter(username__istartswith=name))
        user_row_contains = list(User.objects.filter(username__icontains=name).exclude(username__istartswith=name))
        user_start_with_list = {}
        user_contains_list = {}
        for user in user_row_start_with:
            user_start_with_list.update({user.username: user.id})
        for user in user_row_contains:
            user_contains_list.update({user.username: user.id})
        return JsonResponse({"user_start_with_list": user_start_with_list, "user_contains_list": user_contains_list}, content_type='application/json')
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)

