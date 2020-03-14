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

core_url='https://sleepy-ocean-25130.herokuapp.com/'


@csrf_exempt
def registration(request):
    if request.method == 'POST':
        username = request.POST.get(['username'][0], False)
        password = request.POST.get(['password'][0], False)
        email = request.POST.get(['email'][0], False)

        if username == False or password == False or email == False:
            return HttpResponse("NOT VALID DATA", status=415)

        try:
            test_user = User.objects.get(email=email)
        except ObjectDoesNotExist:
            user = User.objects.create_user(username=username, password=password, email=email)
            user.is_active = False
            user.save()

            url_confirm=core_url+'registration/ended?email={}'.format(user.email)
            send_mail('Verify Flex account', 'End up your registration by this url {}'.format(url_confirm)
                      , 'hortmagennn@gmail.com', [email], fail_silently=False, )

    # setSessionHash(request.session)
    # session_hash = request.session.session_key
    # HttpResponse.__setitem__(header='Authorization', value=session_hash)

            csrf_token = django.middleware.csrf.get_token(request)
            http_resp=HttpResponse()
            http_resp.__setitem__(header='X-CSRFToken', value=str(csrf_token))
            print('I created user!!!!!!!')

    # serialized=UserSerializer(data=request.DATA)

            return HttpResponse(http_resp)

        return HttpResponse("Such email is already exist", status=409)
    else:
        return HttpResponse("Pls ensure that you use POST method", status=405)


@csrf_exempt
def login(request):
    if request.method == 'GET':
        username = request.GET.get('username', '')
        password = request.GET.get('password', '')
        print(username, type(username))
        print(password, type(password))
        user = auth.authenticate(username=username, password=password)

        if username == False or password == False:
            return HttpResponse("NOT VALID DATA", status=415)

        if user is not None and user.is_active:
            auth.login(request, user)

            user.save()
            print(request.user.is_authenticated)
            # setSessionHash(request.session)
            # session_hash = request.session.session_key
            csrf_token = django.middleware.csrf.get_token(request)
            http_resp = HttpResponse()
            # must be rechanged on cookies
            http_resp.__setitem__(header='X-CSRFToken', value=str(csrf_token))
            print('I log in !!!!!!!')
            return HttpResponse('Successful login')
        return HttpResponse("Unsuccessful login", status=404)
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)


@csrf_protect
def logout(request):
    if request.method == 'GET':
        auth.logout(request)
        print('I logged out!!!')
        return HttpResponse('acclogout')
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)

@csrf_protect
def checklog(request):
    if request.method == 'GET':
        print(request.user.is_authenticated)
        if request.user.is_authenticated:
            return HttpResponse('good')
        else:
            return HttpResponse('bad', status=400)
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)


@csrf_exempt
def verifying(request):
    if request.method == 'GET':
        user_email = request.GET.get('email', '')
        try:
            user = User.objects.get(email=user_email)
        except ObjectDoesNotExist:
            return HttpResponse('Something go wrong with registration your acc,pls try again')
        else:
            user.is_active = True
            user.save()
            #template = loader.get_template('registration.html')
            return render(request, 'registration.html')
            #return HttpResponseRedirect('flex://main.com')
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)


@csrf_exempt
def forgot_pass(request):

    if request.method == 'GET':
        token = PasswordGenerator(minlen=8, maxlen=8)
        email = request.GET.get(['email'][0], False)
        if email:
            send_mail('Change Flex Password!', 'The SECRETE code number is {}'.format(token), 'hortmagennn@gmail.com'
                      , [email], fail_silently=False)
            try:
                user = User.objects.get(email=email)
            except ObjectDoesNotExist:
                HttpResponse('User with such email is not exist', status=404)
            else:
                try:
                    duplicate_token = TokenConfirm.objects.get(id=user.id)
                except ObjectDoesNotExist:
                    user_token = TokenConfirm(id=user.id, token=token)
                    user_token.save()
                    HttpResponse('Message is sent')
                else:
                    duplicate_token.token = token
                    duplicate_token.save()
                    HttpResponse('Message is sent')
        else:
            HttpResponse('Bad email', status=404)


@csrf_exempt
def reset_pass(request):

    if request.method == 'POST':
        user_email = request.POST.get(['email'], False)
        new_password = request.POST.get(['new_password'], False)
        user_token = request.POST.get(['user_token'][0], False)

        if user_email :
            try:
                user = User.objects.get(email=user_email)
            except ObjectDoesNotExist:
                HttpResponse('User with such email is not exist', status=404)
            else:
                try:
                    token = TokenConfirm.objects.get(id=user.id)
                except ObjectDoesNotExist:
                    HttpResponse('User with such id is not exist', status=404)
                else:
                    if user_token == token:
                        user.set_password(new_password)
                        user.save()
                        token.delete()
                    else:
                        HttpResponse('It is incorrect token', status=400)
            return HttpResponseRedirect('flex://login.com')





