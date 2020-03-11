from django.http import HttpResponse,HttpResponseRedirect
from django.contrib import auth
from django.contrib.sessions.models import Session
from rest_framework import authtoken
from .models import Users
from django.contrib.auth.models import User
from django.views.decorators.csrf import csrf_exempt,ensure_csrf_cookie
import django
from django.contrib.auth.decorators import login_required
from password_generator import PasswordGenerator
from django.core.mail import send_mail


# def setSessionHash(session):
# session_hash = session.session_key
# HttpResponse.__setitem__(header='Authorization', value=session_hash)
core_url='https://sleepy-ocean-25130.herokuapp.com/'


@csrf_exempt
def registration(request):
    if request.method == 'POST':
        username = request.POST.get(['username'][0], False)
        password = request.POST.get(['password'][0], False)
        email = request.POST.get(['email'][0], False)

        if username == False or password == False or email == False:
            return HttpResponse("NOT VALID DATA", status=415)

        user = User.objects.create_user(username=username, password=password, email=email)
        user.is_active = False
        user.save()

        url_confirm=core_url+'registration/ended?email={}'.format(user.email)
        send_mail('Verify Flex account', 'End up your registration by this url {}'.format(url_confirm),
                  'hortmagennn@gmail.com', [email], fail_silently=False, )

    # setSessionHash(request.session)
    # session_hash = request.session.session_key
    # HttpResponse.__setitem__(header='Authorization', value=session_hash)

        csrf_token = django.middleware.csrf.get_token(request)
        http_resp=HttpResponse()
        http_resp.__setitem__(header='X-CSRFToken', value=str(csrf_token))
        print('I created user!!!!!!!')

    # serialized=UserSerializer(data=request.DATA)

        return HttpResponse(http_resp)
    else:
        return HttpResponse("Pls ensure that you use POST method", status=405)


@csrf_exempt
def login(request):
    if request.method == 'GET':
        username = request.GET.get(['username'][0], False)
        password = request.GET.get(['password'][0], False)
        print(username, type(username))
        print(password, type(password))
        user = auth.authenticate(username=username, password=password)

        if username == False or password == False:
            return HttpResponse("NOT VALID DATA", status=415)

        if user is not None and user.is_active:
            auth.login(request, user)
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


def logout(request):
    if request.method == 'GET':
        auth.logout(request)
        print('I logged out!!!')
        return HttpResponse('acclogout')
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)


def verifying(request):
    if request.method == 'GET':
        user_email=request.GET.get('email', '')
        user = User.objects.get(email=user_email)
        user.is_active = True
        user.save()
        return HttpResponseRedirect('flex://main.com')
    else:
        return HttpResponse("Pls ensure that you use GET method", status=405)

# user_created_token = -1
# user_email = ''


# def forgot_pass(request): || reset_pass

    # if request.method == 'GET':
        # token = PasswordGenerator(minlen=8, maxlen=8)
        # user_created_token = token
        # email = request.GET.get(['email'][0], False)
        # user_email = email
        # if email:
            # send_mail('Change Flex Password!', 'The SECRETE code number is {}'.format(token), 'hortmagennn@gmail.com',
                      # [email], fail_silently=False)
            # HttpResponse('Message is sent')
        # else:
            # HttpResponse('Bad email', status=404)
    # if request.method == 'POST':
        # user_token = request.POST.get(['user_token'][0], False)
        # new_password=request.POST.get(['new_password'][0], False)
        # if user_token and user_token == user_created_token:
            # user = User.objects.get(email=user_email)
            # user.set_password(new_password)
            # user.save()






