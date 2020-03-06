from django.http import HttpResponse
from django.contrib import auth
from django.contrib.sessions.models import Session
from rest_framework import authtoken
from .models import Users
from django.contrib.auth.models import User

def setSessionHash(session):
    session_hash = session.session_key
    HttpResponse.__setitem__(header='Authorization', value=session_hash)

def registration(request):
    username = request.POST.get(['username',False])
    password = request.POST.get(['password',False])
    email = request.POST.get(['email',False])
    if username==False or password==False or email==False:
        return HttpResponse("NOT VALID DATA")
    user = User.objects.create_user(username=username,password=password,email=email)
    user.save()
    setSessionHash(request.session)
    print('I created user!!!!!!!')
    #serialized=UserSerializer(data=request.DATA)
    return HttpResponse("CREATED")


def login(request):
    username = request.POST['username']
    password = request.POST['password']
    user = auth.authenticate(username=username,password=password)

    if user is not None and user.is_active:
        auth.login(request, user)
        setSessionHash(request.session)
        print('I log in !!!!!!!')
        return HttpResponse('Successful login'+user.id)
    return HttpResponse("Unsuccessful login",status=404)


def logout(request):
    auth.logout(request)
    return HttpResponse('acclogout')
