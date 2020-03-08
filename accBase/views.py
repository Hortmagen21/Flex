from django.http import HttpResponse
from django.contrib import auth
from django.contrib.sessions.models import Session
from rest_framework import authtoken
from .models import Users
from django.contrib.auth.models import User
from django.views.decorators.csrf import csrf_exempt,ensure_csrf_cookie
import django


#def setSessionHash(session):
    #session_hash = session.session_key
    #HttpResponse.__setitem__(header='Authorization', value=session_hash)


@csrf_exempt
def registration(request):
    username = request.POST.get(['username'][0], False)
    password = request.POST.get(['password'][0], False)
    email = request.POST.get(['email'][0], False)

    if username == False or password == False or email == False:
        return HttpResponse("NOT VALID DATA")

    user = User.objects.create_user(username=username,password=password,email=email)
    user.save()

    #setSessionHash(request.session)
    #session_hash = request.session.session_key
    #HttpResponse.__setitem__(header='Authorization', value=session_hash)

    csrf_token = django.middleware.csrf.get_token(request)
    http_resp=HttpResponse()
    http_resp.__setitem__(header='X-CSRFToken', value=str(csrf_token))
    print('I created user!!!!!!!')

    #serialized=UserSerializer(data=request.DATA)

    return HttpResponse(http_resp)


@csrf_exempt
def login(request):
    username = request.POST.get(['username'][0], False)
    password = request.POST.get(['password'][0], False)
    user = auth.authenticate(username=username,password=password)

    if user is not None and user.is_active:
        auth.login(request, user)
        #setSessionHash(request.session)
        #session_hash = request.session.session_key
        csrf_token = django.middleware.csrf.get_token(request)
        http_resp = HttpResponse()
        http_resp.__setitem__(header='X-CSRFToken', value=str(csrf_token))
        print('I log in !!!!!!!')
        return HttpResponse('Successful login'+user.id)
    return HttpResponse("Unsuccessful login",status=404)


@csrf_exempt
def logout(request):
    auth.logout(request)
    return HttpResponse('acclogout')


