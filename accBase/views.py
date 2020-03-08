from django.http import HttpResponse
from django.contrib import auth
from django.contrib.sessions.models import Session
from rest_framework import authtoken
from .models import Users
from django.contrib.auth.models import User
from django.views.decorators.csrf import csrf_exempt,ensure_csrf_cookie
import django
from django.contrib.auth.decorators import login_required


#def setSessionHash(session):
    #session_hash = session.session_key
    #HttpResponse.__setitem__(header='Authorization', value=session_hash)


@csrf_exempt
def registration(request):
    if request.method == 'POST':
        username = request.POST.get(['username'][0], False)
        password = request.POST.get(['password'][0], False)
        email = request.POST.get(['email'][0], False)

        if username == False or password == False or email == False:
            return HttpResponse("NOT VALID DATA", status=415)

        user = User.objects.create_user(username=username, password=password, email=email)
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
            #setSessionHash(request.session)
            #session_hash = request.session.session_key
            csrf_token = django.middleware.csrf.get_token(request)
            http_resp = HttpResponse()
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






