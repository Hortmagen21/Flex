from django.http import HttpResponse,HttpResponseRedirect
from django.contrib import auth
from django.contrib.sessions.models import Session
from rest_framework import authtoken
from .models import TokenConfirm
from django.contrib.auth.models import User
from django.views.decorators.csrf import csrf_exempt,ensure_csrf_cookie,csrf_protect
import django
from django.contrib.auth.decorators import login_required
from password_generator import PasswordGenerator
from django.core.mail import send_mail
from django.core.exceptions import ObjectDoesNotExist
from django.urls import reverse
from django.template import loader
from django.shortcuts import render
from django.contrib.auth.decorators import login_required




