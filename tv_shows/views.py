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


def login_redirection(request):
    if request.method == 'GET':
        return render(request, 'login_redirect.html')


@login_required(redirect_field_name=core_url+'tv_shows/login_redirection')
def search_people(request):
    if request.method == 'GET':
        name = request.GET.get('name', '')
        user_row = list(User.objects.filter(username__contains=name))
        user_list = {}
        for user in user_row:
            user_list.update({user.username: user.id})
        return JsonResponse({"user_list": user_list}, content_type='application/json')


